package com.pygmalios.reactiveinflux.jawa;

import java.io.Serializable;
import java.util.List;

public interface Result extends Serializable {
    List<Series> getSeries();
}
