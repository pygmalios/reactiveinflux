package com.pygmalios.reactiveinflux.jawa

import java.util
import java.util.Date

import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.jawa.Conversions._
import com.pygmalios.{reactiveinflux => sc}
import org.joda.time.{DateTime, Instant}

class JavaPoint(val underlyingPoint: sc.Point) extends JavaPointNoTime(underlyingPoint) with Point {
  def this(time: PointTime,
           measurement: String,
           tags: util.Map[String, String],
           fields: util.Map[String, Object]) {
    this(sc.Point(sc.PointTime.ofEpochSecond(time.getSeconds, time.getNano), measurement, tagsToScala(tags), fieldsToScala(fields)))
  }

  def this(dateTime: DateTime,
           measurement: String,
           tags: util.Map[String, String],
           fields: util.Map[String, Object]) {
    this(sc.Point(sc.PointTime(dateTime), measurement, tagsToScala(tags), fieldsToScala(fields)))
  }

  def this(instant: Instant,
           measurement: String,
           tags: util.Map[String, String],
           fields: util.Map[String, Object]) {
    this(sc.Point(sc.PointTime(instant), measurement, tagsToScala(tags), fieldsToScala(fields)))
  }

  def this(date: Date,
           measurement: String,
           tags: util.Map[String, String],
           fields: util.Map[String, Object]) {
    this(sc.Point(sc.PointTime(date), measurement, tagsToScala(tags), fieldsToScala(fields)))
  }

  override lazy val getTime: PointTime = new JavaPointTime(underlyingPoint.time)
}
