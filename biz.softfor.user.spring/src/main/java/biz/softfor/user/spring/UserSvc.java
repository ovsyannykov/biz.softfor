package biz.softfor.user.spring;

import biz.softfor.spring.jpa.crud.CrudSvc;
import biz.softfor.user.api.UserFltr;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserWor;
import org.springframework.stereotype.Service;

@Service
public class UserSvc extends CrudSvc<Long, User, UserWor, UserFltr> {}
