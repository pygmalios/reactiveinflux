package com.pygmalios.reactiveinflux.command.write

import com.pygmalios.reactiveinflux.PointTime
import org.scalatest.FlatSpec

class PointTimeSpec extends FlatSpec {
  behavior of "ofEpochMilli"

  it should "crate point time with values 0 and 1000000" in {
    val pointTime = PointTime.ofEpochMilli(1)
    assert(pointTime.seconds == 0)
    assert(pointTime.nanos == 1000000)
  }

  behavior of "ofEpochSecond"

  it should "crate point time with values 1 and 0" in {
    val pointTime = PointTime.ofEpochSecond(1)
    assert(pointTime.seconds == 1)
    assert(pointTime.nanos == 0)
  }

  it should "crate point time with values 0 and 1" in {
    val pointTime = PointTime.ofEpochSecond(0, 1)
    assert(pointTime.seconds == 0)
    assert(pointTime.nanos == 1)
  }

  behavior of "apply with DateTime"

  it should "convert DateTime to correct PointTime and back" in {
    assert(PointTime.pointTimeToDateTime(PointTime(PointSpec.dateTime1)) == PointSpec.dateTime1)
  }
}
