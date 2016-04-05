package com.pygmalios.reactiveinflux.jawa;

import java.io.Serializable;

public interface PointTime extends Serializable {
    long getSeconds();
    int getNano();
}
