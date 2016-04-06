# reactiveinflux 0.5-SNAPSHOT
Non-blocking [InfluxDB](https://influxdata.com/time-series-platform/influxdb/) driver for
[Scala](http://www.scala-lang.org/) and [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) (blocking only).

Immutability, testability and extensibility are key features of ReactiveInflux. It internally uses
[Play Framework WS API](https://www.playframework.com/documentation/2.3.x/ScalaWS)
which is a rich asynchronous HTTP client built on top of [Async Http Client](https://github.com/AsyncHttpClient/async-http-client).

## Get it from Maven Central repository
**TODO:**
[http://mvnrepository.com/artifact/com.pygmalios](http://mvnrepository.com/artifact/com.pygmalios)

## Scala example

```scala
package com.pygmalios.reactiveinflux.examples

import java.net.URI

import com.pygmalios.reactiveinflux._
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Example of asynchronous usage of SyncReactiveInflux.
  *
  * It assumes that you have InfluxDB running locally on port 8086. How to install InfluxDB:
  * https://docs.influxdata.com/influxdb/v0.11/introduction/installation/
  */
object Example extends App {
  // Use Influx at the provided URL and database "example1"
  val result = withInfluxDb(new URI("http://localhost:8086/"), "example1") { db =>

    // Asynchronously create the "example1" database ...
    db.create().flatMap { _ =>

      // ... and then asynchronously write a single point to "measurement1" ...
      val point = Point(
        time = DateTime.now(),
        measurement = "measurement1",
        tags = Map("t1" -> "A", "t2" -> "B"),
        fields = Map(
          "f1" -> 10.3, // BigDecimal field
          "f2" -> "x", // String field
          "f3" -> -1, // Long field
          "f4" -> true) // Boolean field
      )
      db.write(point).flatMap { _ =>

        // ... and then asynchronously read the written point ...
        db.query("SELECT * FROM measurement1").flatMap { queryResult =>

          // Print the single point to the console
          println(queryResult.row.mkString)

          // ... and then asynchronously drop the "example1" database.
          db.drop()
        }
      }
    }
  }

  // Wait at most 30 seconds for the future to complete
  Await.ready(result, 30.seconds)
}
```

## Scala blocking example

```scala
package com.pygmalios.reactiveinflux.examples

import java.net.URI

import com.pygmalios.reactiveinflux._
import org.joda.time.DateTime

import scala.concurrent.duration._

/**
  * Example of blocking, synchronous usage of SyncReactiveInflux.
  *
  * It assumes that you have InfluxDB running locally on port 8086. How to install InfluxDB:
  * https://docs.influxdata.com/influxdb/v0.11/introduction/installation/
  */
object SyncExample extends App {
  // You have to specify how much are you willing to wait results of individual blocking calls
  implicit val awaitAtMost = 10.seconds

  // Use Influx at the provided URL and database "example1"
  syncInfluxDb(new URI("http://localhost:8086/"), "example1") { db =>

    // Synchronously create the "example1" database
    db.create()

    // Synchronously write a single point to "measurement1"
    val point = Point(
      time = DateTime.now(),
      measurement = "measurement1",
      tags = Map("t1" -> "A", "t2" -> "B"),
      fields = Map(
        "f1" -> 10.3, // BigDecimal field
        "f2" -> "x",  // String field
        "f3" -> -1,   // Long field
        "f4" -> true) // Boolean field
    )
    db.write(point)

    // Synchronously read the written point
    val queryResult = db.query("SELECT * FROM measurement1")

    // Print the single point to the console
    println(queryResult.row.mkString)

    // Synchronously drop the "example1" database.
    db.drop()
  }
}
```

## Java blocking example

```java
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
```

## Apache Spark support

**TODO**

## Versioning explained

**TODO**