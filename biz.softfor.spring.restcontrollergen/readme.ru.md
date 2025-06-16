[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![UA](https://img.shields.io/badge/UA-yellow)](readme.ua.md)
[![EN](https://img.shields.io/badge/EN-blue)](readme.md)

<h1 align="center">biz.softfor.spring.restcontrollergen</h1>

— это **процессор аннотаций** для генерации из **Entity**-классов
REST-контроллеров, реализующих **CRUD API**.

Например, вот как выглядит контроллер для сущности Partner:
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

Генерируемый класс получает имя как у исходного плюс **```Ctlr```**,
помещается в пакет, как исходный, но с заменой **.jpa** на **.spring.rest**,
имеет маппинг, совпадающий с именем исходного класса, но начинающегося
со строчного символа, и реализующего CRUD-методы с помощью соответствующего
наследника [CrudSvc](../biz.softfor.spring.jpa.crud/src/main/java/biz/softfor/spring/jpa/crud/CrudSvc.java).
CRUD-методы получают одноимённый маппинг и доступны по POST. Компонент
securityMgr обеспечивает проверку прав доступа к методу, а для операций read и
update и к запрашиваемым и обновляемым полям. В методе **create** валидирутся
входные данные, а в **update** - только те, что обновляются. Результат
возвращается в JSON.

Генерация классов сервисов запускается аннотацией
[@GenRestController](src/main/java/biz/softfor/spring/restcontrollergen/GenRestController.java).
Например, см. [biz.softfor.partner.spring.package-info](../biz.softfor.partner.spring/src/main/java/biz/softfor/user/spring/package-info.java):
```java
@GenRestController(value = { Partner.class })
package biz.softfor.partner.spring.rest;
```
Здесь в параметре **value** мы указываем классы, в пакетах с которыми процессор
будет искать аннотацию **@Entity**. В параметре **exclude** можно указать классы,
для которые мы генерировать контроллеры не хотим.

## Пример использования

— [biz.softfor.partner.spring](../biz.softfor.partner.spring).

- Создаём файл **package-info.java**, в котором пакет аннотируем
**@GenRestController**.

- В [pom-файле](../biz.softfor.partner.spring.rest/pom.xml) проекта добавляем
зависимость от нашего процессора аннотаций:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.spring.restcontrollergen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

- Так же включаем зависимости от соответствующих CRUD-сервисов и
[biz.softfor.spring.security.service](../biz.softfor.spring.security.service):
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.partner.spring</artifactId>
</dependency>
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.spring.security.service</artifactId>
</dependency>
```

## Лицензия

Этот проект имеет лицензию MIT - подробности смотрите в файле [license.md](license.md).
