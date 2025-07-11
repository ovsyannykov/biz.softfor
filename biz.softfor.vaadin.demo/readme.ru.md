[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![UA](https://img.shields.io/badge/UA-yellow)](readme.ua.md)
[![EN](https://img.shields.io/badge/EN-blue)](readme.md)

<h1 align="center">biz.softfor.vaadin.demo</h1>

![Demo](doc/images/readme.png)

— [Vaadin](https://vaadin.com) приложение для демонстрации возможностей
фреймворка [softfor.biz](http://softfor.biz).

## Сборка и запуск

В силу не ясных до конца причин рекомендую первую сборку проекта выполнить
посредством команд в [rebuild.bat](rebuild.bat), а запуск - [run.bat](run.bat).
Последующие сборки и запуски проходят нормально, в том числе с помощью IDE и в
режиме отладки. Также используйте эти команды и при обновлении версии Vaadin.

## Описание

Приводимые скриншоты получены в результате запуска
[теста](src/test/java/biz/softfor/vaadin/user/AccessSeTest.java) с
использованием [Selenium](https://www.selenium.dev). Итак, что здесь происходит:

- При начальных для теста данных просмотр и редактирование справочника
***Cities*** доступны лишь пользователям группы ***CITIES_EDITORS***.
Таким образом для неавторизованных пользователей недоступны пункт меню
***Address* -> *Cities*** и колонка ***City*** в таблице ***Postcodes***:

![](doc/images/0.png)

- Логин пользователя ***admin***:

![](doc/images/1.png)

- Так как ***admin*** не включен в группу ***CITIES_EDITORS***, то данные
из таблицы ***Cities*** ему также недоступны:

![](doc/images/2.png)

- Переходим в меню ***Administration -> Groups***:

![](doc/images/3.png)

- Выбираем группу ***CITIES_EDITORS*** и видим, что в ней нет ни одного
пользователя:

![](doc/images/4.png)

- Двойным кликом или нажатием кнопки **Edit** открываем редактирование выбранной
записи:

![](doc/images/5.png)

- Открываем список пользователей, отсутствующих в группе ***CITIES_EDITORS***:

![](doc/images/7.png)

- В фильтр поля **Username** вводим имя пользователя, которого хотим найти:

![](doc/images/9.png)

- После нажатия кнопки **Filtrate** видим единственную строку, удовлетворяющую
введённому фильтру:

![](doc/images/10.png)

- Отмечаем её и нажимаем кнопку **Select**:

![](doc/images/12.png)

- В группу ***CITIES_EDITORS*** добавлен пользователь ***admin***:

![](doc/images/13.png)

- Члены группы ***CITIES_EDITORS*** могут как читать сущности ***City***, так и
обновлять их - т.е. имеют роли ***City*** и ***City (update)***:

![](doc/images/14.png)

- Нажимаем кнопку **Save**:

![](doc/images/15.png)

- После сохранения видим, что в группе ***CITIES_EDITORS*** есть пользователь
***admin***, и она содержит роли ***City*** и ***City (update)***:

![](doc/images/16.png)

- Для вступления в силу внесённых изменений пользователь должен выйти и войти в
приложение снова. Т.к. пункт меню ***Administration -> Groups*** недоступен
неавторизованным пользователям, то после выхода отображается, что такая страница
не найдена:

![](doc/images/17.png)

- После входа пользователя как ***admin***, ему доступен и пункт
***Address -> Cities***, и колонка ***Postcodes -> City***:

![](doc/images/18.png)

## Лицензия

Этот проект имеет лицензию MIT - подробности смотрите в файле [license.md](license.md).
