package biz.softfor.partner.spring;

import biz.softfor.partner.api.ContactDetailsFltr;
import biz.softfor.partner.jpa.ContactDetails;
import biz.softfor.partner.jpa.ContactDetailsWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class ContactDetailsSvc
extends CrudSvc<Long, ContactDetails, ContactDetailsWor, ContactDetailsFltr> {}
