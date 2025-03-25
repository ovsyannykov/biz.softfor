package biz.softfor.address.spring;

import biz.softfor.address.api.DistrictFltr;
import biz.softfor.address.jpa.District;
import biz.softfor.address.jpa.DistrictWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class DistrictSvc
extends CrudSvc<Integer, District, DistrictWor, DistrictFltr> {}
