package com.pygmalios.reactiveinflux;

public interface JavaWriteParameters extends JavaOptionalParameters {
    String getRetentionPolicy();
    String getPrecision();
    String getConsistency();
}
