package biz.softfor.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public class DateUtil {

  public final static String DATETIME_TZ_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  public final static String DATETIME_MS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  public final static String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
  public final static String DATE_FORMAT = "yyyy-MM-dd";

  //Order is important! First we try to choose the longest format.
  private final static String[] FORMATS = {
    DATETIME_TZ_FORMAT
  , DATETIME_MS_FORMAT
  , DATETIME_FORMAT
  , DATE_FORMAT
  };

  public static class Parser {

    private String[] formats = FORMATS;
    private final Locale locale = Locale.US;

    public String[] getFormats() {
      return formats;
    }

    public void setFormats(String[] v) {
      formats = v;
    }

    public Date parse(String v) throws IOException {
      Date result = null;
      if(StringUtils.isNotBlank(v)) {
        for(String f : formats) {
          try {
            result = new SimpleDateFormat(f, locale).parse(v);
            break;
          } catch(ParseException ex) {
          }
        }
        if(result == null) {
          String m = "";
          for(String f : formats) {
            if(!m.isEmpty()) {
              m += ", ";
            }
            m += "\"" + f + "\"";
          }
          throw new IOException("Date value \"" + v + "\" has invalid format. Valid formats: " + m);
        }
      }
      return result;
    }

  }

  public static class Deserializer extends JsonDeserializer<Date> {

    private final static Parser parser = new Parser();

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
      return parser.parse(jp.getValueAsString());
    }

  }

  public static class Serializer extends JsonSerializer<Date> {

    public final String format = DATETIME_TZ_FORMAT;
    private final Locale locale = Locale.US;

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider prov) throws IOException {
      gen.writeString(new SimpleDateFormat(format, locale).format(value));
    }

    @Override
    public Class<Date> handledType() {
      return Date.class;
    }

  }

  public static boolean isDateOnly(Date date, Calendar calendar) {
    calendar.setTime(date);
    return
      calendar.get(Calendar.MILLISECOND) == 0
    && calendar.get(Calendar.SECOND) == 0
    && calendar.get(Calendar.MINUTE) == 0
    && calendar.get(Calendar.HOUR_OF_DAY) == 0
    ;
  }

  public static Date of(int year, int month, int date) {
    return of(year, month, date, 0, 0, 0);
  }

  public static Date of(int year, int month, int date, int hrs, int min, int sec) {
    Calendar c = Calendar.getInstance();
    c.set(year, month, date, hrs, min, sec);
    c.set(Calendar.MILLISECOND, 0);
    return c.getTime();
  }

  public static Date toDate(Date date, Calendar calendar) {
    calendar.setTime(date);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    return calendar.getTime();
  }

  public static Date toDate(LocalDate v) {
    return Date.from(v.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date toDate(LocalDateTime v) {
    return Date.from(v.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDate toLocalDate(Date v) {
    return v.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public static LocalDateTime toLocalDateTime(Date v) {
    return v.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

}
