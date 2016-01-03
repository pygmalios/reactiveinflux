package com.pygmalios.reactiveinflux.model

import java.time.Instant

import com.pygmalios.reactiveinflux.model.Point.{FieldKey, TagValue, TagKey}

/**
  * Common attributes of every point.
  */
trait PointNoTime extends Serializable {
  def measurement: String
  def tags: Map[TagKey, TagValue]
  def fields: Map[FieldKey, FieldValue]
}

/**
  * Point with nanosecond precision time.
  */
trait Point extends PointNoTime {
  def time: Instant
}

object Point {
  type TagKey = String
  type TagValue = String
  type FieldKey = String

  def apply(measurement: String, tags: Map[TagKey, TagValue], fields: Map[FieldKey, FieldValue]): PointNoTime =
    SimplePointNoTime(measurement, tags, fields)

  def apply(time: Instant, measurement: String, tags: Map[TagKey, TagValue], fields: Map[FieldKey, FieldValue]): Point =
    SimplePoint(time, measurement, tags, fields)
}

/**
  * Supported field value types.
  */
sealed trait FieldValue extends Serializable
case class StringFieldValue(value: String) extends FieldValue
case class FloatFieldValue(value: Double) extends FieldValue
case class LongFieldValue(value: Long) extends FieldValue
case class BooleanFieldValue(value: Boolean) extends FieldValue

private[reactiveinflux] case class SimplePointNoTime(measurement: String,
                                                     tags: Map[TagKey, TagValue],
                                                     fields: Map[FieldKey, FieldValue]) extends PointNoTime

private[reactiveinflux] case class SimplePoint(time: Instant,
                                               measurement: String,
                                               tags: Map[TagKey, TagValue],
                                               fields: Map[FieldKey, FieldValue]) extends Point