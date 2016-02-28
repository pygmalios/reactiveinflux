package com.pygmalios.reactiveinflux.command.write

import com.pygmalios.reactiveinflux._
import org.joda.time.{DateTime, DateTimeZone, Duration}

object PointSpec {
  val dateTime1 = new DateTime(1983, 1, 10, 11, 42, 7, 13, DateTimeZone.UTC)
  val time1 = PointTime(dateTime1)
  val point1 = Point(time1, "m1", Map.empty, Map("fk" -> LongFieldValue(-1)))

  val time2 = time1.plus(Duration.millis(3))
  val point2 = Point(time2, "m2", Map("tk1" -> "tv1", "tk2" -> "tv2"),
    Map(
      "fk" -> BooleanFieldValue(true),
      "fk2" -> BigDecimalFieldValue(1),
      "fk3" -> StringFieldValue("abcXYZ")
    ))
}
