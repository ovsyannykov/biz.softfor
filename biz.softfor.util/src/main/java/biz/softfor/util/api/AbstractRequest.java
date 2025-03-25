package biz.softfor.util.api;

import biz.softfor.util.AbstractError;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractRequest {

  public final static String DATA = "data";
  public final static String FIELDS = "fields";

  private static int startRowOffset = 0;

  public static void setStartRowOffset(int v) {
    startRowOffset = v;
  }

  public static int getStartRowOffset() {
    return startRowOffset;
  }

  private static int defaultRowsOnPage = 50;

  public static void setDefaultRowsOnPage(int v) {
    defaultRowsOnPage = v;
  }

  public static int getDefaultRowsOnPage() {
    return defaultRowsOnPage;
  }

  private static int maxRowsOnPage = 5000;

  public static void setMaxRowsOnPage(int v) {
    maxRowsOnPage = v;
  }

  public static int getMaxRowsOnPage() {
    return maxRowsOnPage;
  }

  public String traceId;
  public final static String traceId_KEY = "traceId";

  public String token;
  public final static String token_KEY = "token";

  @JsonInclude(Include.NON_NULL)
  private Integer startRow;
  public final static String startRow_KEY = "startRow";

  public Integer getStartRow() {
    return startRow;
  }

  public final void setStartRow(Integer v) {
    startRow = (v == null || v < startRowOffset) ? startRowOffset : v;
  }

  @JsonInclude(Include.NON_NULL)
  @Getter
  private Integer rowsOnPage;
  public final static String rowsOnPage_KEY = "rowsOnPage";

  public final void setRowsOnPage(Integer v) {
    if(v == null || v <= 0) {
      rowsOnPage = maxRowsOnPage;
    } else if(v > maxRowsOnPage) {
      throw new AbstractError(
        "rowsOnPage exceeded limit of " + maxRowsOnPage + ": " +  + v
      , BasicResponse.ROWS_ON_PAGE_EXCEED_LIMIT
      );
    } else {
      rowsOnPage = v;
    }
  }

  public AbstractRequest() {
    setStartRow(startRowOffset);
    setRowsOnPage(defaultRowsOnPage);
  }

  @JsonIgnore
  public int getFirstRowIndex() {
    return getStartRow() - startRowOffset;
  }

}
