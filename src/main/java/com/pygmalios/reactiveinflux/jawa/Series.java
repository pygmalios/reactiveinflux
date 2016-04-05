package com.pygmalios.reactiveinflux.jawa;

import java.io.Serializable;
import java.util.List;

public interface Series extends Serializable {
    String getName();
    List<String> getColumns();
    List<Row> getRows();
}
