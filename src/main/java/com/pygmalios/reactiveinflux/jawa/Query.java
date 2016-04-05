package com.pygmalios.reactiveinflux.jawa;

import java.io.Serializable;

public interface Query extends Serializable {
    String getInfluxQl();
}
