package biz.softfor.address.spring;

import biz.softfor.address.api.PostcodeFltr;
import biz.softfor.address.jpa.Postcode;
import biz.softfor.address.jpa.PostcodeWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class PostcodeSvc
extends CrudSvc<Integer, Postcode, PostcodeWor, PostcodeFltr> {}
