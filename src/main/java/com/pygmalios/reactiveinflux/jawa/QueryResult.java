package com.pygmalios.reactiveinflux.jawa;

import java.io.Serializable;

public interface QueryResult extends Serializable {
    Query getQ();
    Result getResult();
}
