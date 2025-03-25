<h1 align="center">biz.softfor.vaadin</h1>
<p align="right">
  <a href="readme.ua.md">UA</a>
  <a href="readme.md">EN</a>
</p>

![Demo](doc/images/readme.png)

— это фреймворк на базе компонентов [Vaadin](https://vaadin.com/components) для
построения CRUD-интерфейсов корпоративных приложений. Он предоставляет следующие
уникальные возможности:
- **не требует высокой квалификации** программистов для создания сложных интерфейсов; 
- **минимальное количество необходимого кода** для отображения и редактирования
сложных данных, включая ManyToMany, OneToMany, ManyToOne и OneToOne отношения;
- гибкое **разграничение доступа** к пунктам меню, просмотру и редактированию полей
данных;
- табличные компоненты с фильтрами и сортировкой;
- сквозная **локализация** приложения;
- готовые страницы логина, **регистрации и профиля** пользователя.

## Пример

Давайте создадим интерфейс для работы со справочником должностей:

![Contact Types](doc/images/Appointments.png)

Имеем Entity-класс:
```java
@Entity
@Table(name = Appointment.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("Appointment")
public class Appointment extends IdEntity<Short> implements Serializable {

  public final static String TABLE = "appointments";
  public final static String TITLE = "appointment";

  @Column
  @NotBlank
  @Size(min = 2, max = 63)
  private String name;

  @Column
  @NotBlank
  @Size(min = 2, max = 255)
  private String descr;

  private final static long serialVersionUID = 0L;

}
```

и сервис для работы с БД:
```java
@Service
public class AppointmentSvc
extends CrudSvc<Short, Appointment, AppointmentWor, AppointmentFltr> {}
```

Для реализации полноценного CRUD-интерфейса нам понадобятся следующие
компоненты Spring.

Форма для создания/редактирования/просмотра:
```java
@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AppointmentForm
extends EntityForm<Short, Appointment, AppointmentWor> {

  public AppointmentForm(SecurityMgr securityMgr, Validator validator) {
    super(
      Appointment.TITLE
    , new FormColumns(
        securityMgr
      , Appointment.class
      , AppointmentWor.class
      , new LinkedHashMap<String, Component>() {{
          TextField name = new TextField(Appointment_.NAME);
          put(Appointment_.NAME, name);
          TextField descr = new TextField(Appointment_.DESCR);
          put(Appointment_.DESCR, descr);
        }}
      )
    , validator
    );
  }

}
```
Базовый класс **EntityForm** реализует всю логику контроля доступа к полям,
отображения и валидации изменённых значений переданного списка компонентов
элементов управлени с метками, соответствующими именам полей редактируемого
объекта. **AppointmentWor** - класс без отношений (Without Relations),
созданный специальным працессором аннотаций.
В данном случае, т.к. исходный класс не содержит отношений, то
**AppointmentWor** практически идентичен исходному.

Далее зададим список колонок для табличного отображения:
```java
@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AppointmentDbGridColumns extends DbGridColumns<Short, Appointment> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, Appointment_.NAME));

  public AppointmentDbGridColumns(SecurityMgr securityMgr) {
    super(
      Appointment.TABLE
    , securityMgr
    , Appointment.class
    , new TextDbGridColumn<>(Appointment_.NAME, AppointmentFltr::setName)
    , new TextDbGridColumn<>(Appointment_.DESCR, AppointmentFltr::setDescr)
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
```

DbGridColumn-компоненты - это элементы управления для задания значений фильтров
по колонкам. Класс AppointmentFltr генерируется из Appointment специальным
процессором аннотаций. Предусмотрена также возможность фильтрации по более
сложным выражениям, чем по колонкам. Для этого конструктор DbGrid имеет параметр
filters, элементы которого располаются над компонентом Grid.

Компонент для отображения табличных данных:
```java
@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AppointmentsDbGrid
extends DbGrid<Short, Appointment, AppointmentWor> {

  public AppointmentsDbGrid
  (AppointmentSvc service, AppointmentDbGridColumns columns) {
    super(service, AppointmentRequest.Read.class, columns, DbGridColumns.EMPTY);
  }


}
```

И, наконец, то, что Вы видите на скриншоте:
```java
@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AnonymousAllowed
@Route(value = AppointmentsView.PATH, layout = MainLayout.class)
public class AppointmentsView
extends EntityView<Short, Appointment, AppointmentWor> {

  public final static String PATH = "appointment";

  public AppointmentsView
  (AppointmentsDbGrid dbGrid, AppointmentForm form, SecurityMgr securityMgr) {
    super(
      dbGrid
    , AppointmentRequest.Update.class
    , AppointmentRequest.Delete.class
    , GridFields.EMPTY
    , form
    , securityMgr
    );
  }

}
```
Здесь второй и третий параметры - классы для создания объектов соответствующих
запросов, а четвёртый - список элементов для отображения ManyToMany и OneToMany
отношений.

## Компоненты пакета biz.softfor.vaadin

- **BasicView** - базовый View-класс, реализующий  отображение заголовка текущей
страницы в браузере в зависимости от выбранного языка.
- **EntityForm** - отображает форму для создания и редактирования записи в
соответствии с заданными правами доступа и обеспечивает валидацию введённых
данных в соотвествии с аннотациями Java Bean Validation.
- **EntityFormColumns** - Map компонентов, разрешённых к просмотру и
редактированию.
- **EntityView** - отображает данные в табличной форме, включая ManyToMany и
OneToMany отношения, в соответствии с заданными правами доступа. Данные могут
быть отсортированы по нескольким колонкам, отфильтрованы как по колонке, так и
по содержимому нескольких колонок либо по отличному от используемого по
умолчанию методу.
- **LangSelector** - выбор языка интерфейса из списка, задаваемого в настройках.
- **MainLayout** - включает в себя меню, заголовок приложения, переключатель
языка, кнопки входа/выхода и регистрации/профиля.
- **MenuItemData** - содержит данные для создания иерархического меню.
- **NotFoundView** - компонент с сообщением, что запрашиваемая страница не найдена.
- **SideNavLocalized** - боковое меню.
- **Text** - общеупотребительные слова и фразы в корпоративных приложениях. Эти
строки используются как ключи для подсистемы интернационализации.

## Компоненты пакета biz.softfor.vaadin.dbgrid

- **DbGrid** - содержит табличное отображение данных с возможностями сортировки
и фильтрации, возможности удаления, создания и редактирования записей.
- **DbGridColumn** - базовый класс для компонентов, описывающих колонку DbGrid,
реализующих методы отображения и фильтрации данных.
- **DbGridColumns** - отфильтрованный в соответствии с правами доступа список
колонок для DbGrid.
- **BoolDbGridColumn** - компонент для отображения булевых значений, может иметь
состояние "не определено", необходимое для отображения и фильтрации
NULL-значений.
ComboBoxDbGridColumn
- **DateDbGridColumn** - компонент для отображения дат и фильтрации по диапазону.
- **ListDbGridColumn** - ComboBox для отображения и фильтрации наборов
значений, может иметь состояние "не определено", необходимое для отображения и
фильтрации NULL-значений.
- **ManyToOneDbGridColumn** - компонент для отображения и фильтрации
ManyToOne-колонки, иными словами - значения из справочника по идентификатору.
- **NumberDbGridColumn** - компонент для отображения и фильтрации Short, Integer
и Long значений.
- **TextDbGridColumn** - компонент для отображения и фильтрации строковых
значений.

## Компоненты пакета biz.softfor.vaadin.field

- **DateRangePicker** - выбор диапазона дат.
- **ManyToOneField** - выбор значения из справочника.
- **ToManyField** - отображение и редактирование списка OneToMany и ManyToMany
связей.

## Компоненты пакета biz.softfor.vaadin.field.grid

- **GridField** - содержит табличное отображение данных с возможностями
сортировки и фильтрации. В отличие от DbGrid работает с предварительно
считанными данными.
- **GridFields** - список полей GridField для отображения связанных *ToMany
записей совместно с DbGrid в составе EntityView.
- **GridFieldColumn** - базовый класс для компонентов, описывающих колонку
GridField, реализующих методы отображения и фильтрации данных.
- **GridFieldColumns** - отфильтрованный в соответствии с правами доступа список
колонок для GridField.
- **BoolGridFieldColumn** - компонент для отображения булевых значений, может
иметь состояние "не определено", необходимое для отображения и фильтрации
NULL-значений.
- **ListGridFieldColumn** - ComboBox для отображения и фильтрации наборов
значений, может иметь состояние "не определено", необходимое для отображения и
фильтрации NULL-значений.
- **NumberGridFieldColumn** - компонент для отображения и фильтрации Short,
Integer и Long значений.
- **TextGridFieldsColumn** - компонент для отображения и фильтрации строковых
значений.

## Компоненты пакета biz.softfor.vaadin.security

- **LoginView** - страница входа в приложение, собрана на базе Vaadin LoginForm
с добавлением локализации и возврата на исходную страницу, с которой
пользователь попытался войти.
- **ProfileView** - даёт возможность пользователю просмотривать и редактировать
собственные (и только собственные) данные. При этом пароль увидеть невозможно,
можно лишь ввести новый.
- **RegistrationView** - форма регистрации нового пользователя. При этом он
получает права доступа по умолчанию, изменить которые может администратор.
