package biz.softfor.partner.spring;

import biz.softfor.partner.api.AppointmentFltr;
import biz.softfor.partner.jpa.Appointment;
import biz.softfor.partner.jpa.AppointmentWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import org.springframework.stereotype.Service;

@Service
public class AppointmentSvc
extends CrudSvc<Short, Appointment, AppointmentWor, AppointmentFltr> {}
