package com.pygmalios.reactiveinflux.examples;

import com.pygmalios.reactiveinflux.jawa.ReactiveInfluxConfig;
import com.pygmalios.reactiveinflux.jawa.sync.SyncReactiveInflux;
import com.pygmalios.reactiveinflux.jawa.sync.SyncReactiveInfluxDb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.pygmalios.reactiveinflux.jawa.sync.SyncReactiveInfluxFactory.createReactiveInfluxConfig;
import static com.pygmalios.reactiveinflux.jawa.sync.SyncReactiveInfluxFactory.createSyncReactiveInflux;

public class JavaSyncExample {
    private static final long awaitAtMostMillis = 30000;

    public static void main(String[] args) throws IOException, URISyntaxException {
        ReactiveInfluxConfig config = createReactiveInfluxConfig(new URI("http://localhost:8086/"));
        try (SyncReactiveInflux reactiveInflux = createSyncReactiveInflux(config, awaitAtMostMillis)) {
            SyncReactiveInfluxDb db = reactiveInflux.database("example1");

            db.create();
            db.drop();
        }
    }
}
