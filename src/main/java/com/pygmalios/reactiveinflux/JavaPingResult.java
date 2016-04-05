package com.pygmalios.reactiveinflux;

import java.io.Serializable;

public interface JavaPingResult extends Serializable {
    String getInfluxDbVersion();
}
