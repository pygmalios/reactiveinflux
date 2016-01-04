package com.pygmalios.reactiveinflux.command.query

import java.time.Instant

import com.pygmalios.reactiveinflux.ReactiveInfluxException
import com.pygmalios.reactiveinflux.command.query.Series.{ColumnName, SeriesName}

trait QueryResult extends Serializable {
  def q: Query
  def result: Result
}

object QueryResult {
  def apply(q: Query, result: Result): QueryResult = SimpleQueryResult(q, result)
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

object Result {
  def apply(series: Seq[Series]): Result = SimpleResult(series)
}

trait Series extends Serializable {
  def name: SeriesName
  def columns: Seq[ColumnName]
  def values: Seq[Row]
  def isEmpty: Boolean = values.isEmpty
  private[reactiveinflux] def createRow(items: Seq[Value]): Row
}

object Series {
  type SeriesName = String
  type ColumnName = String

  def apply(name: SeriesName, columns: Seq[ColumnName], values: Seq[Row]): Series =
    SimpleSeries(name, columns, values)
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

private[reactiveinflux] case class SimpleQueryResult(q: Query, result: Result) extends QueryResult

private[reactiveinflux] case class SimpleResult(series: Seq[Series]) extends Result

private[reactiveinflux] case class SimpleSeries(name: SeriesName, columns: Seq[ColumnName], values: Seq[Row]) extends Series {
  override private[reactiveinflux] def createRow(items: Seq[Value]): Row = SimpleRow(items)

  case class SimpleRow(items: Seq[Value]) extends Row {
    override def apply(columnName: ColumnName): Value = items(columns.indexOf(columnName))
  }
}