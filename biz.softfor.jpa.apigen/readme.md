[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![UA](https://img.shields.io/badge/UA-yellow)](readme.ua.md)
[![RU](https://img.shields.io/badge/RU-black)](readme.ru.md)

<h1 align="center">biz.softfor.jpa.apigen</h1>

— **annotation processor** that generates classes for working with services that
implements CRUD API (see [biz.softfor.spring.jpa.crud](../biz.softfor.spring.jpa.crud)
and [biz.softfor.spring.servicegen](../biz.softfor.spring.servicegen)):
**DTO** (Data Transfer Object), **requests** and **responses**.
These classes are actually the specification of our API.

The **-Dto** class is generated according to the following rules:

- The class is created in a package with a name in which **.jpa.** is replaced
with **.api.**, and the **-Dto** suffix is ​​added to the name of the class itself:

  ```biz.softfor.user.jpa.Role => biz.softfor.user```<b>```.api.```</b>```Role```<b>```Dto```</b>

- **Dto** class inherits from
[HaveId](../biz.softfor.util/src/main/java/biz/softfor/util/api/HaveId.java).

- The **@ToString** annotation is copied into the class annotation:
<table border="0">
<tr>
<td>

```java
@NoArgsConstructor
@Entity
@Table(name = Role.TABLE)
@Getter
@Setter
@ToString
@JsonFilter("Role")
public class Role
implements Identifiable<Long>, Serializable {
```

</td>
<td>
=>
</td>
<td>

```java
@ToString(callSuper = true)
public class RoleDto extends HaveId<Long> {
```

</td>
</tr>
</table>

- The fields of the generated **```Dto```**-class are provided with getters,
setters and constants with the field names. For clarity, they are omitted in the
examples below.

- The type of the annotated **@OneToOne** field is replaced with a
**```Dto```**-class:
<table border="0">
<tr>
<td>

```java
@OneToOne(optional = true, fetch = FetchType.LAZY)
@PrimaryKeyJoinColumn
private PartnerDetails partnerDetails;
```

</td>
<td>
=>
</td>
<td>

```java
private PartnerDetailsDto partnerDetails;
```

</td>
</tr>
</table>

- The type of the annotated **@ManyToOne** field is also replaced with the
**```Dto```** class:
<table border="0">
<tr>
<td>

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "postcodeId")
private Postcode postcode;
```

</td>
<td>
=>
</td>
<td>

```java
private PostcodeDto postcode;
```

</td>
</tr>
</table>

- The **@OneToMany** annotated field is converted into List of ```-Dto``` objects:
<table border="0">
<tr>
<td>

```java
@OneToMany(
  mappedBy = "partner"
, orphanRemoval = true
, fetch = FetchType.LAZY
)
@JsonIgnoreProperties
(value = { "partner" }, allowSetters = true)
@ToString.Exclude
@EqualsAndHashCode.Exclude
private List<Contact> contacts;
```

</td>
<td>
=>
</td>
<td>

```java
@JsonIgnoreProperties
(allowSetters = true, value = "partner")
private List<ContactDto> contacts;
```

</td>
</tr>
</table>

- The **@ManyToMany** annotated field is converted into Set of ```Dto```-objects:
<table border="0">
<tr>
<td>

```java
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
  name = "users_groups"
, joinColumns = @JoinColumn(name = "groupId")
, inverseJoinColumns = @JoinColumn(name = "userId")
)
@JsonIgnoreProperties
(value = { User.GROUPS }, allowSetters = true)
@ToString.Exclude
private Set<User> users;
```

</td>
<td>
=>
</td>
<td>

```java
@JsonIgnoreProperties
(allowSetters = true, value = "groups")
private Set<UserDto> users;
```

</td>
</tr>
</table>

- Annotated **@Column** field is copied unchanged, without
**```jakarta.persistence.*```** package annotations:
<table border="0">
<tr>
<td>

```java
@Column
@Temporal(TemporalType.DATE)
@NotNull
@PastOrPresent
private LocalDate partnerRegdate;
```

</td>
<td>
=>
</td>
<td>

```java
@NotNull(groups = { Create.class, PartnerRegdate.class })
@PastOrPresent(groups = { Create.class, PartnerRegdate.class })
private LocalDate partnerRegdate;
```

</td>
</tr>
</table>

As you may have noticed, the validation annotations are supplemented by the
**```groups```** parameter and the corresponding interfaces, which are also
generated in this class. This allows you to **selectively validate only the changed**
fields.

In the **Create** and **Update** requests for fields annotated with @ManyToOne,
@OneToMany and @ManyToMany it is more convenient to use identifiers. For this
purpose, **Request-data Transfer Objects** - ```-Rto``` classes are generated.

The **-Rto** class is generated according to the following rules:

- The class is created in a package with a name in which **.jpa.** is replaced
with **.api.**, and the **-Rto** suffix is ​​added to the name of the class itself:

  ```biz.softfor.user.jpa.Role => biz.softfor.user```<b>```.api.```</b>```Role```<b>```Rto```</b>

- **Rto** class inherits from
[HaveId](../biz.softfor.util/src/main/java/biz/softfor/util/api/HaveId.java).

- The **@ToString** annotation is copied into the class annotation:
<table border="0">
<tr>
<td>

```java
@NoArgsConstructor
@Entity
@Table(name = Role.TABLE)
@Getter
@Setter
@ToString
@JsonFilter("Role")
public class Role
implements Identifiable<Long>, Serializable {
```

</td>
<td>
=>
</td>
<td>

```java
@ToString(callSuper = true)
public class RoleRto extends HaveId<Long> {
```

</td>
</tr>
</table>

- The fields of the generated **```Rto```**-class are provided with getters,
setters and constants with the field names. For clarity, they are omitted in the
examples below.

- The type of the annotated **@OneToOne** field is replaced with a **```Rto```**-class:
<table border="0">
<tr>
<td>

```java
@OneToOne(optional = true, fetch = FetchType.LAZY)
@PrimaryKeyJoinColumn
private PartnerDetails partnerDetails;
```

</td>
<td>
=>
</td>
<td>

```java
private PartnerDetailsRto partnerDetails;
```

</td>
</tr>
</table>

- The **@ManyToOne** annotated field is also replaced with the identifier of
the type corresponding to the relationship key with the name taken from
the **@JoinColumn** annotation:
<table border="0">
<tr>
<td>

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "postcodeId")
private Postcode postcode;
```

</td>
<td>
=>
</td>
<td>

```java
private Integer postcodeId;
```

</td>
</tr>
</table>

- An annotated **@OneToMany** field is converted to a List of identifier keys:
<table border="0">
<tr>
<td>

```java
@OneToMany(
  mappedBy = "partner"
, orphanRemoval = true
, fetch = FetchType.LAZY
)
@JsonIgnoreProperties
(value = { "partner" }, allowSetters = true)
@ToString.Exclude
@EqualsAndHashCode.Exclude
private List<Contact> contacts;
```

</td>
<td>
=>
</td>
<td>

```java
/**
 * The default value of this field is {@code null}, which means no changes when saving.
 */
private List<Long> contactIds;
```

</td>
</tr>
</table>

- An annotated **@ManyToMany** field is converted to a Set of identifier keys:
<table border="0">
<tr>
<td>

```java
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
  name = "users_groups"
, joinColumns = @JoinColumn(name = "groupId")
, inverseJoinColumns = @JoinColumn(name = "userId")
)
@JsonIgnoreProperties
(value = { User.GROUPS }, allowSetters = true)
@ToString.Exclude
private Set<User> users;
```

</td>
<td>
=>
</td>
<td>

```java
/**
 * The default value of this field is {@code null}, which means no changes when saving.
 */
private Set<Long> userIds;
```

</td>
</tr>
</table>

- Annotated **@Column** field is copied unchanged, without
**```jakarta.persistence.*```** package annotations:
<table border="0">
<tr>
<td>

```java
@Column
@Temporal(TemporalType.DATE)
@NotNull
@PastOrPresent
private LocalDate partnerRegdate;
```

</td>
<td>
=>
</td>
<td>

```java
@NotNull(groups = { Create.class, PartnerRegdate.class })
@PastOrPresent(groups = { Create.class, PartnerRegdate.class })
private LocalDate partnerRegdate;
```

</td>
</tr>
</table>

In ```Rto```-classes, validation annotations are also supplemented with the
**```groups```** parameter and the corresponding interfaces generated in this
class. This allows **selectively validating only changed** fields.

In addition, the ***Request** classes are generated according to this template,
containing the **Create**, **Read**, **Update** and **Delete** request classes.
This **automatically documents** our API directly in the code:
```java
// Automatically generated. Don't modify!
package biz.softfor.user.api;

import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.DeleteRequest;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.UpdateRequest;
import java.lang.Integer;
import java.lang.String;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class UserGroupRequest {

  public static final String UPDATE_PATH = "/userGroup/update";
  public static final String UPDATE_METHOD = "POST";
  public static final String READ_PATH = "/userGroup/read";
  public static final String READ_METHOD = "POST";
  public static final String DELETE_PATH = "/userGroup/delete";
  public static final String DELETE_METHOD = "POST";
  public static final String CREATE_PATH = "/userGroup/create";
  public static final String CREATE_METHOD = "POST";

  @ToString(callSuper = true)
  @EqualsAndHashCode(callSuper = true)
  public static class Create extends CreateRequest<Integer, UserGroupRto> {

    public Create() {}

    public Create(UserGroupRto data) {
      super(data);
    }

  }

  @ToString(callSuper = true)
  @EqualsAndHashCode(callSuper = true)
  public static class Read extends ReadRequest<Integer, UserGroupFltr> {}

  @ToString(callSuper = true)
  @EqualsAndHashCode(callSuper = true)
  public static class Update extends UpdateRequest<Integer, UserGroupFltr, UserGroupRto> {

    public Update() {}

    public Update(UserGroupRto data) {
      super(data);
    }

    public Update(UserGroupRto data, List<String> fields) {
      super(data, fields);
    }

  }

  @ToString(callSuper = true)
  @EqualsAndHashCode(callSuper = true)
  public static class Delete extends DeleteRequest<Integer, UserGroupFltr> {}

}
```

Finally, response classes are generated using this template, for example for
**Role**:
```xml
// Automatically generated. Don't modify!
package biz.softfor.user.api;

import biz.softfor.util.api.CommonResponse;

public class RoleResponse extends CommonResponse<RoleDto> {}
```
Such responses are returned by the **create** and **read** methods.

## Usage example

— [biz.softfor.user.api](../biz.softfor.user.api).

- In the packages in which we will search for annotated **@Entity** and
**@RestController** classes, we create empty marker classes:
[biz.softfor.user.jpa.GenMarker](../biz.softfor.user.api/src/main/java/biz/softfor/user/jpa/GenMarker.java)
and [biz.softfor.user.spring.rest.GenMarker](../biz.softfor.user.api/src/main/java/biz/softfor/user/spring/rest/GenMarker.java).

- In the ```api``` package, we create a file **[package-info.java](../biz.softfor.user.api/src/main/java/biz/softfor/user/api/package-info.java)**,
in which we put the annotation **@GenApi** above the package, in which we specify our
marker classes, in the packages with which the processor will search for the annotations
**@Entity** and **@RestController**:
```java
@GenApi(
  value = { biz.softfor.user.jpa.GenMarker.class }
, restControllers = { biz.softfor.user.spring.rest.GenMarker.class }
)
package biz.softfor.user.api;

import biz.softfor.jpa.apigen.GenApi;
```

- In the [pom-file](../biz.softfor.user.api/pom.xml) of the project, we add
a dependency on our annotation processor:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.jpa.apigen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

- and from those very artifacts for which we generate our API, but we do this
in the section for **maven-compiler-plugin**:
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <dependencies>
        <!-- Here we list artifacts with @Entity and @RestController -->
        <dependency>
          <groupId>biz.softfor</groupId>
          <artifactId>biz.softfor.user.jpa</artifactId>
          <version>${biz.softfor.user.jpa.version}</version>
          <scope>runtime</scope>
        </dependency>
        <dependency>
          <groupId>biz.softfor</groupId>
          <artifactId>biz.softfor.user.spring.rest</artifactId>
          <version>${biz.softfor.user.spring.rest.version}</version>
          <scope>runtime</scope>
        </dependency>
      </dependencies>
    </plugin>
  </plugins>
</build>
```

## License

This project is licensed under the MIT License - see the [license.md](license.md) file for details.
