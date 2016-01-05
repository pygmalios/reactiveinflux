package com.pygmalios.reactiveinflux.command.write

import java.time.Instant

import com.pygmalios.reactiveinflux.command.write.Point.{FieldKey, Measurement, TagKey, TagValue}
import com.pygmalios.reactiveinflux.impl.{EscapedString, EscapedStringWithEquals}

/**
  * Common attributes of every point.
  */
trait PointNoTime extends Serializable {
  def measurement: Measurement
  def tags: Map[TagKey, TagValue]
  def fields: Map[FieldKey, FieldValue]
}

/**
  * Point with time.
  */
trait Point extends PointNoTime {
  def time: Instant
}

object Point {
  type Measurement = EscapedString
  type TagKey = EscapedStringWithEquals
  type TagValue = EscapedStringWithEquals
  type FieldKey = EscapedStringWithEquals

  def apply(measurement: Measurement, tags: Map[TagKey, TagValue], fields: Map[FieldKey, FieldValue]): PointNoTime =
    SimplePointNoTime(measurement, tags, fields)

  def apply(time: Instant, measurement: Measurement, tags: Map[TagKey, TagValue], fields: Map[FieldKey, FieldValue]): Point =
    SimplePoint(time, measurement, tags, fields)
}

/**
  * Supported field value types.
  */
sealed trait FieldValue extends Serializable
case class StringFieldValue(value: String) extends FieldValue
case class BigDecimalFieldValue(value: BigDecimal) extends FieldValue
case class LongFieldValue(value: Long) extends FieldValue
case class BooleanFieldValue(value: Boolean) extends FieldValue

private[reactiveinflux] case class SimplePointNoTime(measurement: Measurement,
                                                     tags: Map[TagKey, TagValue],
                                                     fields: Map[FieldKey, FieldValue]) extends PointNoTime

private[reactiveinflux] case class SimplePoint(time: Instant,
                                               measurement: Measurement,
                                               tags: Map[TagKey, TagValue],
                                               fields: Map[FieldKey, FieldValue]) extends Point