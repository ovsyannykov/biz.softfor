package biz.softfor.partner.spring;

import biz.softfor.partner.api.ContactFltr;
import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.ContactWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class ContactSvc
extends CrudSvc<Long, Contact, ContactWor, ContactFltr> {}
