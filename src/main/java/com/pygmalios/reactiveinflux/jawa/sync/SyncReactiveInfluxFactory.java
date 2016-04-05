package com.pygmalios.reactiveinflux.jawa.sync;

import com.pygmalios.reactiveinflux.ReactiveInfluxConfig$;
import com.pygmalios.reactiveinflux.jawa.ReactiveInfluxConfig;
import com.pygmalios.reactiveinflux.sync.SyncReactiveInflux$;
import scala.Option;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.net.URI;

import static com.pygmalios.reactiveinflux.jawa.Conversions.toJava;
import static com.pygmalios.reactiveinflux.jawa.Conversions.toScala;

public class SyncReactiveInfluxFactory {
    private SyncReactiveInfluxFactory() {
    }

    public static SyncReactiveInflux createSyncReactiveInflux(final ReactiveInfluxConfig javaConfig,
                                                              final long awaitAtMostMillis) {
        return toJava(SyncReactiveInflux$.MODULE$.apply(toScala(javaConfig)), durationFromMillis(awaitAtMostMillis));
    }

    public static ReactiveInfluxConfig createReactiveInfluxConfig(final URI url) {
        return createReactiveInfluxConfig(url, null, null);
    }

    public static ReactiveInfluxConfig createReactiveInfluxConfig(final URI url,
                                                                  final String username,
                                                                  final String password) {
        return toJava(ReactiveInfluxConfig$.MODULE$.apply(url, Option.<String>apply(username), Option.<String>apply(password)));
    }

    private static Duration durationFromMillis(final long millis) {
        return FiniteDuration.fromNanos(millis*1000000);
    }
}
