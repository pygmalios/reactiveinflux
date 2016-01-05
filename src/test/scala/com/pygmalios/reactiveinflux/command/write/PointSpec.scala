package com.pygmalios.reactiveinflux.command.write

import java.time.{OffsetDateTime, ZoneOffset}

import com.pygmalios.reactiveinflux._

object PointSpec {
  val time1 = OffsetDateTime.of(1983, 1, 10, 11, 42, 0, 0, ZoneOffset.UTC).toInstant
  val point1 = Point(time1, "m1", Map.empty, Map("fk" -> LongFieldValue(-1)))

  val time2 = time1.plusNanos(3)
  val point2 = Point(time2, "m2", Map("tk1" -> "tv1", "tk2" -> "tv2"),
    Map(
      "fk" -> BooleanFieldValue(true),
      "fk2" -> BigDecimalFieldValue(1),
      "fk3" -> StringFieldValue("abcXYZ")
    ))
}
