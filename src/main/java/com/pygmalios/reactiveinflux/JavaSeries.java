package com.pygmalios.reactiveinflux;

import java.io.Serializable;
import java.util.List;

public interface JavaSeries extends Serializable {
    String getName();
    List<String> getColumns();
    List<JavaRow> getRows();
}
