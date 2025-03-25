package biz.softfor.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class StringUtil {

  public final static String FIELDS_DELIMITER = ".";
  public final static String FIELDS_DELIMITER_REGEX = "\\" + FIELDS_DELIMITER;
  public final static String NULL = "null";

  public static String camelCaseToSentenceCase(String camelCase) {
    StringBuilder result = new StringBuilder(camelCase.length() + 4);
    char pc = ' ';
    for(int i = 0; i < camelCase.length(); ++i) {
      char c = camelCase.charAt(i);
      if(i == 0) {
        result.append(Character.toUpperCase(c));
      } else {
        if(Character.isUpperCase(c)) {
          result.append(' ');
          result.append(Character.toLowerCase(c));
        } else {
          result.append(c);
        }
      }
    }
    return result.toString();
  }

  public static String camelCaseToUnderScoreUpperCase(String camelCase) {
    StringBuilder result = new StringBuilder(camelCase.length() + 4);
    char pc = '_';
    for(int i = 0; i < camelCase.length(); ++i) {
      char c = camelCase.charAt(i);
      if(Character.isUpperCase(c)) {
        if(pc != '_') {
          result.append('_');
        }
        result.append(c);
      } else {
        result.append(Character.toUpperCase(c));
      }
      pc = c;
    }
    return result.toString();
  }

  public static String field(String... parts) {
    String result = "";
    for(String p : parts) {
      if(!result.isEmpty()) {
        result += FIELDS_DELIMITER;
      }
      result += p;
    }
    return result;
  }

  public static String fieldToName(String field) {
    return field.substring(field.lastIndexOf(StringUtil.FIELDS_DELIMITER) + 1);
  }

  public static List<String> filterByPrefix(List<String> fields, String prefix) {
    List<String> result = fields;
    if(StringUtils.isNotBlank(prefix)) {
      result = null;
      if(fields != null) {
        String criteria = prefix + FIELDS_DELIMITER;
        int criteriaLength = criteria.length();
        for(String f : fields) {
          if(f.length() > criteriaLength && f.startsWith(prefix)) {
            if(result == null) {
              result = new ArrayList<>(2);
            }
            result.add(f.substring(criteriaLength));
          }
        }
      }
    }
    return result;
  }

  public static long longHash(String v) {
    long result = 1125899906842597L;
    int l = v.length();
    for(int i = 0; i < l; ++i) {
      result = 31 * result + v.charAt(i);
    }
    return result;
  }

  public static String snakeCaseCapitalize(String snake_case) {
    String[] parts = snake_case.split("_");
    for(int i = parts.length; --i >= 0;) {
      parts[i] = StringUtils.capitalize(parts[i]);
    }
    return String.join("_", parts);
  }

  public static String withPrefix(String prefix, String attr) {
    return prefix.isEmpty() ? attr : field(prefix, attr);
  }

}
