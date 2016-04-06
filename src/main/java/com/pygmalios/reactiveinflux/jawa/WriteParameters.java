package com.pygmalios.reactiveinflux.jawa;

public interface WriteParameters extends OptionalParameters {
    String getRetentionPolicy();
    String getPrecision();
    String getConsistency();
}
