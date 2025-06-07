[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![EN](https://img.shields.io/badge/EN-blue)](readme.md)
[![RU](https://img.shields.io/badge/RU-black)](readme.ru.md)

<h1 align="center">biz.softfor.spring.servicegen</h1>

— це **процесор анотацій** для генерації класів **CRUD-сервісів** Spring з
**Entity**-класів.

Генерований клас отримує ім'я як у вихідного плюс **``Svc``**, успадковується від
[CrudSvc](../biz.softfor.spring.jpa.crud/src/main/java/biz/softfor/spring/jpa/crud/CrudSvc.java),
отримує анотацію **```@Service```** і поміщається в пакет з ім'ям як у
вихідного та заміною "**.jpa**" на "**.spring**":
```java
@Service
public class UserSvc extends CrudSvc<Long, User, UserWor, UserFltr> {}
```

Генерація класів сервісів запускається анотацією
[@GenService](src/main/java/biz/softfor/spring/servicegen/GenService.java).
Наприклад, див. [biz.softfor.user.spring.package-info](../biz.softfor.user.spring/src/main/java/biz/softfor/user/spring/package-info.java):
```java
@GenService(value = { User.class }, exclude = { RoleSvc.class, TokenSvc.class, UserGroupSvc.class })
package biz.softfor.user.spring;
```
Тут у параметрі **value** ми вказуємо класи, у пакетах з якими процесор
шукатиме анотацію **@Entity**, а в **exclude** можемо перерахувати класи
вже реалізованих нами сервісів, які нам генерувати не потрібно.

## Приклад використання

— [biz.softfor.user.spring](../biz.softfor.user.spring).

- Створюємо файл **package-info.java**, у якому пакет анотуємо **@GenService**:
[biz.softfor.user.spring.package-info](../biz.softfor.user.spring/src/main/java/biz/softfor/user/spring/package-info.java)

- У [pom-файлі](../biz.softfor.user.spring/pom.xml) проекту додаємо
залежність від нашого процесора анотацій:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.spring.servicegen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

- Також додаємо залежність з базовим класом **CrudSvc**, вихідними **Entity**,
**-Wor** та **-Fltr**-класами:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.spring.jpa.crud</artifactId>
</dependency>
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.user.jpa</artifactId>
</dependency>
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.user.jpa.withoutrelations</artifactId>
</dependency>
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.user.api.filter</artifactId>
</dependency>
```

## Ліцензія

Цей проект має ліцензію MIT - подробиці дивіться у файлі [license.md](license.md).
