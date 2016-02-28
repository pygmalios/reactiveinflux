package com.pygmalios.reactiveinflux.command.write

import java.util.Date

import org.joda.time.{DateTime, DateTimeZone, Instant}

/**
  * Epoch time with nanosecond precision.
  */
trait PointTime {
  /**
    * The number of seconds from the epoch of 1970-01-01T00:00:00Z.
    */
  def seconds: Long

  /**
    * The number of nanoseconds, later along the time-line, from the seconds field.
    * This is always positive, and never exceeds 999,999,999.
    */
  def nanos: Int
}

object PointTime {
  def ofEpochMilli(epochMilli: Long): PointTime = ofEpochSecond(epochMilli / 1000, (epochMilli % 1000).toInt * 1000000)
  def ofEpochSecond(epochSecond: Long): PointTime = ofEpochMilli(epochSecond * 1000)
  def ofEpochSecond(epochSecond: Long, nanoAdjustment: Int): PointTime =
    SimplePointTime(epochSecond, nanoAdjustment)

  implicit def apply(dateTime: DateTime): PointTime = ofEpochMilli(dateTime.withZone(DateTimeZone.UTC).getMillis)
  implicit def apply(instant: Instant): PointTime = apply(instant.toDateTime)
  implicit def apply(date: Date): PointTime = apply(new DateTime(date))
  implicit def pointTimeToDateTime(pointTime: PointTime): DateTime =
    new DateTime(pointTime.seconds*1000 + (pointTime.nanos/1000000), DateTimeZone.UTC)
}

private case class SimplePointTime(seconds: Long, nanos: Int) extends PointTime