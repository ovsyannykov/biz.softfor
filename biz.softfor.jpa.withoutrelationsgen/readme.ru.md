<p>
  <a href="readme.md">EN</a>
  <a href="readme.ua.md">UA</a>
</p>
<h1 align="center">biz.softfor.jpa.withoutrelationsgen</h1>

— это **процессор аннотаций** для генерации **Entity**-классов без аннотаций
связей **@ManyToOne**, **@OneToMany** и **@ManyToMany** из исходных классов.

Эти классы затем используются для построения и **более экономного** по сравнению
с **Hibernate** выполнения запросов создания и обновления. Например, чтобы
изменить наименование определённой группы пользователей, мы должны передать
Hibernate весь объект "группы пользователей" со всеми входящими в него
пользователями, иначе ORM решает, что мы захотели ещё и очистить состав группы!
Это приводит как к избыточному объёму данных в запросах и ответах, так и к
логическим ошибкам взаимодействия между фронтендом, бекендом и базой данных.

Генерируемый класс содержит маркерные интерфейсы, позволяющие организовать
**выборочную валидацию только изменённых** полей:
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

Кроме этого генерируются классы ***Request**, содержащие классы запросов 
**Create**, чтения **Read**, обновления **Update** и удаления **Delete**.
Это **автоматически документирует** наше API непосредственно в коде:
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

Для связи **@ManyToMany** генерируется ещё два класса:
  1) **Entity**-класс для связующей таблицы и

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

  2) и **```@IdClass```**-класс для её первичного ключа:

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

Генерация **Wor**-класса производится по следующим правилам:

- Класс создаётся в том же пакете, что и исходный, к имени добавляется
суффикс **-```Wor```**:

  ```biz.softfor.user.jpa.Role => biz.softfor.user.jpa.Role```<b>```Wor```</b>

- Если исходный класс наследуется от **Object**, то **Wor**-класс наследуется от
[Identifiable](../biz.softfor.jpa/src/main/java/biz/softfor/util/api/Identifiable.java),
иначе - от того же класса, что и исходный.

- Если в исходном классе присутствует аннотация
[@ActionAccess](../biz.softfor.util/src/main/java/biz/softfor/util/security/ActionAccess.java)
или
[@UpdateAccess](../biz.softfor.util/src/main/java/biz/softfor/util/security/UpdateAccess.java),
то она добавляется с параметрами из исходного класса.

- В аннотации класса добавляется
[@Generated](../biz.softfor.util/src/main/java/biz/softfor/util/Generated.java)
с именем исходного класса.

- В аннотации класса копируются аннотации **@Entity**, **@Table** и **@ToString**:

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

- В генерируемый класс добавляется три конструктора:
  1) Пустой конструктор без параметров.
  2) Конструктор копирования из объекта исходного **Entity-класса**.
  3) Конструктор копирования из объекта своего же **Wor-класса**.

- Поля генерируемого **```Wor```**-класса снабжаются геттерами, сеттерами и
аннотациями доступа **@ActionAccess** и **@UpdateAccess** (параметры которых
берутся из исходного класса, так как мы хотим скопировать ограничения без
изиенений). Для ясности в примерах ниже они опущены.

- Аннотированное **@OneToOne** поле помечается аннотацией **@Transient**,
и его тип заменяется на **```Wor```**-тип:

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

- Аннотированное **@ManyToOne** поле помечается аннотацией **@Column**, имя
поля заменяется на имя из аннотации **@JoinColumn**, а тип - на тип ключа связи:

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

- Аннотированное **@OneToMany** поле помечается аннотацией **```@Transient```**
и трансформируется в List ключей-идентификаторов:

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

- Аннотированное **@ManyToMany** поле помечается аннотацией **```@Transient```**
и трансформируется в Set ключей-идентификаторов. В дополнение к этому создаётся
**```@OneToMany```**-поле для связи с промежуточной таблицей, заданной в
аннотации **@JoinTable** исходного файла. Это поле является вспомогательным,
исключительно для ORM, поэтому не имеет ни сеттера, ни геттера, ни аннотаций
доступа **@ActionAccess** и **@UpdateAccess**:

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

- Аннотированное **@Column** поле копируется неизменным, без аннотаций пакета
**```jakarta.persistence.*```**, исключая **@Column** и, если есть, **@Temporal**:

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

Как Вы могли заметить, аннотации валидации дополняются параметром **```groups```**
и соответствующими интерфейсами, также генерируемыми в этом классе. Это
позволяет **выборочно валидировать только изменённые** поля.

## Пример использования

— [biz.softfor.user.jpa.withoutrelations](../biz.softfor.user.jpa.withoutrelations).

- В пакете с именем, как у исходного, создаём файл **package-info.java**, в
котором над пакетом ставим аннотацию **@GenWithoutRelations** с именем исходного
пакета с Entity-классами:

```java
@GenWithoutRelations({ "biz.softfor.user.jpa" })
package biz.softfor.user.jpa;

import biz.softfor.jpa.withoutrelationsgen.GenWithoutRelations;
```

- В [pom-файле](../biz.softfor.user.jpa.withoutrelations/pom.xml) проекта
добавляем зависимость от нашего процессора аннотаций:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.jpa.withoutrelationsgen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

## Лицензия

Этот проект имеет лицензию MIT - подробности смотрите в файле [license.md](license.md).
