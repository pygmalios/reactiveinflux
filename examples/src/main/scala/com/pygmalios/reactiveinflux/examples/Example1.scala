package com.pygmalios.reactiveinflux.examples

import java.net.URI

import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.command.write.Point
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Simple asynchronous example of ReactiveInflux using and chaining Scala futures together.
  */
object Example1 extends App {
  // Use Influx at the provided URL and database "example1"
  val result = withInfluxDb(new URI("http://myinflux:6543/"), "example1") { db =>

    // Asynchronously create the "example1" database ...
    db.create().flatMap { _ =>

      // ... and then asynchronously write a single point to "measurement1" ...
      val point = Point(
        time        = DateTime.now(),
        measurement = "measurement1",
        tags        = Map("t1" -> "A", "t2" -> "B"),
        fields      = Map(
          "f1" -> 10.3, // BigDecimal field
          "f2" -> "x",  // String field
          "f3" -> -1,   // Long field
          "f4" -> true) // Boolean field
      )
      db.write(point).flatMap { _ =>

        // ... and then asynchronously read the written point ...
        db.query("SELECT * FROM measurement1").flatMap { queryResult =>

          // Print the single point to the console
          println(queryResult.row)

          // ... and then asynchronously drop the "example1" database.
          db.drop()
        }
      }
    }
  }

  // Wait at most 30 seconds for everything to complete.
  Await.result(result, 30.seconds)
}
