[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![EN](https://img.shields.io/badge/EN-blue)](readme.md)
[![RU](https://img.shields.io/badge/RU-black)](readme.ru.md)

<h1 align="center">biz.softfor.spring.restcontrollergen</h1>

— це **процесор анотацій** для генерації з **Entity**-класів REST-контролерів,
що реалізують **CRUD API**.

Наприклад, ось як виглядає контролер для сутності Partner:
```java
package biz.softfor.partner.spring.rest;

@RestController
@RequestMapping(path = "/partner", produces = "application/json")
public class PartnerCtlr {
  private final JsonFilters jsonFilters;
  private final SecurityMgr securityMgr;
  private final PartnerSvc service;

  public PartnerCtlr(JsonFilters jsonFilters, SecurityMgr securityMgr, PartnerSvc service) {
    this.jsonFilters=jsonFilters;
    this.securityMgr=securityMgr;
    this.service=service;
  }

  @RequestMapping(path = "/create", method = RequestMethod.POST)
  public CommonResponse<PartnerWor> create
  (@RequestBody @Validated(Create.class) PartnerRequest.Create request) {
    securityMgr.methodCheck(PartnerSvc.class, "create", SecurityUtil.groups());
    return service.create(request);
  }

  @RequestMapping(path = "/read", method = RequestMethod.POST)
  public MappingJacksonValue read(@RequestBody PartnerRequest.Read request) {
    securityMgr.readCheck(service, request, SecurityUtil.groups());
    return jsonFilters.filter(service::read, request, Partner.class);
  }

  @RequestMapping(path = "/update", method = RequestMethod.POST)
  public CommonResponse update(@RequestBody @Valid PartnerRequest.Update request) {
    securityMgr.updateCheck(service, request, SecurityUtil.groups());
    service.validateUpdate(request);
    return service.update(request);
  }

  @RequestMapping(path = "/delete", method = RequestMethod.POST)
  public CommonResponse delete(@RequestBody PartnerRequest.Delete request) {
    securityMgr.methodCheck(PartnerSvc.class, "delete", SecurityUtil.groups());
    return service.delete(request);
  }
}
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
