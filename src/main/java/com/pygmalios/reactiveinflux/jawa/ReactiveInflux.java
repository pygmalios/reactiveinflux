package com.pygmalios.reactiveinflux.jawa;

import play.libs.F;

import java.io.Closeable;

/**
 * Reactive client for InfluxDB.
 */
public interface ReactiveInflux extends Closeable {
    F.Promise<PingResult> ping();
    ReactiveInfluxDb database(String dbName);
    ReactiveInfluxConfig getConfig();
}
