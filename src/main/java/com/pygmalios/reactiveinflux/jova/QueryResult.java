package com.pygmalios.reactiveinflux.jova;

import java.io.Serializable;

public interface QueryResult extends Serializable {
    Query getQ();
    Result getResult();
}
