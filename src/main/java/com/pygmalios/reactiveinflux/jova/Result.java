package com.pygmalios.reactiveinflux.jova;

import java.io.Serializable;
import java.util.List;

public interface Result extends Serializable {
    List<Series> getSeries();
}
