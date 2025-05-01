package biz.softfor.spring.jpa.crud;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.partner.api.PartnerFltr;
import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.ContactRequest;
import biz.softfor.partner.jpa.Contact_;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerRequest;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.spring.jpa.crud.assets.PartnersTestBasic;
import biz.softfor.spring.jpa.crud.assets.TestPartners;
import biz.softfor.spring.sqllog.SqlCountValidator;
import biz.softfor.user.api.RoleFltr;
import biz.softfor.user.api.UserFltr;
import biz.softfor.user.api.UserGroupFltr;
import biz.softfor.user.jpa.Role_;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.jpa.UserRequest;
import biz.softfor.user.jpa.User_;
import biz.softfor.util.Json;
import biz.softfor.util.Range;
import static biz.softfor.util.StringUtil.field;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.Value;
import java.beans.IntrospectionException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import static org.assertj.core.util.Sets.set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
@Log
public class FilterTest extends PartnersTestBasic {

  private void read
  (UserRequest.Read request, Set<User> expected, int selectCount)
  throws IntrospectionException, ReflectiveOperationException {
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<User> response = userSvc.read(request);
    log.info(() -> "expected=" + Json.serializep(om, expected));
    jpaCheck.resultData("response", response, expected, UserFltr.GROUPS);
    validator.select(selectCount).assertTotal();
  }

  @Test
  public void id() throws Exception {
    Set<User> expected = data.users.data(1, 2);
    UserRequest.Read request = new UserRequest.Read();
    request.filter.setId(Identifiable.ids(expected));
    read(request, expected, 1);
  }

  @Test
  public void varcharWithLike() throws Exception {
    int INDEX = 2;
    Set<User> expected = data.users.data(INDEX);
    UserRequest.Read request = new UserRequest.Read();
    request.filter.setUsername("%" + LABEL.substring(3, LABEL.length() - 3)
    + "%" + INDEX);
    read(request, expected, 1);
  }

  @Test
  public void date(CapturedOutput output) throws Exception {
    Set<Partner> expected = data.partners.data(1, 2);
    PartnerRequest.Read request = new PartnerRequest.Read();
    request.fields = list(Partner_.ID, Partner_.PARTNER_REGDATE, Partner_.TYP);
    request.filter.setPartnerRegdate
    (new Range<>(LocalDate.of(2022, 1, 21), LocalDate.of(2022, 1, 23)));
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<Partner> response = partnerSvc.read(request);
    log.info(() -> "expected=" + Json.serializep(om, expected));
    jpaCheck.resultData("response", response, expected
    , Partner_.ADDRESS, Partner_.CONTACTS, Partner_.PARTNER_FULLNAME
    , Partner_.LOCATION_TYPE, Partner_.PARTNER_NAME, Partner_.PARENT
    , Partner_.PARTNER_DETAILS, Partner_.PARTNER_FILES, Partner_.PERSON_DETAILS
    , Partner_.POSTCODE, Partner_.PARTNER_REGCODE, Partner_.USERS
    );
    validator.select(1).assertTotal();
    assertThat(output).contains(
      "where p1_0.partnerRegdate>='2022-01-21T00:00:00.000"
    , "and p1_0.partnerRegdate<'2022-01-23T00:00:00.000"
    );
  }

  @Test
  public void nestedField(CapturedOutput output) throws Exception {
    int dataIdx = 2;
    String EXPECTED_SQL =
    "from users u1_0 join users_groups g1_0 on u1_0.id=g1_0.userId join userGroups g1_1 on g1_1.id=g1_0.groupId join roles_groups r1_0 on g1_1.id=r1_0.groupId where r1_0.roleId="
    + data.roles.data.get(dataIdx).getId();
    UserRequest.Read request = new UserRequest.Read();
    UserGroupFltr userGroupFltr = new UserGroupFltr();
    RoleFltr role = new RoleFltr();
    role.setId(data.roles.idList(dataIdx));
    userGroupFltr.setRoles(role);
    request.filter.setGroups(userGroupFltr);
    request.fields = list(
      User_.ID
    , User_.USERNAME
    , field(User_.GROUPS, UserGroup_.ROLES, Role_.ID)
    );
    Set<User> expected = new HashSet<>();
    for(User d : data.users.data(1, 2, 3)) {
      User e = ColumnDescr.copyByFields(d, User.class, request.fields);
      TestPartners.collectionsEmpty2null(e);
      expected.add(e);
    }
    read(request, expected, 3);
    assertThat(output).contains(EXPECTED_SQL);
  }

  @Test
  public void nestedField_m2o_o2m(CapturedOutput output) throws Exception {
    Set<Integer> dataIdxs = set(0, 1);
    List<Long> dataIds = data.users.idList(dataIdxs);
    ContactRequest.Read request = new ContactRequest.Read();
    UserFltr userFltr = new UserFltr();
    userFltr.setId(dataIds);
    PartnerFltr partnerFltr = new PartnerFltr();
    partnerFltr.setUsers(userFltr);
    request.filter.setPartner(partnerFltr);
    request.fields = list
    (field(Contact_.PARTNER, Partner_.USERS, User_.USERNAME), Contact_.DESCR);
    Set<Contact> expected = new HashSet<>();
    for(Contact d : data.contacts.data(0, 1)) {
      Contact e = ColumnDescr.copyByFields(d, Contact.class, request.fields);
      TestPartners.collectionsEmpty2null(e.getPartner());
      expected.add(e);
    }
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<Contact> response = contactSvc.read(request);
    log.info(() -> "expected=" + Json.serializep(om, expected));
    jpaCheck.resultData("response", response, expected
    , Contact_.PARTNER, field(Contact_.PARTNER, Partner_.USERS));
    validator.select(2).assertTotal();
    String EXPECTED_SQL =
    "from contacts c1_0 join partners p1_0 on p1_0.id=c1_0.partnerId join users u1_0 on p1_0.id=u1_0.personId where u1_0.id in (" + StringUtils.join(dataIds, ",") + ")";
    assertThat(output).contains(EXPECTED_SQL);
  }

  @Test
  public void extended(CapturedOutput output) throws Exception {
    Set<User> sourceData = data.users.data(1, 2, 3);
    UserRequest.Read request = new UserRequest.Read();
    request.fields = list(User_.USERNAME);
    Calendar cal = Calendar.getInstance();
    cal.set(1970, Calendar.MARCH, 2, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);
    Long roleId = data.roles.data.get(2).getId();
    request.filter.and(
      new Expr(Expr.GT, Identifiable.ID, new Value(0))
    , new Expr(Expr.OR
      , list(new Expr(Expr.GE, new Expr(Expr.NOW), new Value(cal.getTime()))
        , new Expr(Expr.NOT
          , new Expr(Expr.OR
            , new Expr(Expr.EQUAL
              , field(UserFltr.GROUPS, UserGroupFltr.ROLES, RoleFltr.IS_URL)
              , new Value(Boolean.FALSE)
              )
            , new Expr(Expr.LIKE
              , field(UserFltr.GROUPS, UserGroupFltr.ROLES, RoleFltr.NAME)
              , new Value("some%")
              )
            , new Expr(Expr.EQUAL
              , UserFltr.USERNAME
              , field(UserFltr.GROUPS, UserGroupFltr.NAME)
              )
            , new Expr(Expr.EQUAL
              , new Expr(Expr.SUBSTRING, UserFltr.EMAIL, 0, 5)
              , new Expr(Expr.CONCAT_WS
                , new Value("~")
                , new Value("tezzt")
                , field(UserFltr.GROUPS, UserGroupFltr.ROLES, RoleFltr.NAME)
                , new Value(42)
                )
              )
            )
          )
        , new Expr(Expr.IS_NULL, field(UserFltr.GROUPS, UserGroupFltr.NAME))
        , new Expr(Expr.NOT_IN
          , field(UserFltr.GROUPS, UserGroupFltr.NAME)
          , list("ROLE_ADMIN2", "ROLE_USER2", "ROLE_SOMETHING2")
          )
        )
      )
    , new Expr(Expr.IN, field(UserFltr.GROUPS, UserGroupFltr.ROLES, Identifiable.ID), roleId)
    );
    Set<User> expected = new HashSet<>();
    for(User d : sourceData) {
      User e = ColumnDescr.copyByFields(d, User.class, request.fields);
      TestPartners.collectionsEmpty2null(e);
      expected.add(e);
    }
    read(request, expected, 1);
    String expectedSql =
    "from users u1_0 left join users_groups g1_0 on u1_0.id=g1_0.userId left join userGroups g1_1 on g1_1.id=g1_0.groupId left join roles_groups r1_0 on g1_1.id=r1_0.groupId left join roles r1_1 on r1_1.id=r1_0.roleId where u1_0.id>0 and (now()>=timestamp with time zone '1970-03-02 00:00:00.000' or not(not(r1_1.isUrl) or r1_1.name like 'some%' escape '' or u1_0.username=g1_1.name or substring(u1_0.email,0,5)=CONCAT_WS('~','tezzt',r1_1.name,42)) or g1_1.name is null or g1_1.name not in ('ROLE_ADMIN2','ROLE_USER2','ROLE_SOMETHING2')) and r1_0.roleId="
    + roleId;
    assertThat(output).contains(expectedSql);
  }

}
