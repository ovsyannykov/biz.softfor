[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![UA](https://img.shields.io/badge/UA-yellow)](readme.ua.md)
[![EN](https://img.shields.io/badge/EN-blue)](readme.md)

<h1 align="center">biz.softfor.spring.servicegen</h1>

— это **процессор аннотаций** для генерации классов **CRUD-сервисов** Spring из
**Entity**-классов.

Генерируемый класс получает имя как у исходного плюс **```Svc```**, наследуется от
[CrudSvc](../biz.softfor.spring.jpa.crud/src/main/java/biz/softfor/spring/jpa/crud/CrudSvc.java),
получает аннотацию **```@Service```** и помещается в пакет с именем как у
исходного и заменой "**.jpa**" на "**.spring**":
```java
@Service
public class UserSvc extends CrudSvc<Long, User, UserWor, UserFltr> {}
```

Генерация классов сервисов запускается аннотацией
[@GenService](src/main/java/biz/softfor/spring/servicegen/GenService.java).
Например, см. [biz.softfor.user.spring.package-info](../biz.softfor.user.spring/src/main/java/biz/softfor/user/spring/package-info.java):
```java
@GenService(value = { User.class }, exclude = { RoleSvc.class, TokenSvc.class, UserGroupSvc.class })
package biz.softfor.user.spring;
```
Здесь в параметре **value** мы указываем классы, в пакетах с которыми процессор
будет искать аннотацию **@Entity**, а в **exclude** можем перечислить классы
уже реализованных нами сервисов, которые мы генерировать не хотим.

## Пример использования

— [biz.softfor.user.spring](../biz.softfor.user.spring).

- Создаём файл **package-info.java**, в котором пакет аннотируем **@GenService**:
[biz.softfor.user.spring.package-info](../biz.softfor.user.spring/src/main/java/biz/softfor/user/spring/package-info.java)

- В [pom-файле](../biz.softfor.user.spring/pom.xml) проекта добавляем
зависимость от нашего процессора аннотаций:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.spring.servicegen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

- Так же включаем зависимость с базовым классом **CrudSvc**, исходными
**Entity**, **-Wor** и **-Fltr**-классами:
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

## Лицензия

Этот проект имеет лицензию MIT - подробности смотрите в файле [license.md](license.md).
