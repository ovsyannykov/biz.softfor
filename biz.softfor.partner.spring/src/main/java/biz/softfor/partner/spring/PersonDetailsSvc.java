package biz.softfor.partner.spring;

import biz.softfor.partner.api.PersonDetailsFltr;
import biz.softfor.partner.jpa.PersonDetails;
import biz.softfor.partner.jpa.PersonDetailsWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class PersonDetailsSvc
extends CrudSvc<Long, PersonDetails, PersonDetailsWor, PersonDetailsFltr> {}
