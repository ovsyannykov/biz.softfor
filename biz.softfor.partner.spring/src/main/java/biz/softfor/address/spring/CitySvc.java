package biz.softfor.address.spring;

import biz.softfor.address.api.CityFltr;
import biz.softfor.address.jpa.City;
import biz.softfor.address.jpa.CityWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class CitySvc extends CrudSvc<Integer, City, CityWor, CityFltr> {}
