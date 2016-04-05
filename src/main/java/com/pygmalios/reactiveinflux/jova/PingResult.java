package com.pygmalios.reactiveinflux.jova;

import java.io.Serializable;

public interface PingResult extends Serializable {
    String getInfluxDbVersion();
}
