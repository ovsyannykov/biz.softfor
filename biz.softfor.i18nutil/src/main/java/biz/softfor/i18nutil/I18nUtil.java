package biz.softfor.i18nutil;

import biz.softfor.util.Reflection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import static java.util.Locale.ENGLISH;
import java.util.Map;
import static java.util.Map.of;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

@I18n
public class I18nUtil {

  public final static Locale UKRAINIAN = Locale.of("uk");
  public final static List<Locale> LOCALES = List.of(ENGLISH, UKRAINIAN);
  public final static Locale DEFAULT_LOCALE = Locale.ENGLISH;

  @AllArgsConstructor
  private static class Message {
    public final String message;
    public final String from;
  }

  @AllArgsConstructor
  private static class Error {
    public final String key;
    public final Locale locale;
    public final String message;
    public final String from;
  }

  public final static String ERRORS = "errors";
  public final static List<Error> errors = new ArrayList<>();
  public final static String MESSAGES = "messages";
  private final static Map<String, Map<Locale, Message>> messages = new HashMap<>();

  static {
    add(ENGLISH.toString(), of(
      ENGLISH, ENGLISH.getDisplayLanguage(ENGLISH)
    , UKRAINIAN, "Англійська"
    ));
    add(UKRAINIAN.toString(), of(
      ENGLISH, "Ukrainian"
    , UKRAINIAN, "Українська"
    ));
    add("jakarta.validation.constraints.PastOrPresent.message", of(
      ENGLISH, "must be a date in the past or in the present"
    , UKRAINIAN, "має бути в минулому або теперішньому часі"
    ));

  }

  public static void add(String key, Map<Locale, String> aMessages) {
    Map<Locale, Message> existing = messages.get(key);
    if(existing == null) {
      Map<Locale, Message> adding = new HashMap<>(LOCALES.size());
      for(Locale l : LOCALES) {
        String addingMessage = aMessages.get(l);
        if(addingMessage == null) {
          errors.add(new Error(key, l, null, Reflection.trace()));
        } else {
          adding.put(l, new Message(addingMessage, Reflection.trace()));
        }
      }
      messages.put(key, adding);
    } else {
      boolean isDuplicate = true;
      for(Locale l : LOCALES) {
        String message = existing.get(l).message;
        String addingMessage = aMessages.get(l);
        if(!message.equals(addingMessage)) {
          isDuplicate = false;
          errors.add(new Error(key, l, addingMessage, Reflection.trace()));
        }
      }
      if(isDuplicate) {
        errors.add(new Error(key, null, null, Reflection.trace()));
      }
    }
  }

  public static void genMessages(String resourcesDir, String... packages)
  throws Exception {
    Reflections reflections = new Reflections(new ConfigurationBuilder()
    .forPackages(packages).setScanners(Scanners.TypesAnnotated));
    for(Class c : reflections.getTypesAnnotatedWith(I18n.class)) {
      c.getConstructor().newInstance();
    }
    List<String> keys = messages.keySet().stream().sorted().collect(Collectors.toList());
    for(Locale l : LOCALES) {
      String fileName = l.equals(DEFAULT_LOCALE) ? "" : ("_" + l.toString());
      fileName = resourcesDir + "/" + MESSAGES + fileName + ".properties";
      try(FileWriter fileWriter = new FileWriter(fileName);
      BufferedWriter writer = new BufferedWriter(fileWriter)) {
        for(String k : keys) {
          Message message = messages.get(k).get(l);
          if(message != null) {
            writer.write(k + "=" + messages.get(k).get(l).message + "\n");
          }
        }
      }
    }
    String log = "./log";
    File logPath = new File(log);
    if(!logPath.exists()) {
      logPath.mkdir();
    }
    String fileName = log + "/" + MESSAGES + "." + ERRORS;
    logPath = new File(fileName);
    if(logPath.exists()) {
      logPath.delete();
    }
    if(!errors.isEmpty()) {
      try(FileWriter fileWriter = new FileWriter(fileName);
      BufferedWriter writer = new BufferedWriter(fileWriter)) {
        for(int i = 0; i < errors.size(); ++i) {
          Error e = errors.get(i);
          if(i > 0) {
            writer.write("\n\n");
          }
          if(e.locale == null) {
            Message message = messages.get(e.key).get(DEFAULT_LOCALE);
            writer.write("DUPLICATE| " + e.key + "\n" + message.from + "\n" + e.from);
          } else if(e.message == null) {
            writer.write("ABSENT| " + e.key + " (" + e.locale.toString() + ")\n" + e.from);
          } else {
            Message message = messages.get(e.key).get(e.locale);
            writer.write("DIFFERENCE| " + e.key + " (" + e.locale.toString() + ")\n"
            + message.from + ": " + message.message + "\n"
            + e.from + ": " + e.message);
          }
        }
      }
    }
  }

}
