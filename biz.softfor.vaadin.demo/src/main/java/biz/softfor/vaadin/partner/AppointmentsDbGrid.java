package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.Appointment;
import biz.softfor.partner.jpa.AppointmentWor;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AppointmentsDbGrid
extends DbGrid<Short, Appointment, AppointmentWor> {

  public AppointmentsDbGrid(AppointmentDbGridColumns columns) {
    super(Appointment.class, columns, DbGridColumns.EMPTY);
  }


}
