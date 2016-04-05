package com.pygmalios.reactiveinflux.jawa.sync;

import com.pygmalios.reactiveinflux.jawa.PingResult;
import com.pygmalios.reactiveinflux.jawa.ReactiveInfluxConfig;

import java.io.Closeable;

/**
 * Reactive client for InfluxDB.
 */
public interface SyncReactiveInflux extends Closeable {
    PingResult ping();
    SyncReactiveInfluxDb database(String dbName);
    ReactiveInfluxConfig getConfig();
}
