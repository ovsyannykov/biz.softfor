[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![EN](https://img.shields.io/badge/EN-blue)](readme.md)
[![RU](https://img.shields.io/badge/RU-black)](readme.ru.md)

<h1 align="center">biz.softfor.vaadin.demo</h1>

![Demo](doc/images/readme.png)

— [Vaadin](https://vaadin.com) додаток для демонстрації можливостей фреймворку
[softfor.biz](http://softfor.biz).

## Збирання та запуск

В силу незрозумілих до кінця причин рекомендую першу збірку проекту виконати
за допомогою команд [rebuild.bat](rebuild.bat), а запуск - [run.bat](run.bat).
Наступні зборки та запуски проходять нормально, у тому числі за допомогою IDE та
в режимі налагодження. Також використовуйте ці команди при оновленні версії
Vaadin.

## Опис

Скріншоти отримані в результаті запуску
[тесту](src/test/java/biz/softfor/vaadin/user/AccessSeTest.java) з використанням
[Selenium](https://www.selenium.dev). Отже, що тут відбувається:

- При початкових для тесту даних перегляд та редагування довідника ***Cities***
доступні лише користувачам групи ***CITIES_EDITORS***. Таким чином, для
неавторизованих користувачів недоступні пункт меню ***Address* -> *Cities*** та
колонка ***City*** у таблиці ***Postcodes***:

![](doc/images/0.png)

- Логін користувача ***admin***:

![](doc/images/1.png)

- Оскільки ***admin*** не включений до групи ***CITIES_EDITORS***, дані
з таблиці ***Cities*** йому також недоступні:

![](doc/images/2.png)

- Переходимо в меню ***Administration -> Groups***:

![](doc/images/3.png)

- Вибираємо групу ***CITIES_EDITORS*** і бачимо, що в ній немає жодного
користувача:

![](doc/images/4.png)

- Подвійним кліком або натисканням кнопки **Edit** відкриваємо редагування
обраного рядка:

![](doc/images/5.png)

- Відкриваємо список користувачів, які відсутні у групі ***CITIES_EDITORS***:

![](doc/images/7.png)

- У фільтр поля **Username** вводимо ім'я користувача, якого хочемо знайти:

![](doc/images/9.png)

- Після натискання кнопки **Filtrate** бачимо єдиний рядок, що задовольняє
введеному фільтру:

![](doc/images/10.png)

- Відзначаємо її та натискаємо кнопку **Select**:

![](doc/images/12.png)

- До групи ***CITIES_EDITORS*** доданий користувач ***admin***:

![](doc/images/13.png)

- Члени групи ***CITIES_EDITORS*** можуть як читати сутності ***City***, так і
оновлювати їх – тобто мають ролі ***City*** та ***City (update)***:

![](doc/images/14.png)

- Натискаємо кнопку **Save**:

![](doc/images/15.png)

- Після збереження бачимо, що у групі ***CITIES_EDITORS*** є користувач
***admin***, і вона містить ролі ***City*** та ***City (update)***:

![](doc/images/16.png)

- Для набуття чинності внесених змін користувач повинен вийти та увійти до
додатку знову. Т.к. пункт меню ***Administration -> Groups*** недоступний
неавторизованим користувачам, після виходу відображається, що така сторінка
не знайдена:

![](doc/images/17.png)

- Після входу користувача як ***admin***, йому доступний і пункт
***Address -> Cities***, і колонка ***Postcodes -> City***:

![](doc/images/18.png)

## Ліцензія

Цей проект має ліцензію MIT - подробиці дивіться у файлі [license.md](license.md).
