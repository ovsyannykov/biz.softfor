[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![EN](https://img.shields.io/badge/EN-blue)](readme.md)
[![RU](https://img.shields.io/badge/RU-black)](readme.ru.md)

<h1 align="center">biz.softfor.jpa.apigen</h1>

— **процесор анотацій**, що генерує класи для роботи з сервісами, які
реалізують CRUD API (див. [biz.softfor.spring.jpa.crud](../biz.softfor.spring.jpa.crud)
та [biz.softfor.spring.servicegen](../biz.softfor.spring.servicegen)):
**DTO** (Data Transfer Object), **запитів** та **відповідей**.
Ці класи фактично і є специфікацією нашого API.

Генерація **Dto**-класу провадиться за наступними правилам:

- Клас створюється в пакеті з ім'ям, в якому **.jpa.** замінюється на **.api.**,
а до імені самого класу додається суфікс **-Dto**:

  ```biz.softfor.user.jpa.Role => biz.softfor.user```<b>```.api.```</b>```Role```<b>```Wor```</b>

- **Dto**-клас успадковується від
[HaveId](../biz.softfor.util/src/main/java/biz/softfor/util/api/HaveId.java).

- В анотації класу копіюється анотація **@ToString**:
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

- Поля генерованого **``Wor``**-класу забезпечуються геттерами, сеттерами та
константами з іменами полів. Для ясності у прикладах нижче вони опущені.

- Тип анотованого **@OneToOne** поля замінюється на **````Dto```**-клас:
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

- Тип анотованого **@ManyToOne** поля також замінюється на
**```Dto```**-клас, і додається ще одне поле, ім'я якого береться з
анотації **@JoinColumn**, а тип відповідає типу ключа зв'язку:
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
private PostcodeDto postcode;
```

</td>
</tr>
</table>

- Анотоване **@OneToMany** поле також перетворюється на два поля: List
Dto-об'єктів та List ключів-ідентифікаторів:
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

/**
 * The default value of this field is {@code null}, which means no changes when saving.
 */
private Set<Long> contactIds;
```

</td>
</tr>
</table>

- Анотоване **@ManyToMany** поле також перетворюється на два поля: Set
Dto-об'єктів та Set ключів-ідентифікаторів:
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

/**
 * The default value of this field is {@code null}, which means no changes when saving.
 */
private Set<Long> userIds;
```

</td>
</tr>
</table>

- Анотоване **@Column** поле копіюється незмінним, без анотацій пакету
**```jakarta.persistence.*```**:
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

Як Ви могли помітити, анотації валідації доповнюються параметром **```groups```**
і відповідними інтерфейсами, що також генеруються в цьому класі. Це дозволяє
**вибірково валідувати лише змінені** поля.

Крім цього за таким шаблоном генеруються класи ***Request**, що містять
класи запитів **Create**, **Read**, **Update** та **Delete**.
Це **автоматично документує** наше API безпосередньо у коді:
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
  public static class Create extends CreateRequest<Integer, UserGroupDto> {

    public Create() {}

    public Create(UserGroupDto data) {
      super(data);
    }

  }

  @ToString(callSuper = true)
  @EqualsAndHashCode(callSuper = true)
  public static class Read extends ReadRequest<Integer, UserGroupFltr> {}

  @ToString(callSuper = true)
  @EqualsAndHashCode(callSuper = true)
  public static class Update extends UpdateRequest<Integer, UserGroupFltr, UserGroupDto> {

    public Update() {}

    public Update(UserGroupDto data) {
      super(data);
    }

    public Update(UserGroupDto data, List<String> fields) {
      super(data, fields);
    }

  }

  @ToString(callSuper = true)
  @EqualsAndHashCode(callSuper = true)
  public static class Delete extends DeleteRequest<Integer, UserGroupFltr> {}

}
```

Нарешті, класи відповідей генеруються за таким шаблоном, наприклад **Role**:
```xml
// Automatically generated. Don't modify!
package biz.softfor.user.api;

import biz.softfor.util.api.CommonResponse;

public class RoleResponse extends CommonResponse<RoleDto> {}
```
Такі відповіді надаються методами **create** і **read**.

## Приклад використання

— [biz.softfor.user.api](../biz.softfor.user.api).

- У пакетах, в яких шукатимемо анотовані **@Entity** та
**@RestController** класи, створюємо порожні маркерні класи:
[biz.softfor.user.jpa.GenMarker](../biz.softfor.user.api/src/main/java/biz/softfor/user/jpa/GenMarker.java)
та [biz.softfor.user.spring.rest.GenMarker](../biz.softfor.user.api/src/main/java/biz/softfor/user/spring/rest/GenMarker.java).

- В ```api```-пакеті створюємо файл **[package-info.java](../biz.softfor.user.api/src/main/java/biz/softfor/user/api/package-info.java)**,
в якому над пакетом ставимо інструкцію **@GenApi**, в якій вказуємо наші
маркерні класи, у пакетах з якими процесор шукатиме анотації
**@Entity** та **@RestController**:
```java
@GenApi(
  value = { biz.softfor.user.jpa.GenMarker.class }
, restControllers = { biz.softfor.user.spring.rest.GenMarker.class }
)
package biz.softfor.user.api;

import biz.softfor.jpa.apigen.GenApi;
```

- У [pom-файлі](../biz.softfor.user.api/pom.xml) проекту додаємо залежність
від нашого процесора анотацій:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.jpa.apigen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

- і, власне, від тих самих артефактів, для яких ми генеруємо наш API, але
робимо це у секції для **maven-compiler-plugin**:
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <dependencies>
        <!-- Тут перераховуємо артефакти з @Entity та @RestController -->
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

## Ліцензія

Цей проект має ліцензію MIT - подробиці дивіться у файлі [license.md](license.md).
