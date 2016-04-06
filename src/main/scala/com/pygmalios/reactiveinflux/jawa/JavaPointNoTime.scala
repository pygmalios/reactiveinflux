package com.pygmalios.reactiveinflux.jawa

import java.util

import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.jawa.Conversions._
import com.pygmalios.{reactiveinflux => sc}

class JavaPointNoTime(val underlying: sc.PointNoTime) extends PointNoTime {
  def this(measurement: String,
           tags: util.Map[String, String],
           fields: util.Map[String, Object]) {
    this(sc.Point(measurement, tagsToScala(tags), fieldsToScala(fields)))
  }

  override lazy val getMeasurement: String = underlying.measurement.unescaped
  override lazy val getTags: util.Map[String, String] = tagsToJava(underlying.tags)
  override lazy val getFields: util.Map[String, AnyRef] = fieldsToJava(underlying.fields)

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
