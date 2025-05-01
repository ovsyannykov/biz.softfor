package biz.softfor.spring.jpa.crud;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.jpa.crud.querygraph.PredicateProvider;
import biz.softfor.spring.jpa.crud.assets.PartnersTestBasic;
import biz.softfor.spring.jpa.crud.assets.TestPartners;
import biz.softfor.spring.jpa.crud.assets.UpdateParams;
import static biz.softfor.spring.jpa.crud.assets.UpdateParams.BY_ID;
import static biz.softfor.spring.jpa.crud.assets.UpdateParams.NOP;
import biz.softfor.testutil.jpa.TestEntities;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.RoleRequest;
import biz.softfor.user.jpa.Role_;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroupRequest;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.jpa.UserRequest;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.jpa.User_;
import biz.softfor.util.Json;
import biz.softfor.util.StringUtil;
import static biz.softfor.util.StringUtil.field;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.Identifiable;
import biz.softfor.spring.sqllog.SqlCountValidator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.list;
import static org.assertj.core.util.Sets.set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
@Log
public class ManyToManyTest extends PartnersTestBasic {

  @Test
  public void createWithEmptyData() throws Exception {
    Class<? extends Exception> EXPECTED_EXCEPTION = IllegalArgumentException.class;
    UserRequest.Create request = new UserRequest.Create();
    assertThatThrownBy(() -> userSvc.create(request))
    .as(() -> "Empty request data should throw the " + EXPECTED_EXCEPTION.getName())
    .isInstanceOf(EXPECTED_EXCEPTION);
  }

  public static Stream<Set<Integer>> create() {
    List<Set<Integer>> p = new ArrayList<>();
    p.add(null);
    p.add(set());
    p.add(set(1, 2));
    return p.stream()
    //.skip(2).limit(1)
    ;
  }

  @ParameterizedTest
  @MethodSource
  public void create(Set<Integer> relIndexes, CapturedOutput output) throws Exception {
    String CREATE_I = "";
    UserRequest.Create request = new UserRequest.Create();
    request.data = TestPartners.newUserWor.apply(LABEL, CREATE_I);
    if(relIndexes != null) {
      request.data.setGroupIds(data.groups.ids(relIndexes));
    }
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<UserWor> response = userSvc.create(request);
    Long userId = response.getData(0).getId();
    jpaCheck.create("response", response);
    int inserts = CollectionUtils.isEmpty(request.data.getGroupIds()) ? 1 : 2;
    validator.insert(inserts).assertTotal();
    if(CollectionUtils.size(relIndexes) == 2) {
      List<Integer> groupIds = new ArrayList<>(CollectionUtils.size(relIndexes));
      Set<Integer> gIds = response.getData(0).getGroupIds();
      for(Integer gId : gIds) {
        groupIds.add(gId);
      }
      assertThat(output).as("Output log").containsIgnoringWhitespaces
      ("BatchSize:2, Query:[\"insert into users_groups(groupId, userId) values(?, ?)\"], Params:[("
      + groupIds.get(0) + "," + userId + "),(" + groupIds.get(1) + "," + userId + ")]");
    }
    User newData = TestPartners.newUser.apply(LABEL, CREATE_I);
    newData.setId(userId);
    newData.setGroups(relIndexes == null ? new HashSet<>() : data.groups.data(relIndexes));
    data.users.data.add(newData);
    data.users.check(data.users.allIdxs(), data.userIgnoringFields, User_.GROUPS);
  }

  @Test
  public void createWithBadInverseJoinColumn() throws Exception {
    Class<?> EXPECTED_EXCEPTION = jakarta.persistence.PersistenceException.class;
    String CREATE_I = "";
    Integer BAD_INVERSE_JOIN_ID = -31416;
    UserRequest.Create request = new UserRequest.Create();
    request.data = TestPartners.newUserWor.apply(LABEL, CREATE_I);
    request.data.setGroupIds(set(
      data.groups.data.get(1).getId()
    , BAD_INVERSE_JOIN_ID
    , data.groups.data.get(2).getId()
    ));
    assertThatThrownBy(() -> {
      CommonResponse<UserWor> response = userSvc.create(request);
      //this is necessary as the rollback is called after the test is completed
      jpaCheck.create("response", response);
      User newData = TestPartners.newUser.apply(LABEL, CREATE_I);
      newData.setId(response.getData(0).getId());
      data.users.data.add(newData);
      em.flush();
    })
    .as(() -> "Invalid roleId=" + BAD_INVERSE_JOIN_ID
    + " should throw the " + EXPECTED_EXCEPTION.getName())
    .isInstanceOf(EXPECTED_EXCEPTION);
  }

  public final static String READ = "biz.softfor.spring.jpa.crud.ManyToManyTest#read";

  public static Stream<List<String>> read() {
    List<List<String>> p = new ArrayList<>();
    p.add(null);
    p.add(list());
    p.add(list(User_.GROUPS, User_.ID, User_.USERNAME));
    p.add(list(field(User_.GROUPS, UserGroup_.NAME)));
    p.add(list(field(User_.GROUPS, UserGroup_.ROLES, Role_.OBJ_NAME)));
    return p.stream()
    //.skip(2).limit(1)
    ;
  }

  @ParameterizedTest
  @MethodSource
  public void read(List<String> fields) throws Exception {
    int idx = 2;
    log.info(() -> "=".repeat(32) + "\nfields=" + fields + ", idx=" + idx);
    User sample = data.users.data.get(idx);
    User expected = ColumnDescr.copyByFields(sample, User.class, fields);
    TestPartners.collectionsEmpty2null(expected);
    UserRequest.Read request = new UserRequest.Read();
    request.filter.assignId(sample.getId());
    request.fields = fields;
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<User> response = userSvc.read(request);
    if(CollectionUtils.isNotEmpty(response.getData())) {
      TestPartners.collectionsEmpty2null(response.getData(0));
    }
    log.info(() -> "response=" + Json.serializep(om, response));
    log.info(() -> "expected=" + Json.serializep(om, expected));
    jpaCheck.resultData("response", response, expected
    , field(User_.GROUPS, UserGroup_.USERS)
    , field(User_.GROUPS, UserGroup_.ROLES, Role_.GROUPS)
    );
    int selectCount = 1;
    if(CollectionUtils.isNotEmpty(fields)) {
      for(String f : fields) {
        String[] fa = f.split(StringUtil.FIELDS_DELIMITER_REGEX);
        if(fa.length >= 2 && User_.GROUPS.equals(fa[0]) && UserGroup_.ROLES.equals(fa[1])) {
          selectCount = 3;
          break;
        }
        if(fa.length >= 1 && User_.GROUPS.equals(fa[0])) {
          selectCount = 2;
          break;
        }
      }
    }
    validator.select(selectCount).assertTotal();
  }

  @Test
  public void readRole() throws Exception {
    List<String> fields = list(field(Role_.GROUPS, UserGroup_.NAME), Role_.NAME);
    int idx = 2;
    log.info(() -> "=".repeat(32) + "\nfields=" + fields + ", idx=" + idx);
    Role sample = data.roles.data.get(idx);
    Role expected = ColumnDescr.copyByFields(sample, Role.class, fields);
    TestPartners.collectionsEmpty2null(expected);
    RoleRequest.Read request = new RoleRequest.Read();
    request.filter.assignId(sample.getId());
    request.fields = fields;
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<Role> response = roleSvc.read(request);
    if(CollectionUtils.isNotEmpty(response.getData())) {
      TestPartners.collectionsEmpty2null(response.getData(0));
    }
    log.info(() -> "response=" + Json.serializep(om, response));
    log.info(() -> "expected=" + Json.serializep(om, expected));
    jpaCheck.resultData("response", response, expected
    , field(Role_.GROUPS, UserGroup_.ROLES)
    , field(Role_.GROUPS, UserGroup_.USERS, User_.GROUPS)
    );
    validator.select(2).assertTotal();
  }

  @Test
  public void readGroup() throws Exception {
    List<String> fields = list(field(UserGroup_.USERS, User_.USERNAME), UserGroup_.NAME);
    int idx = 2;
    log.info(() -> "=".repeat(32) + "\nfields=" + fields + ", idx=" + idx);
    UserGroup sample = data.groups.data.get(idx);
    UserGroup expected = ColumnDescr.copyByFields(sample, UserGroup.class, fields);
    TestPartners.collectionsEmpty2null(expected);
    UserGroupRequest.Read request = new UserGroupRequest.Read();
    request.filter.assignId(sample.getId());
    request.fields = fields;
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<UserGroup> response = userGroupSvc.read(request);
    if(CollectionUtils.isNotEmpty(response.getData())) {
      TestPartners.collectionsEmpty2null(response.getData(0));
    }
    log.info(() -> "response=" + Json.serializep(om, response));
    log.info(() -> "expected=" + Json.serializep(om, expected));
    jpaCheck.resultData("response", response, expected
    , field(UserGroup_.ROLES)
    , field(UserGroup_.USERS, User_.GROUPS)
    );
    validator.select(2).assertTotal();
  }

  public static Stream<UpdateParams> update() {
    BiFunction<Object, String, Object> UPDATE
    = (e, label) -> UpdateParams.UPDATE.apply(User_.PASSWORD, e, label);
    List<UpdateParams> p = new ArrayList<>();
    p.add(new UpdateParams(
      "without changes"
    , TestEntities.allIdxs(DATA_SIZE), null, NOP, 0
    , SqlCountValidator.builder()
    ));
    p.add(new UpdateParams(
      "update 2"
    , TestEntities.allIdxs(DATA_SIZE), null, UPDATE, 4
    , SqlCountValidator.builder().update(1)
    ));
    p.add(new UpdateParams(
      "update 3"
    , TestEntities.allIdxs(DATA_SIZE), list(1, 0), NOP, 12
    , SqlCountValidator.builder().select(1).delete(1).insert(1)
    ));
    p.add(new UpdateParams(
      "update 4"
    , TestEntities.allIdxs(DATA_SIZE), list(1, 0), UPDATE, 16
    , SqlCountValidator.builder().update(1).select(1).delete(1).insert(1)
    ));
    p.add(new UpdateParams(
      "update 5"
    , list(0), list(1, 0), UPDATE, 3
    , SqlCountValidator.builder().update(1).select(1).insert(1)
    ));
    p.add(new UpdateParams(
      "update 6"
    , list(1), list(1, 2), UPDATE, 2
      , SqlCountValidator.builder().update(1).select(1).delete(1)
    ));
    p.add(new UpdateParams(
      "with the unchanged roleIds"
    , list(1), list(1, 2, 3), UPDATE, 1
    , SqlCountValidator.builder().update(1).select(1)
    ));
    p.add(new UpdateParams(
      "update 8"
    , list(1), list(1, 0), UPDATE, 4
    , SqlCountValidator.builder().update(1).select(1).delete(1).insert(1)
    ));
    p.add(new UpdateParams(
      "update 9"
    , list(1), list(), UPDATE, 4
    , SqlCountValidator.builder().update(1).delete(1)
    ));
    p.add(new UpdateParams(
      "with updToNull for ManyToMany (User.roles)"
    , list(1), null, NOP, list(UserWor.GROUP_IDS), BY_ID, 3
    , SqlCountValidator.builder().delete(1)
    ));
    return p.stream()
    //.skip(0).limit(1)
    ;
  }

  @ParameterizedTest
  @MethodSource
  public void update(UpdateParams params) {
    log.info(() -> {
      String updToNullInfo = CollectionUtils.isEmpty(params.fields)
      ? "" : "\nupdToNull=" + params.fields;
      return "=".repeat(32) + "\n" + params.description
      + "\nuserIds=" + data.users.ids(params.idxs)
      + "\ngroupIds=" + data.groups.ids(params.joinIdxs)
      + updToNullInfo;
    });
    UserRequest.Update request = new UserRequest.Update();
    Set<UserGroup> reassignableRoles = data.groups.data(params.joinIdxs);
    for(int i = 0; i < data.users.data.size(); ++i) {
      User d = data.users.data.get(i);
      if(params.idxs.contains(i)) {
        params.updater.apply(d, LABEL);
        if(reassignableRoles != null
        || (
            params.fields != null
            && params.fields.contains(UserWor.GROUP_IDS)
          )
        ) {
          d.setGroups(reassignableRoles == null ? set() : reassignableRoles);
        }
      }
    }
    request.data = (UserWor)params.updater.apply(new UserWor(), LABEL);
    request.data.setGroupIds(Identifiable.idSet(reassignableRoles));
    request.fields = params.fields;
    params.filter.accept(request.filter, params.idxs, data.users);
    SqlCountValidator validator = params.validatorBldr.entityManager(em).build();
    CommonResponse<User> response = userSvc.update(request);
    jpaCheck.update("response", response, params.total);
    validator.assertTotal();
    data.users.check(params.idxs, data.userIgnoringFields
    , TestPartners.USER_FETCH_RELATIONS);
  }

  @Test
  public void updateWithBadInverseJoinColumn() throws Exception {
    Class<?> exClazz = jakarta.persistence.PersistenceException.class;
    Long id = data.users.data.get(0).getId();
    UserRequest.Update request = new UserRequest.Update();
    request.filter.assignId(id);
    request.data = TestPartners.newUserWor.apply(LABEL, "");
    request.data.setGroupIds(set(-1));
    SqlCountValidator validator = new SqlCountValidator(em);
    assertThatThrownBy(() -> { userSvc.update(request); em.flush(); })
    .as(() -> "Invalid roleId=" + request.data.getGroupIds()
    + " should throw the " + exClazz.getName())
    .isInstanceOf(exClazz);
    /*//don't work because rollback is called after the method ends
    validator.update(1).select(1).insert(1).assertTotal();
    Holder<User> holder = new Holder<>();
    new TransactionTemplate(tm).executeWithoutResult(
      status -> holder.value = em.find(User.class, id)
    );
    assertThat(holder.value).as(() -> "User(id=" + id + ") must not exist.")
    .isNull();
    */
  }

  @Test
  public void updateByEmptyFilter() {
    UserRequest.Update request = new UserRequest.Update();
    request.data = new UserWor();
    request.data.setEmail(LABEL + "@t.co");
    SqlCountValidator validator = new SqlCountValidator(em);
    assertThatThrownBy(() -> userSvc.update(request))
    .isInstanceOf(ClientError.class)
    .hasMessage(MessageFormat.format(
      PredicateProvider.The_filter_must_be_not_empty
    , PredicateProvider.DELETE
    ));
    validator.assertTotal();
  }

}
