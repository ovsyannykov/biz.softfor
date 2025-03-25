package biz.softfor.address.spring;

import biz.softfor.address.api.StateFltr;
import biz.softfor.address.jpa.State;
import biz.softfor.address.jpa.StateWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class StateSvc extends CrudSvc<Integer, State, StateWor, StateFltr> {}
