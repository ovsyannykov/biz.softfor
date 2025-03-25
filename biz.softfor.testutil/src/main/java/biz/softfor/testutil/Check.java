package biz.softfor.testutil;

import biz.softfor.util.Json;
import biz.softfor.util.api.BasicResponse;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.Identifiable;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.AbstractListAssert;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.description.Description;

@AllArgsConstructor
@Builder(builderMethodName = "")
@Log
public class Check {

  private final ObjectMapper objectMapper;
  private Class[] ignoredTypes = new Class[] {};
  private String[] ignoredFields = new String[] {};

  public Check(ObjectMapper objectMapper) {
    this(objectMapper, new Class[] {}, new String[] {});
  }

  public static class CheckBuilder {

    private CheckBuilder objectMapper(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
      return this;
    }

    public CheckBuilder ignoredTypes(Class<?>... ignoredTypes) {
      this.ignoredTypes = ignoredTypes;
      return this;
    }

    public CheckBuilder ignoredFields(String... ignoredFields) {
      this.ignoredFields = ignoredFields;
      return this;
    }

  }

  public static CheckBuilder builder(ObjectMapper objectMapper) {
    return new CheckBuilder().objectMapper(objectMapper);
  }

  public <K extends Number, E extends Identifiable<K>> void create(String name, CommonResponse<E> actual) {
    resultData(name, actual);
    assertThat(actual.getData(0).getId())
    .as(() -> name + "=" + Json.serializep(objectMapper, actual)
    + "\n" + name + "." + BasicResponse.DATA + "[0]." + Identifiable.ID)
    .isNotNull();
  }

  public void data
  (String name, Object actual, Object expected, String... ignoringFields) {
    String[] ignoring = ArrayUtils.addAll(ignoringFields, ignoredFields);
    Supplier<String> as
    = () -> name + "=" + Json.serializep(objectMapper, actual) + "\n" + name;
    if(actual instanceof Iterable) {
      RecursiveComparisonConfiguration configuration
      = RecursiveComparisonConfiguration.builder()
      .withStrictTypeChecking(false)
      .withIgnoreCollectionOrder(true)
      .withIgnoredFieldsOfTypes(ignoredTypes)
      .withIgnoredFields(ignoring)
      .build();
      assertThat((Iterable<?>)actual).as(as)
      .usingRecursiveFieldByFieldElementComparator(configuration)
      .usingRecursiveComparison()
      .ignoringCollectionOrder()
      .ignoringFieldsOfTypes(ignoredTypes)
      .ignoringFields(ignoring)
      .isEqualTo(expected);
    } else {
      assertThat(actual).as(as)
      .usingRecursiveComparison()
      .ignoringCollectionOrder()
      .ignoringFieldsOfTypes(ignoredTypes)
      .ignoringFields(ignoring)
      .isEqualTo(expected);
    }
  }

  public void isOk(String name, CommonResponse<?> actual) {
    log.info(() -> name + "=" + Json.serializep(objectMapper, actual));
    assertThat(actual.getStatus())
    .as(() -> name + "=" + Json.serializep(objectMapper, actual) + "\n" + name + "." + BasicResponse.STATUS)
    .isEqualTo(BasicResponse.OK);
  }

  public void resultData(String name, CommonResponse<?> actual) {
    isOk(name, actual);
    assertThat(actual.getData())
    .as(() -> name + "=" + Json.serializep(objectMapper, actual) + "\n" + name + "." + BasicResponse.DATA)
    .isNotEmpty();
  }

  public //<K extends Number, E extends Identifiable<K>, R extends CommonResponse<E>>
  void resultData(String name, CommonResponse<?> actual, int expectedDataSize) {
    isOk(name, actual);
    AbstractListAssert listAssert = assertThat(actual.getData()).as(new Description() {
      @Override
      public String value() {
        return name + "=" + Json.serializep(objectMapper, actual) + "\n" + name + "." + BasicResponse.DATA;
      }
    });
    if(expectedDataSize > 0) {
      listAssert.hasSize(expectedDataSize);
    } else {
      listAssert.isNullOrEmpty();
    }
  }

  public //<K extends Number, E extends Identifiable<K>, R extends CommonResponse<E>>
  void resultData(
    String name
  , CommonResponse<?> actual
  , Object expected
  , String... ignoringFields
  ) {
    resultData(name, actual);
    log.info(() -> "expected=" + Json.serializep(objectMapper, expected));
    data(name + "." + BasicResponse.DATA + "[0]", actual.getData(0), expected, ignoringFields);
  }

  public //<K extends Number, E extends Identifiable<K>, R extends CommonResponse<E>>
  void resultData(
    String name
  , CommonResponse<?> actual
  , Collection<?> expected
  , String... ignoringFields
  ) {
    resultData(name, actual, expected == null ? 0 : expected.size());
    data(name + "." + BasicResponse.DATA, actual.getData(), expected, ignoringFields);
  }

  public void update(String name, CommonResponse<?> actual, int expectedTotal) {
    isOk(name, actual);
    assertThat(actual.getTotal())
    .as(() -> name + "=" + Json.serializep(objectMapper, actual)
    + "\n" + name + "." + CommonResponse.TOTAL)
    .isEqualTo(expectedTotal);
  }

}
