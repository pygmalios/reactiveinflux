package com.pygmalios.reactiveinflux;

import java.io.Serializable;
import java.util.Map;

public interface JavaOptionalParameters extends Serializable {
    Map<String, String> getParams();
}
