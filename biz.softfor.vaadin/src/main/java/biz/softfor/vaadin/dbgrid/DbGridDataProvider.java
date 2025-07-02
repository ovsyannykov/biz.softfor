package biz.softfor.vaadin.dbgrid;

import biz.softfor.spring.jpa.crud.CrudSvc;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.Order;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.FilterId;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import java.util.List;
import java.util.stream.Stream;

public class DbGridDataProvider
<K extends Number, E extends Identifiable<K>, F extends FilterId<K>> {

  private final CrudSvc<K, E, ?, F> service;
  private final ReadRequest<K, F> request;

  public DbGridDataProvider(CrudSvc service, ReadRequest<K, F> request) {
    this.service = service;
    this.request = request;
  }

  public Stream<E> get(Query query) {
    request.sort.clear();
    List<QuerySortOrder> qso = query.getSortOrders();
    for(QuerySortOrder so : qso) {
      request.sort.add(new Order(
        so.getDirection() == SortDirection.DESCENDING
        ? Order.Direction.DESC : Order.Direction.ASC
      , so.getSorted()
      ));
    }
    request.setStartRow(query.getOffset());
    request.setRowsOnPage(query.getLimit());
    return service.read(request).getData().stream();
  }

}
