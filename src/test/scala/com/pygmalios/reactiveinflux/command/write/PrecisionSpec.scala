package com.pygmalios.reactiveinflux.command.write

import org.joda.time.Instant
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PrecisionSpec extends FlatSpec {
//  behavior of "Nano"
//
//  it should "convert 1 nanosecond to 1" in {
//    assert(Nano.format(Instant.ofEpochSecond(0, 1)) == "1")
//  }
//
//  it should "convert 1 millisecond to 1000000" in {
//    assert(Nano.format(Instant.ofEpochMilli(1)) == "1000000")
//  }
//
//  it should "convert 1 second to 1000000000" in {
//    assert(Nano.format(Instant.ofEpochSecond(1)) == "1000000000")
//  }
//
//  it should "convert 1 minute to 60000000000" in {
//    assert(Nano.format(Instant.ofEpochSecond(60)) == "60000000000")
//  }
//
//  it should "convert 1234567890 to 1234567890000000000" in {
//    assert(Nano.format(Instant.ofEpochSecond(1234567890)) == "1234567890000000000")
//  }
//
//  behavior of "Micro"
//
//  it should "convert 1 nanosecond to 0" in {
//    assert(Micro.format(Instant.ofEpochSecond(0, 1)) == "0")
//  }
//
//  it should "convert 999 nanoseconds to 0" in {
//    assert(Micro.format(Instant.ofEpochSecond(0, 999)) == "0")
//  }
//
//  it should "convert 1 microsecond to 1" in {
//    assert(Micro.format(Instant.ofEpochSecond(0, 1000)) == "1")
//  }
//
//  it should "convert 1 millisecond to 1000" in {
//    assert(Micro.format(Instant.ofEpochMilli(1)) == "1000")
//  }
//
//  it should "convert 1 second to 100000" in {
//    assert(Micro.format(Instant.ofEpochSecond(1)) == "1000000")
//  }
//
//  it should "convert 1 minute to 60000000" in {
//    assert(Micro.format(Instant.ofEpochSecond(60)) == "60000000")
//  }
//
//  it should "convert 1234567890 to 1234567890000000" in {
//    assert(Micro.format(Instant.ofEpochSecond(1234567890)) == "1234567890000000")
//  }

  behavior of "Milli"

//  it should "convert 1 nanosecond to 0" in {
//    assert(Milli.format(Instant.ofEpochSecond(0, 1)) == "0")
//  }
//
//  it should "convert 999 nanoseconds to 0" in {
//    assert(Milli.format(Instant.ofEpochSecond(0, 999)) == "0")
//  }
//
//  it should "convert 1 microsecond to 0" in {
//    assert(Milli.format(Instant.ofEpochSecond(0, 1000)) == "0")
//  }
//
//  it should "convert 999 microsecond to 0" in {
//    assert(Milli.format(Instant.ofEpochSecond(0, 999000)) == "0")
//  }

  it should "convert 1 millisecond to 1" in {
    assert(Milli.format(new Instant(1)) == "1")
  }

  it should "convert 1 second to 1000" in {
    assert(Milli.format(new Instant(1000)) == "1000")
  }

  it should "convert 1 minute to 60000" in {
    assert(Milli.format(new Instant(60000)) == "60000")
  }

  it should "convert 1234567890 to 1234567890000" in {
    assert(Milli.format(new Instant(1234567890)) == "1234567890")
  }

  behavior of "Second"

//  it should "convert 1 nanosecond to 0" in {
//    assert(Second.format(new Instant(0, 1)) == "0")
//  }
//
//  it should "convert 1 microsecond to 0" in {
//    assert(Second.format(new Instant(0, 1000)) == "0")
//  }

  it should "convert 1 millisecond to 0" in {
    assert(Second.format(new Instant(1)) == "0")
  }

  it should "convert 999 milliseconds to 0" in {
    assert(Second.format(new Instant(999)) == "0")
  }

  it should "convert 1 second to 1" in {
    assert(Second.format(new Instant(1000)) == "1")
  }

  it should "convert 1 minute to 60" in {
    assert(Second.format(new Instant(60000)) == "60")
  }

  it should "convert 1234567890 to 1234567" in {
    assert(Second.format(new Instant(1234567890)) == "1234567")
  }

  behavior of "Minute"

//  it should "convert 1 nanosecond to 0" in {
//    assert(Minute.format(new Instant(0, 1)) == "0")
//  }
//
//  it should "convert 1 microsecond to 0" in {
//    assert(Minute.format(new Instant(0, 1000)) == "0")
//  }

  it should "convert 1 millisecond to 0" in {
    assert(Minute.format(new Instant(1)) == "0")
  }

  it should "convert 1 second to 0" in {
    assert(Minute.format(new Instant(1)) == "0")
  }

  it should "convert 59 seconds to 0" in {
    assert(Minute.format(new Instant(59)) == "0")
  }

  it should "convert 1 minute to 1" in {
    assert(Minute.format(new Instant(60000)) == "1")
  }

  it should "convert 1234567890 to 20576131" in {
    assert(Minute.format(new Instant(1234567890)) == "20576")
  }

  behavior of "Hour"

//  it should "convert 1 nanosecond to 0" in {
//    assert(Hour.format(new Instant(0, 1)) == "0")
//  }
//
//  it should "convert 1 microsecond to 0" in {
//    assert(Hour.format(new Instant(0, 1000)) == "0")
//  }

  it should "convert 1 millisecond to 0" in {
    assert(Hour.format(new Instant(1)) == "0")
  }

  it should "convert 1 second to 0" in {
    assert(Hour.format(new Instant(1)) == "0")
  }

  it should "convert 1 minute to 0" in {
    assert(Hour.format(new Instant(60000)) == "0")
  }

  it should "convert 59 minutes to 0" in {
    assert(Hour.format(new Instant(3540000)) == "0")
  }

  it should "convert 1234567890 to 342935" in {
    assert(Hour.format(new Instant(1234567890)) == "342")
  }
}