@GenService(value = { User.class }, exclude = { Role.class, Token.class, UserGroup.class })
package biz.softfor.user.spring;

import biz.softfor.spring.servicegen.GenService;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.Token;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
