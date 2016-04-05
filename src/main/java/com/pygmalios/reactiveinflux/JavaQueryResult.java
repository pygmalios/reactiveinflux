package com.pygmalios.reactiveinflux;

import java.io.Serializable;

public interface JavaQueryResult extends Serializable {
    JavaQuery getQ();
    JavaResult getResult();
}
