package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.BooleansEnum;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.util.api.filter.Value;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.data.renderer.Renderer;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.apache.commons.collections4.CollectionUtils;

public class BoolDbGridColumn<E, F extends FilterId>
extends ComboBoxDbGridColumn<E, Boolean, BooleansEnum, F> {

  public static <F extends FilterId>
  BiConsumer<F, MultiSelectComboBox<BooleansEnum>> defaultFilter(String dbName) {
    return (requestFilter, component) -> {
      Set<BooleansEnum> v = component.getValue();
      if(CollectionUtils.isNotEmpty(v)) {
        Object[] exprs = new Expr[v.size()];
        int i = 0;
        for(BooleansEnum be : v) {
          if(be.value == null) {
            exprs[i] = new Expr(Expr.IS_NULL, dbName);
          } else {
            exprs[i] = new Expr(Expr.EQUAL, dbName, new Value(be.value));
          }
          ++i;
        }
        requestFilter.andAnd(new Expr(Expr.OR, exprs));
      }
    };
  }

  public BoolDbGridColumn(
    String dbName
  , BooleansEnum[] items
  , Renderer<E> renderer
  , BiConsumer<F, MultiSelectComboBox<BooleansEnum>> filter
  ) {
    super(dbName, items, renderer, filter);
  }

  public BoolDbGridColumn
  (String dbName, BooleansEnum[] items, Function<E, Boolean> getter) {
    this(
      dbName
    , items
    , defaultRenderer(e -> BooleansEnum.of(getter.apply(e)))
    , defaultFilter(dbName)
    );
  }

}
