package com.pygmalios.reactiveinflux.jawa;

import java.util.List;

public interface Row {
    PointTime getTime();
    List<Object> getValues();
    String mkString();
}
