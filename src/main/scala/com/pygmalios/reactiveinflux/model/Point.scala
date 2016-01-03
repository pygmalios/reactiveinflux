package com.pygmalios.reactiveinflux.model

import java.time.Instant

import com.pygmalios.reactiveinflux.model.Point.{FieldKey, TagValue, TagKey}

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
}

/**
  * Common attributes of every point.
  */
trait PointNoTime extends Serializable {
  def measurement: String
  def tags: Map[TagKey, TagValue]
  def fields: Map[FieldKey, FieldValue]
}

/**
  * Supported field value types.
  */
sealed trait FieldValue extends Serializable
case class StringFieldValue(value: String) extends FieldValue
case class FloatFieldValue(value: Double) extends FieldValue
case class LongFieldValue(value: Long) extends FieldValue
case class BooleanFieldValue(value: Boolean) extends FieldValue