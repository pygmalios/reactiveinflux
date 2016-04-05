package com.pygmalios.reactiveinflux.jova;

import java.io.Serializable;
import java.util.List;

public interface Series extends Serializable {
    String getName();
    List<String> getColumns();
    List<Row> getRows();
}
