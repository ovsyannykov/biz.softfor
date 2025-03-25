package biz.softfor.spring.jpa.crud;

import biz.softfor.spring.jpa.crud.assets.PartnersTestBasic;
import biz.softfor.spring.jpa.crud.assets.TestPartners;
import biz.softfor.spring.jpa.crud.assets.UpdateParams;
import static biz.softfor.spring.jpa.crud.assets.UpdateParams.BY_AND;
import static biz.softfor.spring.jpa.crud.assets.UpdateParams.BY_ID;
import static biz.softfor.spring.jpa.crud.assets.UpdateParams.NOP;
import biz.softfor.jpa.crud.AbstractCrudSvc;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.Contact_;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerRequest;
import biz.softfor.partner.jpa.PartnerWor;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.spring.sqllog.SqlCountValidator;
import biz.softfor.testutil.jpa.TestEntities;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.User_;
import biz.softfor.util.Json;
import biz.softfor.util.StringUtil;
import static biz.softfor.util.StringUtil.field;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ServerError;
import java.beans.IntrospectionException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.list;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Log
public class OneToManyTest extends PartnersTestBasic {

  final static String CREATE = "biz.softfor.spring.jpa.crud.OneToManyTest#create";

  public static Stream<Set<Integer>> create() {
    List<Set<Integer>> p = new ArrayList<>();
    p.add(null);
    p.add(Set.of());
    p.add(Set.of(0, 1));
    return p.stream()
    //.skip(2).limit(1)
    ;
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("@OneToMany(mappedBy = ")
  public void create(Set<Integer> relIndexes)
  throws ReflectiveOperationException, IntrospectionException {
    int CREATE_I = 0;
    PartnerRequest.Create request = new PartnerRequest.Create();
    request.data = TestPartners.newPartnerWor.apply(LABEL, CREATE_I);
    if(relIndexes != null) {
      request.data.setUserIds(data.users.idList(relIndexes));
    }
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<PartnerWor> response = partnerSvc.create(request);
    jpaCheck.create("response", response);
    validator.insert(1)
    .update(CollectionUtils.isEmpty(request.data.getUserIds()) ? 0 : 1)
    .assertTotal();
    Partner newData = TestPartners.newPartner.apply(LABEL, CREATE_I);
    newData.setId(response.getData(0).getId());
    data.partners.data.add(newData);
    if(CollectionUtils.isNotEmpty(relIndexes)) {
      Set<User> users = data.users.data(relIndexes);
      for(Partner p : data.partners.data) {
        p.removeUsers(users);
      }
      newData.setUsers(new ArrayList<>());
      for(User d : data.users.data(relIndexes)) {
        newData.addUser(d);
      }
    }
    data.partners.check(data.partners.allIdxs(), data.partnerIgnoringFields
    , TestPartners.PARTNER_FETCH_RELATIONS);
  }

  @ParameterizedTest
  @MethodSource(CREATE)
  @DisplayName("@JoinColumn(name = ")
  public void create2(Set<Integer> relIndexes)
  throws ReflectiveOperationException, IntrospectionException {
    int CREATE_I = 0;
    PartnerRequest.Create request = new PartnerRequest.Create();
    request.data = TestPartners.newPartnerWor.apply(LABEL, CREATE_I);
    if(relIndexes != null) {
      request.data.setContactIds(data.contacts.idList(relIndexes));
    }
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<PartnerWor> response = partnerSvc.create(request);
    jpaCheck.create("response", response);
    validator.insert(1)
    .update(CollectionUtils.isEmpty(request.data.getContactIds()) ? 0 : 1)
    .assertTotal();
    Partner newData = TestPartners.newPartner.apply(LABEL, CREATE_I);
    newData.setId(response.getData(0).getId());
    data.partners.data.add(newData);
    if(CollectionUtils.isNotEmpty(relIndexes)) {
      Set<Contact> contacts = data.contacts.data(relIndexes);
      for(Partner p : data.partners.data) {
        p.removeContacts(contacts);
      }
      newData.setContacts(new ArrayList<>());
      for(Contact d : data.contacts.data(relIndexes)) {
        newData.addContact(d);
      }
    }
    data.partners.check(data.partners.allIdxs(), data.partnerIgnoringFields
    , TestPartners.PARTNER_FETCH_RELATIONS);
  }

  @Test
  public void createWithBadJoinColumn() throws ReflectiveOperationException {
    Class<?> EXPECTED_EXCEPTION = ServerError.class;
    int CREATE_I = 0;
    Long BAD_JOIN_ID = -31416L;
    List<Long> userIds = list(
      data.users.data.get(1).getId()
    , BAD_JOIN_ID
    , data.users.data.get(2).getId()
    );
    int userIdsSize = userIds.size();
    PartnerRequest.Create request = new PartnerRequest.Create();
    request.data = TestPartners.newPartnerWor.apply(LABEL, CREATE_I);
    request.data.setUserIds(userIds);
    assertThatThrownBy(() -> partnerSvc.create(request))
    .as(() -> "Invalid userId=" + BAD_JOIN_ID + " should throw the "
    + EXPECTED_EXCEPTION.getName())
    .isInstanceOf(EXPECTED_EXCEPTION)
    .hasMessage(MessageFormat.format(CrudSvc.X_TO_MANY_UPDATE_ERROR, userIdsSize
    , userIdsSize - 1, User.class.getName(), userIds.toString()));
  }

  public static Stream<List<String>> read() {
    List<List<String>> p = new ArrayList<>();
    p.add(null);
    p.add(list());
    p.add(list(Partner_.USERS, field(Partner_.USERS, User_.GROUPS)));
    p.add(list(field(Partner_.USERS, User_.GROUPS), Partner_.USERS));
    p.add(list(field(Partner_.CONTACTS, Contact_.DESCR)
    , Partner_.USERS, Partner_.PARTNER_FULLNAME));
    return p.stream()
    //.skip(2).limit(1)
    ;
  }

  @ParameterizedTest
  @MethodSource
  public void read(List<String> fields) throws Exception {
    log.info(() -> "=".repeat(32) + "\nread by fields="
    + Json.serializep(om, fields));
    Partner sample = data.partners.data.get(1);
    Partner expected = ColumnDescr.copyByFields(sample, Partner.class, fields);
    TestPartners.collectionsEmpty2null(expected);
    int selectUsers = 0;
    int selectRoles = 0;
    int selectContacts = 0;
    if(CollectionUtils.isNotEmpty(fields)) {
      for(String f : fields) {
        String[] fa = f.split(StringUtil.FIELDS_DELIMITER_REGEX);
        if(fa.length > 0) {
          if(Partner_.USERS.equals(fa[0])) {
            selectUsers = 1;
          }
          if(Partner_.CONTACTS.equals(fa[0])) {
            selectContacts = 1;
          }
          if(fa.length > 1) {
            if(User_.GROUPS.equals(fa[1])) {
              selectRoles = 1;
            }
          }
        }
      }
    }
    PartnerRequest.Read request = new PartnerRequest.Read();
    request.filter.assignId(sample.getId());
    request.fields = fields;
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<Partner> response = partnerSvc.read(request);
    if(CollectionUtils.isNotEmpty(response.getData())) {
      TestPartners.collectionsEmpty2null(response.getData(0));
    }
    jpaCheck.resultData("response", response, expected
    , field(Partner_.CONTACTS, Contact_.PARTNER));
    validator.select(1 + selectUsers + selectRoles + selectContacts)
    .assertTotal();
  }

  private final static BiFunction<Object, String, Object> UPDATE
  = (e, label) -> UpdateParams.UPDATE.apply(Partner_.ADDRESS, e, label);

  public static Stream<UpdateParams> update() {
    List<UpdateParams> p = new ArrayList<>();
    p.add(new UpdateParams(
      "no updates"
    , TestEntities.allIdxs(DATA_SIZE), null, NOP, 0
    , SqlCountValidator.builder()
    ));//0
    p.add(new UpdateParams(
      "record only"
    , TestEntities.allIdxs(DATA_SIZE), null, UPDATE, 4
    , SqlCountValidator.builder().update(1)
    ));//1
    p.add(new UpdateParams(
      "1st record with empty relations"
    , list(1), list(), UPDATE, 7
    , SqlCountValidator.builder().update(2).delete(2)
    ));//2
    p.add(new UpdateParams(
      "many records with empty relations"
    , TestEntities.allIdxs(DATA_SIZE), list(), UPDATE, 16
    , SqlCountValidator.builder().update(2).delete(2)
    ));//3
    p.add(new UpdateParams(
      "1st record with nulled relations"
    , list(1), null, UPDATE
    , list(PartnerWor.CONTACT_IDS, PartnerWor.USER_IDS), BY_ID, 7
    , SqlCountValidator.builder().update(2).delete(2)
    ));//4
    p.add(new UpdateParams(
      "many records with nulled relations"
    , TestEntities.allIdxs(DATA_SIZE), null, UPDATE
    , list(PartnerWor.CONTACT_IDS, PartnerWor.USER_IDS), BY_ID, 16
    , SqlCountValidator.builder().update(2).delete(2)
    ));//5
    p.add(new UpdateParams(
      "one record with relations(0, 1)"
    , list(0), list(0, 1), UPDATE, 5
    , SqlCountValidator.builder().update(4).delete(2)
    ));//6
    p.add(new UpdateParams(
      "1st record with relations(0, 1)"
    , list(1), list(0, 1), UPDATE, 7
    , SqlCountValidator.builder().update(4).delete(2)
    ));//7
    p.add(new UpdateParams(
      "1st record with relations(1, 2)"
    , list(1), list(1, 2), UPDATE, 9
    , SqlCountValidator.builder().update(4).delete(2)
    ));//8
    p.add(new UpdateParams(
      "1st record with relations(1, 2, 3)"
    , list(1), list(1, 2, 3), UPDATE, 11
    , SqlCountValidator.builder().update(4).delete(2)
    ));//9
    p.add(new UpdateParams(
      "1st record with empty relations"
    , list(1), list(), NOP, 6
    , SqlCountValidator.builder().select(1).update(1).delete(2)
    ));//10
    p.add(new UpdateParams(
      "no update 1st record with relations(1, 2)"
    , list(1), list(1, 2), NOP, 8
    , SqlCountValidator.builder().select(1).update(3).delete(2)
    ));//11
    p.add(new UpdateParams(
      "no update zero record with empty relations by BY_AND filter"
    , list(0), list(), NOP, BY_AND, 0
    , SqlCountValidator.builder().select(1).update(1).delete(2)
    ));//12
    p.add(new UpdateParams(
      "no update 1st record with empty relations by BY_AND filter"
    , list(1), list(), NOP, BY_AND, 6
    , SqlCountValidator.builder().select(1).update(1).delete(2)
    ));//13
    p.add(new UpdateParams(
      "no update records(1, 2) with empty relations by BY_AND filter"
    , list(1, 2), list(), NOP, BY_AND, 9
    , SqlCountValidator.builder().select(1).update(1).delete(2)
    ));//14
    p.add(new UpdateParams(
      "no update 1st record with relations(1, 2) by BY_AND filter"
    , list(1), list(1, 2), NOP, BY_AND, 8
    , SqlCountValidator.builder().select(1).update(3).delete(2)
    ));//15
    return p.stream()
    //.skip(10).limit(1)
    ;
  }

  @ParameterizedTest
  @MethodSource
  public void update(UpdateParams params) {
    log.info(() -> "=".repeat(32) + "\n" + params.description);
    PartnerRequest.Update request = new PartnerRequest.Update();
    request.data = requestData4Update(params, LABEL);
    request.fields = params.fields;
    params.filter.accept(request.filter, params.idxs, data.partners);
    SqlCountValidator validator = params.validatorBldr.entityManager(em).build();
    CommonResponse<Partner> response = partnerSvc.update(request);
    jpaCheck.update("response", response, params.total);
    validator.assertTotal();
    data.partners.check(params.idxs, data.partnerIgnoringFields
    , TestPartners.PARTNER_FETCH_RELATIONS);
  }

  public static Stream<UpdateParams> updateMultipleWithJoinedIds() {
    List<UpdateParams> p = new ArrayList<>();
    p.add(new UpdateParams(
      "by BY_ID filter"
    , list(1, 2), list(1, 2), NOP, BY_ID, 0
    , SqlCountValidator.builder().select(1)
    ));
    p.add(new UpdateParams(
      "by BY_AND filter"
    , list(1, 2), list(1, 2), NOP, BY_AND, 0
    , SqlCountValidator.builder().select(1)
    ));
    return p.stream()
    //.skip(0).limit(1)
    ;
  }

  @ParameterizedTest
  @MethodSource
  public void updateMultipleWithJoinedIds(UpdateParams params) {
    log.info(() -> "=".repeat(32) + "\n" + params.description);
    PartnerRequest.Update request = new PartnerRequest.Update();
    request.data = requestData4Update(params, LABEL);
    params.filter.accept(request.filter, params.idxs, data.partners);
    SqlCountValidator validator = params.validatorBldr.entityManager(em).build();
    assertThatThrownBy(() -> partnerSvc.update(request))
    .isInstanceOf(biz.softfor.util.api.ClientError.class)
    .message().isIn(
      MessageFormat.format(
        AbstractCrudSvc.Update_multiple_records_with_OneToMany_items
      , params.idxs.size(), PartnerWor.CONTACT_IDS
      )
    , MessageFormat.format(
        AbstractCrudSvc.Update_multiple_records_with_OneToMany_items
      , params.idxs.size(), PartnerWor.USER_IDS
      )
    );
    validator.assertTotal();
  }

  private PartnerWor requestData4Update(UpdateParams params, String label) {
    log.info(() -> "\nids=" + data.partners.ids(params.idxs)
    + "\nuserIds=" + data.users.ids(params.joinIdxs)
    + "\ncontactIds=" + data.contacts.ids(params.joinIdxs));
    List<User> reassignableUsers = data.users.list(params.joinIdxs);
    if(reassignableUsers == null) {
      reassignableUsers = list();
    }
    List<Contact> reassignableContacts = data.contacts.list(params.joinIdxs);
    if(reassignableContacts == null) {
      reassignableContacts = list();
    }
    for(int i = 0; i < data.partners.data.size(); ++i) {
      Partner partner = data.partners.data.get(i);
      if(params.idxs == null || params.idxs.contains(i)) {
        params.updater.apply(partner, label);
        if(params.joinIdxs != null
        || params.fields != null && params.fields.contains(PartnerWor.USER_IDS)) {
          partner.setUsers(reassignableUsers);
          partner.setContacts(reassignableContacts);
        }
      }
    }
    PartnerWor result = (PartnerWor)params.updater.apply(new PartnerWor(), label);
    if(params.joinIdxs != null) {
      result.setUserIds(Identifiable.idList(reassignableUsers));
      result.setContactIds(Identifiable.idList(reassignableContacts));
    }
    return result;
  }

}
