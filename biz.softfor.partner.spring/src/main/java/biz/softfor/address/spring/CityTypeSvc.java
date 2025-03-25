package biz.softfor.address.spring;

import biz.softfor.address.api.CityTypeFltr;
import biz.softfor.address.jpa.CityType;
import biz.softfor.address.jpa.CityTypeWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class CityTypeSvc
extends CrudSvc<Short, CityType, CityTypeWor, CityTypeFltr> {}
