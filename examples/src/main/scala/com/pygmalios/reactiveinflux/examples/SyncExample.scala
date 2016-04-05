package com.pygmalios.reactiveinflux.examples

import java.net.URI

import com.pygmalios.reactiveinflux._
import org.joda.time.DateTime

import scala.concurrent.duration._

/**
  * Example of blocking, synchronous usage of ReactiveInflux.
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
