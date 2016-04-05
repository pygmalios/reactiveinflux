package com.pygmalios.reactiveinflux;

public interface JavaQueryParameters extends JavaOptionalParameters {
    String getEpoch();
    Integer getChunkSize();
}
