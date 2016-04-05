package com.pygmalios.reactiveinflux.jova;

public interface WriteParameters extends OptionalParameters {
    String getRetentionPolicy();
    String getPrecision();
    String getConsistency();
}
