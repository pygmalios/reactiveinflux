package com.pygmalios.reactiveinflux.jova;

import java.io.Serializable;

public interface PointTime extends Serializable {
    long getSeconds();
    int getNano();
}
