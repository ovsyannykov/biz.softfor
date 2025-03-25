package biz.softfor.spring.jpa.crud;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.jpa.filter.Stmt;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class ConfigJpaCrud {

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private ObjectMapper objectMapper;

  @PostConstruct
  public void postConstruct() throws ReflectiveOperationException {
    Stmt.init(objectMapper);
    ColumnDescr.init(em);
  }

}
