package biz.softfor.partner.spring;

import biz.softfor.partner.api.PartnerFltr;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class PartnerSvc
extends CrudSvc<Long, Partner, PartnerWor, PartnerFltr> {}
