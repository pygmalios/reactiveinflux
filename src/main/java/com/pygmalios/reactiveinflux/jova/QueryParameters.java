package com.pygmalios.reactiveinflux.jova;

public interface QueryParameters extends OptionalParameters {
    String getEpoch();
    Integer getChunkSize();
}
