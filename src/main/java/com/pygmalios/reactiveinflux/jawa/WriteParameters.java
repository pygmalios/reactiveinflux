package com.pygmalios.reactiveinflux.jawa;

import com.pygmalios.reactiveinflux.jawa.OptionalParameters;

public interface WriteParameters extends OptionalParameters {
    String getRetentionPolicy();
    String getPrecision();
    String getConsistency();
}
