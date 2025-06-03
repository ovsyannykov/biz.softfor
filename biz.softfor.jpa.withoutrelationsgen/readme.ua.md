<p>
  <a href="license.md">
    <img src="https://img.shields.io/github/license/ovsyannykov/biz.softfor"/>
  </a>
</p>

<p>
  <a href="readme.md">EN</a>
  <a href="readme.ru.md">RU</a>
</p>
<h1 align="center">biz.softfor.jpa.withoutrelationsgen</h1>

— це **процесор анотацій** для генерації **Entity**-класів без анотацій
зв'язків **@ManyToOne**, **@OneToMany** та **@ManyToMany** з вихідних класів.

Ці класи потім використовуються для побудови і **більш економного** в порівнянні
з **Hibernate** виконання запитів створення та оновлення. Наприклад, щоб
змінити назву певної групи користувачів, ми повинні передати
Hibernate весь об'єкт "групи користувачів" з усіма користувачами, що входять
до нього, інакше ORM вирішує, що ми захотіли ще й очистити склад групи!
Це призводить як до надмірного обсягу даних у запитах та відповідях, так і до
логічних помилок взаємодії між фронтендом, бекендом та базою даних.

Клас, що генерується, містить маркерні інтерфейси, що дозволяють організувати
**вибіркову валідацію тільки змінених** полів:
```java
public class PartnerWor extends IdEntity<Long> implements Serializable {
  ...
  public interface PartnerName {}
  ...
  @NotBlank(groups = { Create.class, PartnerName.class })
  @Size(max = 63, groups = { Create.class, PartnerName.class })
  @Column
  private String partnerName;
  ...
}
```

Крім цього генеруються класи ***Request**, які містять класи запитів створення
**Create**, читання **Read**, оновлення **Update** та видалення **Delete**.
Це **автоматично документує** наше API безпосередньо в коді:
```java
// Automatically generated. Don't modify!
package biz.softfor.user.jpa;

import biz.softfor.user.api.UserGroupFltr;
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
  @ToString(
      callSuper = true
  )
  @EqualsAndHashCode(
      callSuper = true
  )
  public static class Create extends CreateRequest<Integer, UserGroupWor> {
    public Create() {
    }

    public Create(UserGroupWor data) {
      super(data);
    }
  }

  @ToString(
      callSuper = true
  )
  @EqualsAndHashCode(
      callSuper = true
  )
  public static class Read extends ReadRequest<Integer, UserGroupFltr> {
  }

  @ToString(
      callSuper = true
  )
  @EqualsAndHashCode(
      callSuper = true
  )
  public static class Update extends UpdateRequest<Integer, UserGroupFltr, UserGroupWor> {
    public Update() {
    }

    public Update(UserGroupWor data) {
      super(data);
    }

    public Update(UserGroupWor data, List<String> fields) {
      super(data, fields);
    }
  }

  @ToString(
      callSuper = true
  )
  @EqualsAndHashCode(
      callSuper = true
  )
  public static class Delete extends DeleteRequest<Integer, UserGroupFltr> {
  }
}
```

Для зв'язку **@ManyToMany** генерується ще два класи:
  1) **Entity**-клас для таблиці зв'язку та

```java
// Automatically generated. Don't modify!
package biz.softfor.user.jpa;

import biz.softfor.jpa.crud.querygraph.ManyToManyGeneratedLink;
import biz.softfor.util.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Generated("biz.softfor.user.jpa.Users_Groups")
@ManyToManyGeneratedLink
@Entity
@Table(
    name = "users_groups"
)
@IdClass(Users_Groups_Id.class)
@ToString
@EqualsAndHashCode
public class Users_Groups implements Serializable {
  public static final String USER_ID = "userId";

  public static final String GROUP_ID = "groupId";

  private static final long serialVersionUID = 0;

  @ManyToOne(
      fetch = FetchType.LAZY
  )
  @JoinColumn(
      name = "groupId",
      insertable = false,
      updatable = false
  )
  private UserGroup userGroup;

  @ManyToOne(
      fetch = FetchType.LAZY
  )
  @JoinColumn(
      name = "userId",
      insertable = false,
      updatable = false
  )
  private User user;

  @Id
  private Long userId;

  @Id
  private Integer groupId;

  public Users_Groups(Long userId, Integer groupId) {
    this.userId = userId;
    this.groupId = groupId;
  }

  public Users_Groups() {
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long v) {
    userId = v;
  }

  public Integer getGroupId() {
    return groupId;
  }

  public void setGroupId(Integer v) {
    groupId = v;
  }

  public interface UserId {
  }

  public interface GroupId {
  }
}
```
  2) та **``@IdClass``**-клас для її первинного ключа:

```java
// Automatically generated. Don't modify!
package biz.softfor.user.jpa;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.Long;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Users_Groups_Id implements Serializable {
  private static final long serialVersionUID = 0;

  private Long userId;

  private Integer groupId;

  public Users_Groups_Id(Long userId, Integer groupId) {
    this.userId = userId;
    this.groupId = groupId;
  }

  public Users_Groups_Id() {
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long v) {
    userId = v;
  }

  public Integer getGroupId() {
    return groupId;
  }

  public void setGroupId(Integer v) {
    groupId = v;
  }
}
```

Генерація **Wor**-класу проводиться за такими правилами:

- Клас створюється в тому ж пакеті, що й вихідний, до імені додається
суфікс **-```Wor```**:

  ```biz.softfor.user.jpa.Role => biz.softfor.user.jpa.Role```<b>```Wor```</b>

- Якщо вихідний клас успадковується від **Object**, то **Wor**-клас успадковується від
[Identifiable](../biz.softfor.jpa/src/main/java/biz/softfor/util/api/Identifiable.java),
інакше – від того ж класу, що й вихідний.

- Якщо у вихідному класі є анотація
[@ActionAccess](../biz.softfor.util/src/main/java/biz/softfor/util/security/ActionAccess.java)
або
[@UpdateAccess](../biz.softfor.util/src/main/java/biz/softfor/util/security/UpdateAccess.java),
вона додається з параметрами з вихідного класу.

- В анотації класу додається
[@Generated](../biz.softfor.util/src/main/java/biz/softfor/util/Generated.java)
з ім'ям вихідного класу.

- В анотації класу копіюються анотації **@Entity**, **@Table** та **@ToString**:

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
@Entity
@Table(name = "roles")
@ToString
public class RoleWor
implements Identifiable<Long>, Serializable {
```

</td>
</tr>
</table>

- У генерований клас додається три конструктори: 
1) Порожній конструктор без параметрів. 
2) Конструктор копіювання з об'єкта вихідного **Entity-класу**. 
3) Конструктор копіювання з об'єкта свого ж **Wor-класу**.

- Поля згенерованого **``Wor``**-класу забезпечуються геттерами, сеттерами та
анотаціями доступу **@ActionAccess** та **@UpdateAccess** (параметри яких
беруться з вихідного класу, тому що ми хочемо скопіювати обмеження без змін).
Для ясності у прикладах нижче вони опущені.

- Анотоване **@OneToOne** поле позначається анотацією **@Transient**,
та його тип замінюється на **``Wor``**-тип:

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
@Transient
private PartnerDetailsWor partnerDetails;
```

</td>
</tr>
</table>

- Анотоване **@ManyToOne** поле позначається анотацією **@Column**, ім'я поля
замінюється на ім'я з анотації **@JoinColumn**, а тип - на тип ключа зв'язку:

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
@Column
private Integer postcodeId;
```

</td>
</tr>
</table>

- Анотоване **@OneToMany** поле позначається анотацією **```@Transient```**
та трансформується у List ключів-ідентифікаторів:

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
@Transient
private List<Long> contactIds;
```

</td>
</tr>
</table>

- Анотоване **@ManyToMany** поле позначається анотацією **```@Transient```**
і трансформується в Set ключів-ідентифікаторів. На додаток до цього створюється
**``@OneToMany``**-поле для зв'язку з проміжною таблицею, заданою в
анотації **@JoinTable** вихідного файлу. Це поле є допоміжним,
виключно для ORM, тому немає ні сеттера, ні геттера, ні анотацій
доступу **@ActionAccess** та **@UpdateAccess**:

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
@Transient
private Set<Long> userIds;

@OneToMany(fetch = FetchType.LAZY)
@JoinColumn(
  name = "groupId",
  insertable = false,
  updatable = false
)
private Set<Users_Groups> users_groups;
```

</td>
</tr>
</table>

- Анотоване **@Column** поле копіюється незмінним, без анотацій пакету
**```jakarta.persistence.*```**, за винятком **@Column** і, якщо є, **@Temporal**:

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
@Column
@Temporal(TemporalType.DATE)
private LocalDate partnerRegdate;
```

</td>
</tr>
</table>

Як Ви могли помітити, анотації валідації доповнюються параметром **````groups```**
і відповідними інтерфейсами, що також генеруються в цьому класі. Це
дозволяє **вибірково валідувати лише змінені** поля.

## Приклад використання

— [biz.softfor.user.jpa.withoutrelations](../biz.softfor.user.jpa.withoutrelations).

- У пакеті з ім'ям, як у вихідного, створюємо файл **package-info.java**, у
якому над пакетом ставимо анотацію **@GenWithoutRelations** з ім'ям вихідного
пакету з Entity-класами:

```java
@GenWithoutRelations({ "biz.softfor.user.jpa" })
package biz.softfor.user.jpa;

import biz.softfor.jpa.withoutrelationsgen.GenWithoutRelations;
```

- У [pom-файлі](../biz.softfor.user.jpa.withoutrelations/pom.xml) проекту
додаємо залежність від нашого процесору анотацій:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.jpa.withoutrelationsgen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

## Ліцензія

Цей проект має ліцензію MIT - подробиці дивіться у файлі [license.md](license.md).
