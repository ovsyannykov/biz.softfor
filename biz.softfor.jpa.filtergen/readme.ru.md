<p>
  <a href="license.md">
    <img src="https://img.shields.io/github/license/ovsyannykov/biz.softfor"/>
  </a>
</p>

<p>
  <a href="readme.md">EN</a>
  <a href="readme.ua.md">UA</a>
</p>
<h1 align="center">biz.softfor.jpa.filtergen</h1>

— это **процессор аннотаций** для генерации классов фильтров **```*Fltr```** из
**Entity**-классов. Классы фильтров затем включаются в классы запросов чтения,
обновления и удаления. Генерация производится по следующим правилам:

- Генерируемый класс получает имя как у исходного плюс **```Fltr```**, наследуется от
[FilterId](../biz.softfor.util/src/main/java/biz/softfor/util/api/filter/FilterId.java)
и размещается в пакете как у исходного с заменой **.jpa** на **.api**. Например:

  biz.softfor.user.jpa.Role => biz.softfor.user.<b>```api```</b>.Role<b>```Fltr```</b>

  Это позволяет избежать зависимости от JPA-библиотек при использовании
сгенерированных классов извне по отношению к нашим сервисам.

  **```FilterId```** содержит два поля:
  1) **```List<K> id```** - список идентификаторов (первичных ключей), который
затем преобразуется в SQL-выражение **IN(...)**.
  2) **```Object and```** - содержит [определённым образом](../biz.softfor.util/readme.ru.md)
сформированный объект, позволяющий задать условие произвольной сложности.

- В аннотации класса добавляется
[@Generated](../biz.softfor.util/src/main/java/biz/softfor/util/Generated.java)
с именем исходного класса и копируются все аннотации Entity-класса кроме
JPA-аннотаций (```jakarta.persistence.*```), [lombok](https://projectlombok.org)-аннотаций,
```@JsonFilter``` и
[@ActionAccess](../biz.softfor.util/src/main/java/biz/softfor/util/security/ActionAccess.java).
Но добавляются аннотации ```@ToString``` и ```@EqualsAndHashCode```:

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

- Аннотированное **@OneToOne** или **@ManyToOne** поле копируется без аннотаций
и с заменой типа на соответствующий **-```Fltr```** класс:

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

- Аннотированное **@OneToMany** или **@ManyToMany** поле копируется c
аннотацией **```@JsonIgnoreProperties```** и с заменой типа **```Set<T>```**
или **```List<T>```** на соответствующий **```T```** класс фильтра:

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

- Аннотированное **@Column** поле типа **String** копируется неизменным, но без
аннотаций.

- Аннотированное **@Column** поле типа **Enum** или аннотированное
[@Identifier](../biz.softfor.jpa/src/main/java/biz/softfor/jpa/Identifier.java)
преобразуется в список значений того же типа. Например:

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

- Аннотированное **@Column** поле типа **даты** или **Number**
преобразуются в **диапазоны** значений. Например:

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

- Класс фильтра снабжается также всеми **геттерами-сеттерами** и методом сброса
всех полей фильтра **```reset```**.

## Пример использования

— [biz.softfor.user.api](../biz.softfor.user.api.filter).

- В api-пакете создаём файл package-info.java, в котором над пакетом ставим
аннотацию **@GenFilter** с именем исходного пакета с Entity-классами:

```java
@GenFilter({ "biz.softfor.user.jpa" })
package biz.softfor.user.api;

import biz.softfor.jpa.filtergen.GenFilter;
```

- В [pom-файле](../biz.softfor.user.api.filter/pom.xml) проекта добавляем
зависимость от нашего процессора аннотаций:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.jpa.filtergen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

- Там же в секции **```<build>/<plugins>```** добавляем в зависимости к плагину
компилятора артефакт с исходными Entity-классами (biz.softfor.user.jpa):
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

## Лицензия

Этот проект имеет лицензию MIT - подробности смотрите в файле [license.md](license.md).
