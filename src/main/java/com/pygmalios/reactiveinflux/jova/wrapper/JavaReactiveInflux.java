package com.pygmalios.reactiveinflux.jova.wrapper;

import com.pygmalios.reactiveinflux.jova.PingResult;
import com.pygmalios.reactiveinflux.jova.ReactiveInflux;
import com.pygmalios.reactiveinflux.jova.ReactiveInfluxConfig;
import com.pygmalios.reactiveinflux.jova.ReactiveInfluxDb;

import java.io.IOException;
import java.util.concurrent.Future;

class JavaReactiveInflux implements ReactiveInflux {
    private final com.pygmalios.reactiveinflux.ReactiveInflux reactiveInflux;

    JavaReactiveInflux(final com.pygmalios.reactiveinflux.ReactiveInflux reactiveInflux) {
        this.reactiveInflux = reactiveInflux;
    }

    @Override
    public final Future<PingResult> ping() {
        return null;
    }

    @Override
    public final ReactiveInfluxDb database(final String dbName) {
        return null;
    }

    @Override
    public final ReactiveInfluxConfig getConfig() {
        return null;
    }

    @Override
    public void close() throws IOException {
    }
}
