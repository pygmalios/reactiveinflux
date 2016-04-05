package com.pygmalios.reactiveinflux.jawa;

import java.io.Serializable;
import java.util.Map;

public interface OptionalParameters extends Serializable {
    Map<String, String> getParams();
}
