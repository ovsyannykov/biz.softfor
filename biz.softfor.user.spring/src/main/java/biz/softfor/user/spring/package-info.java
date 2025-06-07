@GenService(value = { User.class }, exclude = { RoleSvc.class, TokenSvc.class, UserGroupSvc.class })
package biz.softfor.user.spring;

import biz.softfor.spring.servicegen.GenService;
import biz.softfor.user.jpa.User;
