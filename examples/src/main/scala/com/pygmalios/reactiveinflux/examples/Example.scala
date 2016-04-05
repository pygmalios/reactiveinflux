package com.pygmalios.reactiveinflux.examples

import java.net.URI

import com.pygmalios.reactiveinflux._
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Example of asynchronous usage of ReactiveInflux.
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
