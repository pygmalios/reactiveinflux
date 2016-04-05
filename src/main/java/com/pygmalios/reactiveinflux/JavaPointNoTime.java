package com.pygmalios.reactiveinflux;

import java.io.Serializable;
import java.util.Map;

public interface JavaPointNoTime extends Serializable {
    String getMeasurement();
    Map<String, String> getTags();
    Map<String, Object> getFields();
}
