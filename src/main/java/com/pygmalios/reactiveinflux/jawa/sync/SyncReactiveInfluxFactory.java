package com.pygmalios.reactiveinflux.jawa.sync;

import com.pygmalios.reactiveinflux.jawa.ReactiveInfluxConfig;
import com.pygmalios.reactiveinflux.sync.SyncReactiveInflux$;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import static com.pygmalios.reactiveinflux.jawa.Conversions.toJava;
import static com.pygmalios.reactiveinflux.jawa.Conversions.toScala;

public class SyncReactiveInfluxFactory {
    private SyncReactiveInfluxFactory() {
    }

    public static SyncReactiveInflux createSyncReactiveInflux(final ReactiveInfluxConfig javaConfig,
                                                              final long awaitAtMostMillis) {
        return toJava(SyncReactiveInflux$.MODULE$.apply(toScala(javaConfig)), durationFromMillis(awaitAtMostMillis));
    }

    private static Duration durationFromMillis(final long millis) {
        return FiniteDuration.fromNanos(millis*1000000);
    }
}
