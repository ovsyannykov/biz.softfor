@GenRestController(value = { User.class }, exclude = { Role.class, Token.class, User.class })
package biz.softfor.user.spring.rest;

import biz.softfor.spring.restcontrollergen.GenRestController;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.Token;
import biz.softfor.user.jpa.User;
