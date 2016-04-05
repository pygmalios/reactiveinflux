package com.pygmalios.reactiveinflux;

import java.io.Closeable;
import java.util.concurrent.Future;

/**
 * Reactive client for InfluxDB.
 */
public interface JavaReactiveInflux extends Closeable {
    Future<JavaPingResult> ping();
    JavaReactiveInfluxDb database(String dbName);
    JavaReactiveInfluxConfig getConfig();
}
