package com.pygmalios.reactiveinflux.jawa;

public interface QueryParameters extends OptionalParameters {
    String getEpoch();
    Integer getChunkSize();
}
