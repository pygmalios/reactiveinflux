package com.pygmalios.reactiveinflux.command.query

import java.time.Instant

import com.pygmalios.reactiveinflux.ReactiveInfluxException
import com.pygmalios.reactiveinflux.command.query.Series.ColumnName

trait QueryResult extends Serializable {
  def q: Query
  def result: Result
}

trait Result extends Serializable {
  def series: Seq[Series]
  def single: Series = {
    if (series.isEmpty)
      throw new ReactiveInfluxException("No series!")

    if (series.size > 1)
      throw new ReactiveInfluxException(s"More series! [${series.map(_.name).mkString(",")}]")

    series.head
  }
  def isEmpty: Boolean = series.forall(_.isEmpty)
}

trait Series extends Serializable {
  def name: String
  def columns: Seq[ColumnName]
  def values: Seq[Row]
  def isEmpty: Boolean = values.isEmpty
}

object Series {
  type ColumnName = String
}

trait Row extends Serializable {
  def items: Seq[Value]
  def apply(columnName: ColumnName): Value
}

sealed trait Value extends Serializable
case class TimeValue(value: Instant) extends Value
case class StringValue(value: String) extends Value
case class DoubleValue(value: Double) extends Value
case class LongValue(value: Long) extends Value
case class BooleanValue(value: Boolean) extends Value
