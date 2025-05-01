package biz.softfor.spring.jpa.crud;

import biz.softfor.jpa.crud.querygraph.PredicateProvider;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerRequest;
import biz.softfor.spring.jpa.crud.assets.PartnersTestBasic;
import biz.softfor.spring.jpa.crud.assets.TestPartners;
import biz.softfor.spring.sqllog.SqlCountValidator;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserRequest;
import biz.softfor.util.Holder;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.CommonResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.list;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.transaction.support.TransactionTemplate;

@Log
public class DeleteTest extends PartnersTestBasic {

  @AllArgsConstructor
  public static class Params {

    final String description;
    final Integer idx;
    final int total;
    final SqlCountValidator.Builder validatorBldr;

  }

  public static Stream<Params> delete() {
    List<Params> p = new ArrayList<>();
    p.add(new Params(
      "delete"
    , 1, 11
    , SqlCountValidator.builder().update(2).delete(6)
    ));
    p.add(new Params(
      "by nonexistent Id"
    , -1, 0
    , SqlCountValidator.builder().update(2).delete(6)
    ));
    return p.stream()
    //.skip(0).limit(1)
    ;
  }

  @ParameterizedTest
  @MethodSource
  public void delete(Params params) {
    Partner partner;
    if(params.idx < 0) {
      partner = new Partner();
      partner.setId(Long.valueOf(params.idx));
    } else {
      partner = data.partners.data.get(params.idx);
    }
    Long id = partner.getId();
    log.info(() -> "=".repeat(32) + "\n" + params.description
    + "\nid=" + partner.getId());
    for(int i = 0; i < data.partners.data.size(); ++i) {
      Partner p = data.partners.data.get(i);
      Partner parent = p.getParent();
      if(parent != null && id.equals(parent.getId())) {
        p.setParent(null);
      }
    }
    partner.setUsers(null);
    partner.setContacts(null);
    PartnerRequest.Delete request = new PartnerRequest.Delete();
    request.filter.assignId(id);
    SqlCountValidator validator = params.validatorBldr.entityManager(em).build();
    CommonResponse<Partner> response = partnerSvc.delete(request);
    jpaCheck.update("response", response, params.total);
    validator.assertTotal();
    for(int i = 0; i < data.partners.data.size(); ++i) {
      Partner entity = data.partners.data.get(i);
      if(Objects.equals(entity.getId(), id)) {
        Holder<Partner> holder = new Holder<>();
        new TransactionTemplate(tm).executeWithoutResult(
          status -> holder.value = em.find(Partner.class, id)
        );
        assertThat(holder.value)
        .as(() -> "Partner(id=" + id + ") must not exist.").isNull();
      } else {
        data.partners.check(list(i), data.partnerIgnoringFields
        , TestPartners.PARTNER_FETCH_RELATIONS);
      }
    }
  }

  @Test
  public void deleteByEmptyFilter() {
    SqlCountValidator validator = new SqlCountValidator(em);
    PartnerRequest.Delete request = new PartnerRequest.Delete();
    assertThatThrownBy(() -> partnerSvc.delete(request))
    .isInstanceOf(ClientError.class)
    .hasMessage(MessageFormat.format(
      PredicateProvider.The_filter_must_be_not_empty
    , PredicateProvider.DELETE
    ));
    validator.assertTotal();
  }

  @Test
  public void deleteManyToMany() {
    List<Long> ids = data.users.idList(1);
    log.info(() -> "=".repeat(32) + "\n" + LABEL
    + "\nids=" + ids.toString());
    UserRequest.Delete request = new UserRequest.Delete();
    request.filter.setId(ids);
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<User> response = userSvc.delete(request);
    jpaCheck.update("response", response, 4);
    validator.delete(2).assertTotal();
    for(int i = 0; i < data.users.data.size(); ++i) {
      User entity = data.users.data.get(i);
      Long id = entity.getId();
      if(ids.contains(id)) {
        Holder<User> holder = new Holder<>();
        new TransactionTemplate(tm).executeWithoutResult(
          status -> holder.value = em.find(User.class, id)
        );
        assertThat(holder.value)
        .as(() -> "User(id=" + id + ") must not exist.").isNull();
      } else {
        data.users.check(list(i), data.userIgnoringFields
        , TestPartners.USER_FETCH_RELATIONS);
      }
    }
  }

}
