package biz.softfor.jpa;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class SoftforbizFunctionContributor implements FunctionContributor {

  public final static String COUNT_ALL_OVER_FUNCTION = "COUNT_ALL_OVER";

  @Override
  public void contributeFunctions(FunctionContributions functionContributions) {
    functionContributions.getFunctionRegistry().register(
      COUNT_ALL_OVER_FUNCTION
    , new StandardSQLFunction("COUNT(*) OVER", StandardBasicTypes.LONG)
    );
  }

}
