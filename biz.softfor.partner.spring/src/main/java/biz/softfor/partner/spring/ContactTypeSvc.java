package biz.softfor.partner.spring;

import biz.softfor.partner.api.ContactTypeFltr;
import biz.softfor.partner.jpa.ContactType;
import biz.softfor.partner.jpa.ContactTypeWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class ContactTypeSvc
extends CrudSvc<Short, ContactType, ContactTypeWor, ContactTypeFltr> {}
