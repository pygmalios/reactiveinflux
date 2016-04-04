package com.pygmalios.reactiveinflux.examples

import java.net.URI

import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.command.write.Point
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * The simplest possible usage of ReactiveInflux. No configuration file needed.
  */
object Example1 extends App {
  withInfluxDb(new URI("http://myinflux:6543/"), "example1") { db =>
    // Create the "example1" database
    db.create().map { _ =>
      // Write a single point to "measurement1"
      val point = Point(
        time        = DateTime.now(),
        measurement = "measurement1",
        tags        = Map("t1" -> "A", "t2" -> "B"),
        fields      = Map("f1" -> 10.3, "f2" -> "x", "f3" -> -1, "f4" -> true)
      )
      db.write(point).map { _ =>
        // Read the written point
        db.query("SELECT * FROM measurement1").flatMap { result =>
          // Print it to the console
          println(result.result.single.single.items)

          // Drop the "example1" database
          db.drop()
        }
      }
    }
  }
}
