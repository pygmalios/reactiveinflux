package com.pygmalios.reactiveinflux;

import java.util.List;

public interface JavaRow {
    JavaPointTime getTime();
    List<Object> getValues();
    String mkString();
}
