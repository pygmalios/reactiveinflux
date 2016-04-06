package com.pygmalios.reactiveinflux.examples;

import com.pygmalios.reactiveinflux.jawa.*;
import com.pygmalios.reactiveinflux.jawa.sync.JavaSyncReactiveInflux;
import com.pygmalios.reactiveinflux.jawa.sync.SyncReactiveInflux;
import com.pygmalios.reactiveinflux.jawa.sync.SyncReactiveInfluxDb;
import org.joda.time.DateTime;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class JavaSyncExample {
    private static final long awaitAtMostMillis = 30000;

    public static void main(String[] args) throws IOException, URISyntaxException {
        // Use Influx at the provided URL
        ReactiveInfluxConfig config = new JavaReactiveInfluxConfig(new URI("http://localhost:8086/"));
        try (SyncReactiveInflux reactiveInflux = new JavaSyncReactiveInflux(config, awaitAtMostMillis)) {
            // Use database "example1"
            SyncReactiveInfluxDb db = reactiveInflux.database("example1");

            // Create the "example1" database
            db.create();

            // Define tags for the point
            Map<String, String> tags = new HashMap<>();
            tags.put("t1", "A");
            tags.put("t2", "B");

            // Define fields for the point
            Map<String, Object> fields = new HashMap<>();
            fields.put("f1", 10.3);
            fields.put("f2", "x");
            fields.put("f3", -1);
            fields.put("f4", true);

            // Write a single point to "measurement1"
            Point point = new JavaPoint(
                DateTime.now(),
                "measurement1",
                tags,
                fields
            );
            db.write(point);

            // Synchronously read the written point
            QueryResult queryResult = db.query("SELECT * FROM measurement1");

            // Print the single point to the console
            System.out.println(queryResult.getRow().mkString());

            // Synchronously drop the "example1" database
            db.drop();
        }
    }
}
