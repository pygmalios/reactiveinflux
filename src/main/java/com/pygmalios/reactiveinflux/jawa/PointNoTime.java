package com.pygmalios.reactiveinflux.jawa;

import java.io.Serializable;
import java.util.Map;

public interface PointNoTime extends Serializable {
    String getMeasurement();
    Map<String, String> getTags();
    Map<String, Object> getFields();
}
