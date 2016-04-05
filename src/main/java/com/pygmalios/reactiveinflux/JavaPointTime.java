package com.pygmalios.reactiveinflux;

import java.io.Serializable;

public interface JavaPointTime extends Serializable {
    long getSeconds();
    int getNano();
}
