package com.pygmalios.reactiveinflux.jawa;

import com.pygmalios.reactiveinflux.jawa.PointTime;

import java.util.List;

public interface Row {
    PointTime getTime();
    List<Object> getValues();
    String mkString();
}
