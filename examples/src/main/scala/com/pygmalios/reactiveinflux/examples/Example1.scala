package com.pygmalios.reactiveinflux.examples

import java.net.URI

import com.pygmalios.reactiveinflux._

/**
  * Example usage of ReactiveInflux.
  */
object Example1 extends App {
  withInfluxDb(new URI("http://myinflux:6543/"), "example1") { db =>
    // Create the "example1" database
    db.create()

    // Drop the "example1" database
    db.drop()
  }
}
