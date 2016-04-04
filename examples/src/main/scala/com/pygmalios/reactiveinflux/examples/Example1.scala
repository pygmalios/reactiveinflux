package com.pygmalios.reactiveinflux.examples

import com.pygmalios.reactiveinflux.{ReactiveInflux, ReactiveInfluxDbParams}

/**
  * Example usage of ReactiveInflux.
  */
object Example1 extends App {
  implicit val params = ReactiveInfluxDbParams(dbName = "example1")

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
