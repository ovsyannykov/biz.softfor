[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)
[![Java CI with Maven](https://github.com/ovsyannykov/biz.softfor/actions/workflows/maven.yml/badge.svg)](https://github.com/ovsyannykov/biz.softfor/actions/workflows/maven.yml)

[![EN](https://img.shields.io/badge/EN-blue)](readme.md)
[![RU](https://img.shields.io/badge/RU-black)](readme.ru.md)

<h1 align="center">biz.softfor</h1>

![Demo](biz.softfor.vaadin.demo/doc/images/readme.png)

— це набір бібліотек, Spring Boot стартерів та процесорів анотацій для
швидкого створення CRUD-логіки сервісів та корпоративних додатків на основі JPA,
Hibernate, Spring Boot та Vaadin з простим визначенням складу запитуваних даних,
гнучкою фільтрацією під час читання, оновлення, видалення, детальним контролем
доступу.

**[SoftFor.Biz](http://softfor.biz)** - це максимальний результат за мінімальної
кількості необхідного коду та невисоких вимог до кваліфікації розробників.

**[SoftFor.Biz](http://softfor.biz)** дозволяє зосередитися на бізнес-логіці та
не втопити проект на старті у морі технологічних деталей.

- [biz.softfor.bom](biz.softfor.bom) - BOM (bill of materials).
- [biz.softfor.codegen](biz.softfor.codegen) - загальний код для кодогенеруючих
процесорів анотацій.
- [biz.softfor.jpa](biz.softfor.jpa) - бібліотека класів для створення
CRUD-сервісів на основі JPA та Hibernate.
- [biz.softfor.jpa.apigen](biz.softfor.jpa.apigen) - процесор анотацій,
що генерує класи для роботи з CRUD API: DTO, запитів та відповідей.
- [biz.softfor.jpa.filtergen](biz.softfor.jpa.filtergen) - процесор анотацій,
що генерує класи фільтрів для запитів читання, оновлення та видалення.
- [biz.softfor.jpa.withoutrelationsgen](biz.softfor.jpa.withoutrelationsgen) -
процесор анотацій, що генерує за певними правилами класи Entity без
@OneToMany, @ManyToOne та @ManyToMany анотацій.
- [biz.softfor.logback](biz.softfor.logback) - Spring Boot стартер, уніфікуючий
параметри логування додатків, до яких він включений як залежність.
- [biz.softfor.partner.api](biz.softfor.partner.api) - містить згенеровані
класи CRUD API (DTO, запитів та відповідей) для сутностей із пакетів проекту
[biz.softfor.partner.jpa](biz.softfor.partner.jpa).
- [biz.softfor.partner.api.filter](biz.softfor.partner.api.filter) - містить
згенеровані класи фільтрів для сутностей із пакетів проекту
[biz.softfor.partner.jpa](biz.softfor.partner.jpa).
- [biz.softfor.partner.i18n](biz.softfor.partner.i18n) - дані для генерації
файлів локалізації пакета [biz.softfor.partner.jpa](biz.softfor.partner.jpa).
- [biz.softfor.partner.jpa](biz.softfor.partner.jpa) - містить класи
JPA-сутностей демонстраційної предметної області "Партнер", такі як посади,
контакти, персональні дані та ін.
- [biz.softfor.partner.jpa.withoutrelations](biz.softfor.partner.jpa.withoutrelations) -
містить класи JPA-сутностей без відносин (relations) демонстраційної
предметної області "Партнер".
- [biz.softfor.partner.spring](biz.softfor.partner.spring) - Spring Boot стартер,
містить Spring-сервіси CRUD, що працюють з сутностями демонстраційної предметної
області "Партнер".
- [biz.softfor.partner.spring.rest](biz.softfor.partner.spring.rest) - Spring
Boot стартер, що містить REST контролери, що працюють із CRUD-сервісами
демонстраційної предметної області "Партнер".
- [biz.softfor.reflectionsutil](biz.softfor.reflectionsutil) - утиліта для
сканування та збереження у файл списку класів, анотованих заданою анотацією.
- [biz.softfor.spring](biz.softfor.spring) - Spring Boot стартер, що конфігурує
JSON-mapper, бін локалізації та визначає ряд констант.
- [biz.softfor.spring.i18nrest](biz.softfor.spring.i18nrest) - Spring Boot
стартер, який конфігурує локалізацію REST-сервісу через HTTP заголовок
"Accept-Language".
- [biz.softfor.spring.jpa.crud](biz.softfor.spring.jpa.crud) - Spring Boot
стартер, який конфігурує базовий клас CRUD-сервісів.
- [biz.softfor.spring.jpa.properties](biz.softfor.spring.jpa.properties) -
Spring Boot стартер, який уніфікує параметри за замовчуванням для конфігурації
JPA, Hibernate, Flyway та DataSource.
- [biz.softfor.spring.rest](biz.softfor.spring.rest) - Spring Boot стартер,
містить корисні біни для REST API, такі як healthcheck-контролер, обробник
помилок, логування запитів та ін.
- [biz.softfor.spring.rest.demo](biz.softfor.spring.rest.demo) -
демонстраційний REST-сервіс.
- [biz.softfor.spring.rest.pingdb.jpa](biz.softfor.spring.rest.pingdb.jpa) -
Spring Boot стартер, що містить healthcheck-контролер для бази даних.
- [biz.softfor.spring.restcontrollergen](biz.softfor.spring.restcontrollergen) -
процесор анотацій, що генерує класи REST-контролерів на основі CRUD-сервісів.
- [biz.softfor.spring.security](biz.softfor.spring.security) - Spring Boot
стартер, що містить біни та допоміжні методи для Spring Security.
- [biz.softfor.spring.security.service](biz.softfor.spring.security.service) -
Spring Boot стартер, що містить біни та допоміжні методи для Spring Security у
REST-сервісах.
- [biz.softfor.spring.servicegen](biz.softfor.spring.servicegen) - процесор
анотацій, що генерує класи CRUD-сервісів.
- [biz.softfor.spring.ws](biz.softfor.spring.ws) - Spring Boot стартер, що
полегшує побудову SOAP-сервісів.
- [biz.softfor.spring.ws.client.demo](biz.softfor.spring.ws.client.demo) -
демонстраційний SOAP-клієнт.
- [biz.softfor.spring.ws.demo](biz.softfor.spring.ws.demo) - демонстраційний
SOAP-сервер.
- [biz.softfor.testutil](biz.softfor.testutil) - набір корисних утиліт для
тестування.
- [biz.softfor.testutil.jpa](biz.softfor.testutil.jpa) - набір корисних утиліт
для тестування коду із використанням JPA.
- [biz.softfor.testutil.spring](biz.softfor.testutil.spring) - набір корисних
утиліт для тестування Spring Rest API.
- [biz.softfor.user.api](biz.softfor.user.api) - містить згенеровані класи CRUD
API (DTO, запитів та відповідей) для сутностей із пакетів проекту
[biz.softfor.user.jpa](biz.softfor.user.jpa).
- [biz.softfor.user.api.filter](biz.softfor.user.api.filter) - містить
згенеровані класи фільтрів для сутностей із пакетів проекту
[biz.softfor.user.jpa](biz.softfor.user.jpa).
- [biz.softfor.user.i18n](biz.softfor.user.i18n) - дані для генерації файлів
локалізації пакета [biz.softfor.user.jpa](biz.softfor.user.jpa).
- [biz.softfor.user.jpa](biz.softfor.user.jpa) - містить класи JPA-сутностей
предметної області "Користувач" - користувачі, групи користувачів, ролі та
токени.
- [biz.softfor.user.jpa.withoutrelations](biz.softfor.user.jpa.withoutrelations) -
містить класи JPA-сутностей без відносин (relations) предметної області
"Користувач".
- [biz.softfor.user.spring](biz.softfor.user.spring) - містить класи
JPA-сутностей без відносин (relations) предметної області "Користувач".
- [biz.softfor.user.spring.rest](biz.softfor.user.spring.rest) - Spring Boot
стартер, що містить Spring-сервіси CRUD, що працюють з сутностями предметної
області "Користувач".
- [biz.softfor.user.spring.ws](biz.softfor.user.spring.ws) - Spring Boot стартер,
містить SOAP-сервіси, що працюють з сутностями предметної області "Користувач".
- [biz.softfor.util](biz.softfor.util) - бібліотека утиліт, що використовуються
в фреймворку.
- [biz.softfor.util.i18n](biz.softfor.util.i18n) - дані для створення файлів
локалізації пакета утиліт [biz.softfor.util](biz.softfor.util).
- [biz.softfor.vaadin](biz.softfor.vaadin) - бібліотека компонентів та
допоміжних класів для швидкої реалізації CRUD-UI на базі Vaadin та
[biz.softfor.spring.jpa.crud](biz.softfor.spring.jpa.crud).
- [biz.softfor.vaadin.demo](biz.softfor.vaadin.demo) - Vaadin додаток для
демонстрації можливостей фреймворку.
- [biz.softfor.vaadin.demo.i18n](biz.softfor.vaadin.demo.i18n) - дані для
генерації файлів локалізації демонстраційного Vaadin-додатку.
- [biz.softfor.vaadin.i18n](biz.softfor.vaadin.i18n) - дані для генерації
файлів локалізації проекту [biz.softfor.vaadin](biz.softfor.vaadin).

## Ліцензія

Цей проект має ліцензію MIT - подробиці дивіться у файлі [license.md](license.md).
