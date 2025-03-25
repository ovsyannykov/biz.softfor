package biz.softfor.partner.spring;

import biz.softfor.partner.api.LocationTypeFltr;
import biz.softfor.partner.jpa.LocationType;
import biz.softfor.partner.jpa.LocationTypeWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class LocationTypeSvc
extends CrudSvc<Short, LocationType, LocationTypeWor, LocationTypeFltr> {}
