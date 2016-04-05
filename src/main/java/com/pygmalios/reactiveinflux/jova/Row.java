package com.pygmalios.reactiveinflux.jova;

import java.util.List;

public interface Row {
    PointTime getTime();
    List<Object> getValues();
    String mkString();
}
