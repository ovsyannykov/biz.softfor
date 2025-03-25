package biz.softfor.address.spring;

import biz.softfor.address.api.CountryFltr;
import biz.softfor.address.jpa.Country;
import biz.softfor.address.jpa.CountryWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class CountrySvc
extends CrudSvc<Short, Country, CountryWor, CountryFltr> {}
