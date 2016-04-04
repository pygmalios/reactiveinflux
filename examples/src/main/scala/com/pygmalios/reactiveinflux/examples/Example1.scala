package com.pygmalios.reactiveinflux.examples

import com.pygmalios.reactiveinflux.ReactiveInflux
import com.pygmalios.reactiveinflux.ReactiveInflux.ReactiveInfluxDbName

/**
  * Example usage of ReactiveInflux.
  */
object Example1 extends App {
  implicit val dbName = ReactiveInfluxDbName("example1")

  val reactiveInflux = ReactiveInflux()
  try {
    val db = reactiveInflux.database
    db.create()
    db.drop()
  }
  finally {
    reactiveInflux.close()
  }
}
