package biz.softfor.partner.spring;

import biz.softfor.partner.api.PartnerFileFltr;
import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerFileWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class PartnerFileSvc
extends CrudSvc<Long, PartnerFile, PartnerFileWor, PartnerFileFltr> {}
