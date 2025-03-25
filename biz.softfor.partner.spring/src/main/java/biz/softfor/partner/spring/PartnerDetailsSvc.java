package biz.softfor.partner.spring;

import biz.softfor.partner.api.PartnerDetailsFltr;
import biz.softfor.partner.jpa.PartnerDetails;
import biz.softfor.partner.jpa.PartnerDetailsWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class PartnerDetailsSvc
extends CrudSvc<Long, PartnerDetails, PartnerDetailsWor, PartnerDetailsFltr> {}