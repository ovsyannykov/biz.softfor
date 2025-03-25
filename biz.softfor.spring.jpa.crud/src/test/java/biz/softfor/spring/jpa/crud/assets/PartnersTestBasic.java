package biz.softfor.spring.jpa.crud.assets;

import biz.softfor.i18nspring.ConfigI18nSpring;
import biz.softfor.spring.jpa.crud.ConfigJpaCrud;
import biz.softfor.spring.jpa.crud.TestConfigJpaCrud;
import biz.softfor.spring.jpa.properties.ConfigJpaProperties;
import biz.softfor.spring.objectmapper.ConfigObjectMapper;
import biz.softfor.spring.sqllog.ConfigSqlLog;
import biz.softfor.testutil.Check;
import biz.softfor.testutil.spring.ConfigTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@DataJpaTest(showSql = false)
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {
  TestConfigJpaCrud.class
, ConfigI18nSpring.class
, ConfigJpaCrud.class
, ConfigJpaProperties.class
, ConfigObjectMapper.class
, ConfigSqlLog.class
, ConfigTestUtil.class
, LocalValidatorFactoryBean.class
})
public class PartnersTestBasic {

  public final static int DATA_SIZE = 4;

  @Autowired
  protected TestConfigJpaCrud.ContactTestSvc contactSvc;

  @Autowired
  protected TestConfigJpaCrud.PartnerTestSvc partnerSvc;

  @Autowired
  protected TestConfigJpaCrud.RoleTestSvc roleSvc;

  @Autowired
  protected TestConfigJpaCrud.UserTestSvc userSvc;

  @Autowired
  protected TestConfigJpaCrud.UserGroupTestSvc userGroupSvc;

  @Autowired
  protected Check jpaCheck;

  @Autowired
  protected ObjectMapper om;

  @PersistenceContext
  protected EntityManager em;

  @Autowired
  protected PlatformTransactionManager tm;

  protected String LABEL;
  protected TestPartners data;

  @BeforeEach
  public void beforeEach(TestInfo testInfo) {
    LABEL = testInfo.getTestMethod().orElseThrow().getName();
    data = new TestPartners(LABEL, DATA_SIZE, jpaCheck, om, em, tm);
    data.init();
  }

  @AfterEach
  public void afterEach() {
    em.clear();
    data.remove();
  }

}
