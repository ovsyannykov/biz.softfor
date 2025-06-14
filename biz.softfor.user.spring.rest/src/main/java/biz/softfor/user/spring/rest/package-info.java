@GenRestController(value = { User.class }, exclude = { RoleCtlr.class, TokenCtlr.class, UserCtlr.class })
package biz.softfor.user.spring.rest;

import biz.softfor.spring.restcontrollergen.GenRestController;
import biz.softfor.user.jpa.User;
