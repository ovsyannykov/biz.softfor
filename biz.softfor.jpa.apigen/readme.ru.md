[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![UA](https://img.shields.io/badge/UA-yellow)](readme.ua.md)
[![EN](https://img.shields.io/badge/EN-blue)](readme.md)

<h1 align="center">biz.softfor.jpa.apigen</h1>

— **процессор аннотаций**, генерирующий классы для работы с сервисами,
реализующими CRUD API (см. [biz.softfor.spring.jpa.crud](../biz.softfor.spring.jpa.crud)
и [biz.softfor.spring.servicegen](../biz.softfor.spring.servicegen)):
**DTO** (Data Transfer Object), **запросов** и **ответов**.
Эти классы фактически и являются спецификацией нашего API.

Генерация **Dto**-класса производится по следующим правилам:

- Класс создаётся в пакете с именем, в котором **.jpa.** заменяется на **.api.**,
а к имени самого класса добавляется суффикс **-Dto**:

  ```biz.softfor.user.jpa.Role => biz.softfor.user```<b>```.api.```</b>```Role```<b>```Dto```</b>

- **Dto**-класс наследуется от
[HaveId](../biz.softfor.util/src/main/java/biz/softfor/util/api/HaveId.java).

- В аннотации класса копируется аннотация **@ToString**:
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

- Поля генерируемого **```Dto```**-класса снабжаются геттерами, сеттерами и
константами с именами полей. Для ясности в примерах ниже они опущены.

- Тип аннотированного **@OneToOne** поля заменяется на **```Dto```**-класс:
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

- Тип аннотированного **@ManyToOne** поля также заменяется на **```Dto```**-класс:
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

- Аннотированное **@OneToMany** поле преобразуется в List ```Dto```-объектов:
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

- Аннотированное **@ManyToMany** поле преобразуется в Set ```Dto```-объектов:
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

- Аннотированное **@Column** поле копируется неизменным, без аннотаций пакета
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

Как Вы могли заметить, аннотации валидации дополняются параметром **```groups```**
и соответствующими интерфейсами, также генерируемыми в этом классе. Это
позволяет **выборочно валидировать только изменённые** поля.

В запросах **Create** и **Update** для аннотированных @ManyToOne, @OneToMany и
@ManyToMany полей удобнее использовать идентификаторы. Для этого генерируются
классы **Request-data Transfer Objects** - ```-Rto```:

- Класс создаётся в пакете с именем, в котором **.jpa.** заменяется на **.api.**,
а к имени самого класса добавляется суффикс **-Rto**:

  ```biz.softfor.user.jpa.Role => biz.softfor.user```<b>```.api.```</b>```Role```<b>```Rto```</b>

- **Rto**-класс наследуется от
[HaveId](../biz.softfor.util/src/main/java/biz/softfor/util/api/HaveId.java).

- В аннотации класса копируется аннотация **@ToString**:
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

- Поля генерируемого **```Rto```**-класса снабжаются геттерами, сеттерами и
константами с именами полей. Для ясности в примерах ниже они опущены.

- Тип аннотированного **@OneToOne** поля заменяется на **```Rto```**-класс:
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

- Аннотированное **@ManyToOne** поле также заменяется на идентификатор
соответствующего ключу связи типа с взятым из аннотации **@JoinColumn** именем:
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

- Аннотированное **@OneToMany** поле преобразуется в List ключей-идентификаторов:
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

- Аннотированное **@ManyToMany** поле преобразуется в Set ключей-идентификаторов:
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

- Аннотированное **@Column** поле копируется неизменным, без аннотаций пакета
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

В ```Rto```-классах также аннотации валидации дополняются параметром **```groups```**
и соответствующими интерфейсами, генерируемыми в этом классе. Это позволяет
**выборочно валидировать только изменённые** поля.

Классы ***Request**, содержащие классы запросов **Create**, **Read**, **Update**
и **Delete**, генерируются по такому вот шаблону.
Это **автоматически документирует** наше API непосредственно в коде:
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

Наконец, классы ответов генерируются по такому шаблону, например для **Role**:
```xml
// Automatically generated. Don't modify!
package biz.softfor.user.api;

import biz.softfor.util.api.CommonResponse;

public class RoleResponse extends CommonResponse<RoleDto> {}
```
Такие ответы отдаются методами **create** и **read**.

## Пример использования

— [biz.softfor.user.api](../biz.softfor.user.api).

- В пакетах, в которых будем искать аннотированные **@Entity** и
**@RestController** классы, создаём пустые маркерные классы:
[biz.softfor.user.jpa.GenMarker](../biz.softfor.user.api/src/main/java/biz/softfor/user/jpa/GenMarker.java)
и [biz.softfor.user.spring.rest.GenMarker](../biz.softfor.user.api/src/main/java/biz/softfor/user/spring/rest/GenMarker.java).

- В ```api```-пакете создаём файл **[package-info.java](../biz.softfor.user.api/src/main/java/biz/softfor/user/api/package-info.java)**,
в котором над пакетом ставим аннотацию **@GenApi**, в которой указываем наши
маркерные классы, в пакетах с которыми процессор будет искать аннотации
**@Entity** и **@RestController**:
```java
@GenApi(
  value = { biz.softfor.user.jpa.GenMarker.class }
, restControllers = { biz.softfor.user.spring.rest.GenMarker.class }
)
package biz.softfor.user.api;

import biz.softfor.jpa.apigen.GenApi;
```

- В [pom-файле](../biz.softfor.user.api/pom.xml) проекта добавляем зависимость
от нашего процессора аннотаций:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.jpa.apigen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

- и, собственно, от тех самых артефактов, для которых мы генерируем наш API, но
делаем это в секции для **maven-compiler-plugin**:
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <dependencies>
        <!-- Здесь перечисляем артефакты с @Entity и @RestController -->
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

## Лицензия

Этот проект имеет лицензию MIT - подробности смотрите в файле [license.md](license.md).
