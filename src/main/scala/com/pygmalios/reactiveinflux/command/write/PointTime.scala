package com.pygmalios.reactiveinflux.command.write

import java.util.Date

import org.joda.time.{Instant, DateTime, DateTimeZone}

/**
  * Epoch time with nanosecond precision.
  */
trait PointTime {
  /**
    * Java 7 Date type is shitty and supports millisecond precision only. We can change this to Instant to support
    * nanosecond precision once we switch to Java 8.
    *
    * @return Epoch time in seconds.
    */
  def seconds: Long

  /**
    * Nanosecond fraction of second to extend Java 7 Date precision. Legal value is between 0 and 999999999.
    *
    * @return Nanosecond fraction of seconds.
    */
  def nanos: Long
}

object PointTime {
  def ofEpochMilli(epochMilli: Long): PointTime = ofEpochSecond(epochMilli / 1000, (epochMilli % 1000) * 1000000)
  def ofEpochSecond(epochSecond: Long): PointTime = ofEpochMilli(epochSecond * 1000)
  def ofEpochSecond(epochSecond: Long, nanoAdjustment: Long): PointTime =
    SimplePointTime(epochSecond, nanoAdjustment)

  implicit def apply(dateTime: DateTime): PointTime = ofEpochMilli(dateTime.withZone(DateTimeZone.UTC).getMillis)
  implicit def apply(instant: Instant): PointTime = apply(instant.toDateTime)
  implicit def apply(date: Date): PointTime = apply(new DateTime(date))
  implicit def pointTimeToDateTime(pointTime: PointTime): DateTime =
    new DateTime(pointTime.seconds*1000 + (pointTime.nanos/1000000), DateTimeZone.UTC)
}

private case class SimplePointTime(seconds: Long, nanos: Long) extends PointTime