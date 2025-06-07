[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![UA](https://img.shields.io/badge/UA-yellow)](readme.ua.md)
[![RU](https://img.shields.io/badge/RU-black)](readme.ru.md)

<h1 align="center">biz.softfor.jpa.filtergen</h1>

— is an **annotation processor** for generating filter classes **```*Fltr```**
from **Entity** classes. Filter classes are then included in read, update
and delete request classes. Generation is done according to the following rules:

- The generated class gets the same name as the original plus **```Fltr```**, inherits from
[FilterId](../biz.softfor.util/src/main/java/biz/softfor/util/api/filter/FilterId.java)
and is placed in the same package as the original with **.jpa** replaced by **.api**.
For example:

biz.softfor.user.jpa.Role => biz.softfor.user.<b>```api```</b>.Role<b>```Fltr```</b>

This allows us to avoid dependence on JPA libraries when using
generated classes externally to our services.

**```FilterId```** contains two fields:
1) **```List<K> id```** - a list of identifiers (primary keys), which is
then converted to the SQL expression **IN(...)**.
2) **```Object and```** - contains a [specifically](../biz.softfor.util/readme.ru.md)
generated object, allowing us to set a condition of arbitrary complexity.

- In the class annotations,
[@Generated](../biz.softfor.util/src/main/java/biz/softfor/util/Generated.java)
is added with the name of the original class and all annotations of the Entity
class are copied except for JPA annotations (```jakarta.persistence.*```),
[lombok](https://projectlombok.org) annotations, ```@JsonFilter``` and
[@ActionAccess](../biz.softfor.util/src/main/java/biz/softfor/util/security/ActionAccess.java).
But the annotations ```@ToString``` and ```@EqualsAndHashCode``` are added:

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
@Generated("biz.softfor.user.jpa.Role")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RoleFltr extends FilterId<Long> {
```

</td>
</tr>
</table>

- An annotated by **@OneToOne** or **@ManyToOne** field is copied without annotations
and with the type replaced with the corresponding **-```Fltr```** class:

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
private PartnerDetailsFltr partnerDetails;
```

</td>
</tr>
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
private PostcodeFltr postcode;
```

</td>
</tr>
</table>

- An annotated by **@OneToMany** or **@ManyToMany** field is copied with the
**```@JsonIgnoreProperties```** annotation and with the **```Set<T>```**
or **```List<T>```** type replaced with the corresponding **```T```** filter class:

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
(value = "partner", allowSetters = true)
private ContactFltr contacts;
```

</td>
</tr>
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
(value = { "groups" }, allowSetters = true)
@ToString.Exclude
@EqualsAndHashCode.Exclude
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
private UserFltr users;
```

</td>
</tr>
</table>

- An annotated by **@Column** field of type **String** is copied unchanged,
but without annotations.

- An annotated by **@Column** field of type **Enum** or annotated by
[@Identifier](../biz.softfor.jpa/src/main/java/biz/softfor/jpa/Identifier.java)
is converted to a List of values ​​of the same type. For example:

<table border="0">
<tr>
<td>

```java
@Column
@NotNull
private PartnerType typ;
```

</td>
<td>
=>
</td>
<td>

```java
private List<PartnerType> typ;
```

</td>
</tr>
</table>

- Annotated by **@Column** fields of type **date** or **Number** are converted
to **ranges** of values. For example:

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
private Range<LocalDate> partnerRegdate;
```

</td>
</tr>
</table>

- The filter class is also supplied with all **getters-setters** and a method
for resetting all filter fields **```reset```**.

## Example of use

— [biz.softfor.user.api](../biz.softfor.user.api.filter).

- In the api package, we create a file **package-info.java**, in which we put
the **@GenFilter** annotation above the package, in which we specify the classes
in the packages with which the processor will look for the **@Entity** annotation:

```java
@GenFilter({ biz.softfor.user.jpa.GenFilterMarker.class })
package biz.softfor.user.api;

import biz.softfor.jpa.filtergen.GenFilter;
```

Since we do not want to have the original Entity classes in dependencies, we create
another corresponding package with a single empty GenFilterMarker interface.

- In the [pom file](../biz.softfor.user.api.filter/pom.xml) of the project,
we add a dependency on our annotation processor:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.jpa.filtergen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

- There in the section **```<build>/<plugins>```** we add as a dependency to the
compiler plugin an artifact with the original Entity classes (biz.softfor.user.jpa):
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <dependencies>
        <dependency>
          <groupId>biz.softfor</groupId>
          <artifactId>biz.softfor.user.jpa</artifactId>
          <version>${biz.softfor.user.jpa.version}</version>
          <scope>runtime</scope>
        </dependency>
      </dependencies>
    </plugin>
  </plugins>
</build>
```

## License

This project is licensed under the MIT License - see the [license.md](license.md) file for details.
