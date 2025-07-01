[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![UA](https://img.shields.io/badge/UA-yellow)](readme.ua.md)
[![RU](https://img.shields.io/badge/RU-black)](readme.ru.md)

<h1 align="center">biz.softfor.jpa.withoutrelationsgen</h1>

— is an **annotation processor** for generating **Entity** classes without
**@ManyToOne**, **@OneToMany** and **@ManyToMany** annotations from source
classes.

These classes are then used to build and execute create and update queries in a
**more optimal** way than **Hibernate**. For example, to change the name of a
certain user group, we must pass to the Hibernate the entire "user group" object
with all the users in it, otherwise the ORM decides that we also want to clear
the group members! This leads to both excessive data volume in requests and
responses, and to logical errors in the interaction between the frontend,
backend and the database.

The generated class contains marker interfaces that allow you to organize
**selective validation of only changed** fields:

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

In addition, ***Request** classes are generated, containing **Create**,
**Read**, **Update** and **Delete** request classes. This **automatically
documents** our API directly in the code:
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

For the **@ManyToMany** relationship, two more classes are generated:
  1) The **Entity** class for the linking table:

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

  2) and the **```@IdClass```** class for its primary key:

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

The **-Wor** class is generated according to the following rules:

- The class is created in the same package as the original, the **-```Wor```**
suffix is ​​added to the name:

```biz.softfor.user.jpa.Role => biz.softfor.user.jpa.Role```<b>```Wor```</b>

- If the original class inherits from **Object**, then the **Wor** class
inherits from
[Identifiable](../biz.softfor.util/src/main/java/biz/softfor/util/api/Identifiable.java),
otherwise - from the same class as the original.

- If the source class contains the annotation
[@ActionAccess](../biz.softfor.util/src/main/java/biz/softfor/util/security/ActionAccess.java)
or
[@UpdateAccess](../biz.softfor.util/src/main/java/biz/softfor/util/security/UpdateAccess.java)
then it is added with the parameters from the source class.

- In the class annotations,
[@Generated](../biz.softfor.util/src/main/java/biz/softfor/util/Generated.java)
is added with the name of the source class.

- In the class annotations, the annotations **@Entity**, **@Table** and
**@ToString** are copied:

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

- Three constructors are added to the generated class:
  1) An empty constructor without parameters.
  2) A copy constructor from the object of the original **Entity class**.
  3) A copy constructor from the object of its same **-Wor class**.

- The fields of the generated **-```Wor```** class are supplied with getters,
setters, and access annotations **@ActionAccess** and **@UpdateAccess** (the
parameters of which are taken from the original class, since we want to copy
the constraints without changes). For clarity, they are omitted in the examples
below.

- The **@OneToOne** annotated field is marked with the **@Transient** annotation,
and its type is replaced with the **```Wor```**-type:

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

- The **@ManyToOne** annotated field is marked with the **@Column** annotation,
the field name is replaced with the name from the **@JoinColumn** annotation,
and the type is replaced with the type of the relationship key:

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

- An annotated **@OneToMany** field is marked with the annotation
**```@Transient```** and transformed into a List of identifier keys:

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

- Annotated **@ManyToMany** field is marked with the **```@Transient```**
annotation and transformed into a Set of identifier keys. In addition, a
**```@OneToMany```** field is created to link to the intermediate table
specified in the **@JoinTable** annotation of the source file. This field is
auxiliary, exclusively for ORM, so it has neither a setter, nor a getter, nor
the **@ActionAccess** and **@UpdateAccess** access annotations:

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

- An annotated **@Column** field is copied unchanged, without any annotations
from **```jakarta.persistence.*```** package, excluding **@Column** and, if
present, **@Temporal**:

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

As you may have noticed, the validation annotations are complemented by the
**```groups```** parameter and the corresponding interfaces, also generated in
this class. This allows you to **selectively validate only the changed** fields.

## Example of use

— [biz.softfor.user.jpa.withoutrelations](../biz.softfor.user.jpa.withoutrelations).

- In the jpa package, we create a file **package-info.java**, in which we put
the **@GenWithoutRelations** annotation above the package, in which we specify
the classes in the packages with which the processor will look for the
**@Entity** annotation:

```java
@GenWithoutRelations({ "biz.softfor.user.jpa" })
package biz.softfor.user.jpa;

import biz.softfor.jpa.withoutrelationsgen.GenWithoutRelations;
```

- In the [pom file](../biz.softfor.user.jpa.withoutrelations/pom.xml) of the
project we add a dependency on our annotation processor:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.jpa.withoutrelationsgen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

## License

This project is licensed under the MIT License - see the [license.md](license.md) file for details.
