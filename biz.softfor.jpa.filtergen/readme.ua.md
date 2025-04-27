<p>
  <a href="readme.md">EN</a>
  <a href="readme.ru.md">RU</a>
</p>
<h1 align="center">biz.softfor.jpa.filtergen</h1>

— це **процесор анотацій** для генерації класів фільтрів **```*Fltr```** із
**Entity**-класів. Класи фільтрів потім включаються до класів запитів читання,
оновлення та видалення. Генерація робиться за такими правилами:

- Згенерований клас отримує ім'я як у вихідного плюс **```Fltr```**, успадковується від
[FilterId](../biz.softfor.util/src/main/java/biz/softfor/util/api/filter/FilterId.java)
та розміщується у пакеті як у вихідного із заміною **.jpa** на **.api**. Наприклад:

biz.softfor.user.jpa.Role => biz.softfor.user.<b>```api```</b>.Role<b>```Fltr```</b>

Це дозволяє уникнути залежності від JPA-бібліотек під час використання
згенерованих класів ззовні до наших сервісів.

**```FilterId```** містить два поля:
1) **```List<K> id```** - список ідентифікаторів (первинних ключів), який
потім перетворюється на SQL-вираз **IN(...)**.
2) **```Object and```** - містить [певним чином](../biz.softfor.util/readme.ru.md)
сформований об'єкт, що дозволяє задати умову довільної складності.

- В анотації класу додається
[@Generated](../biz.softfor.util/src/main/java/biz/softfor/util/Generated.java)
з ім'ям вихідного класу та копіюються всі анотації Entity-класу крім JPA-анотацій
(```jakarta.persistence.*```), [lombok](https://projectlombok.org)-анотацій,
```@JsonFilter``` та
[@ActionAccess](../biz.softfor.util/src/main/java/biz/softfor/util/security/ActionAccess.java).
Але додаються анотації ```@ToString``` і ```@EqualsAndHashCode```:

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

- Анотоване **@OneToOne** або **@ManyToOne** поле копіюється без анотацій та із
заміною типу на відповідний **-````Fltr```** клас:

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

- Анотоване **@OneToMany** або **@ManyToMany** поле копіюється з
анотацією **```@JsonIgnoreProperties```** і з заміною типу **```Set<T>```**
або **```List<T>```** на відповідний **```T```** клас фільтра:

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

- Анотоване **@Column** поле типу **String** копіюється незмінним, але без
анотацій.

- Анотоване **@Column** поле типу **Enum** або анотоване
[@Identifier](../biz.softfor.jpa/src/main/java/biz/softfor/jpa/Identifier.java)
перетворюється на список значень того ж типу. Наприклад:

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

- Анотоване **@Column** поле типу **дати** або **Number**
перетворюються на **діапазони** значень. Наприклад:

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

- Клас фільтра забезпечується також усіма **геттерами-сеттерами** та методом
скидання всіх полів фільтра **```reset```**.

## Приклад використання

— [biz.softfor.user.api](../biz.softfor.user.api.filter).

- У api-пакеті створюємо файл package-info.java, у якому над пакетом ставимо
анотацію **@GenFilter** з ім'ям вихідного пакета з Entity-класами:

```java
@GenFilter({ "biz.softfor.user.jpa" })
package biz.softfor.user.api;

import biz.softfor.jpa.filtergen.GenFilter;
```

- У [pom-файлі](../biz.softfor.user.api.filter/pom.xml) проекту додаємо
залежність від нашого процесора анотацій:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.jpa.filtergen</artifactId>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

- Там же в секції **```<build>/<plugins>```** додаємо залежність до плагіна
компілятора артефакт із вихідними Entity-класами (biz.softfor.user.jpa):
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

## Ліцензія

Цей проект має ліцензію MIT - подробиці дивіться у файлі [license.md](license.md).
