package com.pygmalios.reactiveinflux.jawa;

import java.io.Serializable;

public interface PingResult extends Serializable {
    String getInfluxDbVersion();
}
