package com.pygmalios.reactiveinflux.jova;

import java.io.Closeable;
import java.util.concurrent.Future;

/**
 * Reactive client for InfluxDB.
 */
public interface ReactiveInflux extends Closeable {
    Future<PingResult> ping();
    ReactiveInfluxDb database(String dbName);
    ReactiveInfluxConfig getConfig();
}
