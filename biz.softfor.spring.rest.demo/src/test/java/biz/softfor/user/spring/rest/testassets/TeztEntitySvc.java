package biz.softfor.user.spring.rest.testassets;

import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class TeztEntitySvc
extends CrudSvc<Integer, TeztEntity, TeztEntityWor, TeztEntityFltr> {}
