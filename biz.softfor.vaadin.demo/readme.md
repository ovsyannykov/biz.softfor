[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor)](license.md)

[![UA](https://img.shields.io/badge/UA-yellow)](readme.ua.md)
[![RU](https://img.shields.io/badge/RU-black)](readme.ru.md)

<h1 align="center">biz.softfor.vaadin.demo</h1>

![Demo](doc/images/readme.png)

— [Vaadin](https://vaadin.com) application for demonstrating the capabilities
of the [SoftFor.Biz](http://softfor.biz) framework.

## Build and run

For reasons that are not entirely clear, I recommend that you first build the
project using the commands in [rebuild.bat](rebuild.bat), and launch it using
[run.bat](run.bat). Subsequent builds and launches proceed normally, including
using the IDE and in debug mode. Also use these commands when updating the
Vaadin version.

## Description

The screenshots below are from running the
[test](src/test/java/biz/softfor/vaadin/user/AccessSeTest.java) using
[Selenium](https://www.selenium.dev). So what's going on here:

- With the initial data for the test, viewing and editing the ***Cities***
directory is available only to users of the ***CITIES_EDITORS*** group. Thus,
the menu item ***Address* -> *Cities*** and the ***City*** column in the
***Postcodes*** table are not available to unauthorized users:

![](doc/images/0.png)

- User ***admin*** is logging in:

![](doc/images/1.png)

- Since ***admin*** is not included in the ***CITIES_EDITORS*** group, the data
from the ***Cities*** table is also unavailable to him:

![](doc/images/2.png)

- Go to the menu ***Administration -> Groups***:

![](doc/images/3.png)

- We select the group ***CITIES_EDITORS*** and see that there are no users in it:

![](doc/images/4.png)

- Double-click or press the **Edit** button to open editing for the selected
entry:

![](doc/images/5.png)

- Open the list of users not in the ***CITIES_EDITORS*** group:

![](doc/images/7.png)

- In the **Username** field filter, enter the name of the user we want to find:

![](doc/images/9.png)

- After pressing the **Filtrate** button, we see the only line that satisfies
the entered filter:

![](doc/images/10.png)

- We mark it and press the **Select** button:

![](doc/images/12.png)

- User ***admin*** has been added to the ***CITIES_EDITORS*** group:

![](doc/images/13.png)

- Members of the ***CITIES_EDITORS*** group can both read ***City*** entities
and update them - i.e. they have the ***City*** and ***City (update)*** roles:

![](doc/images/14.png)

- Click the **Save** button:

![](doc/images/15.png)

- After saving, we see that the ***CITIES_EDITORS*** group has a user
***admin***, and it contains the roles ***City*** and ***City (update)***:

![](doc/images/16.png)

- For the changes to take effect, the user must log out and log in to the
application again. Since the menu item ***Administration -> Groups*** is not
available to unauthorized users, after logging out it is displayed that such a
page is not found:

![](doc/images/17.png)

- After the user logs in as ***admin***, he has access to both the
***Address -> Cities*** item and the ***Postcodes -> City*** column:

![](doc/images/18.png)

## License

This project is licensed under the MIT License - see the [license.md](license.md) file for details.
