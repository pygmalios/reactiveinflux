package com.pygmalios.reactiveinflux.jova;

import java.io.Serializable;
import java.util.Map;

public interface OptionalParameters extends Serializable {
    Map<String, String> getParams();
}
