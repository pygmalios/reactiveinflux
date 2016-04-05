package com.pygmalios.reactiveinflux.jova;

import java.io.Serializable;

public interface Query extends Serializable {
    String getInfluxQl();
}
