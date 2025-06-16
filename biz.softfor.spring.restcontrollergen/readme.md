[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![UA](https://img.shields.io/badge/UA-yellow)](readme.ua.md)
[![RU](https://img.shields.io/badge/RU-black)](readme.ru.md)

<h1 align="center">biz.softfor.spring.restcontrollergen</h1>

— This is an **annotation processor** for generating REST controllers from
**Entity** classes that implement **CRUD API**.

For example, here is what the controller for the Partner entity looks like:
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

The generated class gets the same name as the original class plus **```Ctlr```**,
is placed in the package like the original one, but with **.jpa** replaced by
**.spring.rest**, has a mapping that matches the name of the original class,
but starts with an lowercase character, and implements CRUD methods using the
corresponding [CrudSvc](../biz.softfor.spring.jpa.crud/src/main/java/biz/softfor/spring/jpa/crud/CrudSvc.java)
inheritor. CRUD methods get the same-name mapping and are accessible via POST.
The securityMgr component ensures access rights to the method, and for read and
update operations, to the requested and updated fields. In the **create** method,
the input data is validated, and in **update** - only the data that is being
updated. The result is returned in JSON.

The generation of service classes is triggered by the annotation
[@GenRestController](src/main/java/biz/softfor/spring/restcontrollergen/GenRestController.java).
For example, see [biz.softfor.partner.spring.package-info](../biz.softfor.partner.spring/src/main/java/biz/softfor/user/spring/package-info.java):
```java
@GenRestController(value = { Partner.class })
package biz.softfor.partner.spring.rest;
```
Here in the **value** parameter we specify the classes in the packages with
which the processor will look for the **@Entity** annotation. In the **exclude**
parameter you can specify the classes for which we do not want to generate
controllers.

## Example of use

— [biz.softfor.partner.spring](../biz.softfor.partner.spring).

- Create a **package-info.java** file, in which we annotate the package
**@GenRestController**.

- In the [pom-file](../biz.softfor.partner.spring.rest/pom.xml) of the project,
add the dependency on our annotation processor:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.spring.restcontrollergen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
``

- We also include dependencies on the corresponding CRUD services and
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

## License

This project is licensed under the MIT License - see the [license.md](license.md) file for details.
