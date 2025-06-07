[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![UA](https://img.shields.io/badge/UA-yellow)](readme.ua.md)
[![RU](https://img.shields.io/badge/RU-black)](readme.ru.md)

<h1 align="center">biz.softfor.spring.servicegen</h1>

— is an **annotation processor** for generating Spring **CRUD service** classes
from **Entity** classes.

The generated class gets the same name as the original plus **```Svc```**, inherits from
[CrudSvc](../biz.softfor.spring.jpa.crud/src/main/java/biz/softfor/spring/jpa/crud/CrudSvc.java),
gets the **```@Service```** annotation and is placed in a package with the same
name as the original and replacing "**.jpa**" with "**.spring**":
```java
@Service
public class UserSvc extends CrudSvc<Long, User, UserWor, UserFltr> {}
```

The generation of service classes is triggered by the annotation
by [@GenService](src/main/java/biz/softfor/spring/servicegen/GenService.java).
For example, see [biz.softfor.user.spring.package-info](../biz.softfor.user.spring/src/main/java/biz/softfor/user/spring/package-info.java):
```java
@GenService(value = { User.class }, exclude = { RoleSvc.class, TokenSvc.class, UserGroupSvc.class })
package biz.softfor.user.spring;
```
Here in the **value** parameter we specify the classes in the packages with
which the processor will search for the annotation, and in **exclude** we can
list the classes of the services we have already implemented that we do not want
to generate.

## Usage example

— [biz.softfor.user.spring](../biz.softfor.user.spring).

- Create a **package-info.java** file, in which we annotate the package with
**@GenService**: [biz.softfor.user.spring.package-info](../biz.softfor.user.spring/src/main/java/biz/softfor/user/spring/package-info.java)

- We add a dependency on our annotation processor to the project
[pom-file](../biz.softfor.user.spring/pom.xml):
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.spring.servicegen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

- We also include the dependency with the base class **CrudSvc**, the original
**Entity**, **-Wor** and **-Fltr** classes:
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

## License

This project is licensed under the MIT License - see the [license.md](license.md) file for details.
