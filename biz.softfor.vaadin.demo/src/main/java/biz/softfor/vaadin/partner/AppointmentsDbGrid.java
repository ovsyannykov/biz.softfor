package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.Appointment;
import biz.softfor.partner.jpa.AppointmentRequest;
import biz.softfor.partner.jpa.AppointmentWor;
import biz.softfor.partner.spring.AppointmentSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AppointmentsDbGrid
extends DbGrid<Short, Appointment, AppointmentWor> {

  public AppointmentsDbGrid
  (AppointmentSvc service, AppointmentDbGridColumns columns) {
    super(service, AppointmentRequest.Read.class, columns, DbGridColumns.EMPTY);
  }


}
