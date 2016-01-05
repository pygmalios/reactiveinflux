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
  def single: Row = {
    if (values.isEmpty)
      throw new ReactiveInfluxException("No values!")

    if (values.size > 1)
      throw new ReactiveInfluxException(s"More values! [${values.size}]")

    values.head
  }
  def isEmpty: Boolean = values.isEmpty
  def apply(row: Row, columnName: ColumnName): Value = row.items(columns.indexOf(columnName))
}

object Series {
  val timeColumnName: ColumnName = "time"

  type SeriesName = String
  type ColumnName = String

  def apply(name: SeriesName, columns: Seq[ColumnName], values: Seq[Seq[Value]], timeFormat: TimeFormat): Series = {
    val timeColumnIndex = columns.indexOf(timeColumnName)
    SimpleSeries(name, columns, values.map(Row.apply(_, timeColumnIndex, timeFormat)))
  }
}

trait Row {
  def time: Instant
  def items: Seq[Value]
}

object Row {
  def apply(items: Seq[Value], timeColumnIndex: Int, timeFormat: TimeFormat): Row =
    SimpleRow(timeFormat(items(timeColumnIndex)), items)
}

sealed trait Value extends Serializable
case class StringValue(value: String) extends Value
case class BigDecimalValue(value: BigDecimal) extends Value
case class BooleanValue(value: Boolean) extends Value

trait TimeFormat {
  def apply(value: Value): Instant
}

private[reactiveinflux] object Rfc3339 extends TimeFormat {
  override def apply(value: Value): Instant = value match {
    case StringValue(s) => Instant.parse(s)
    case other => throw new ReactiveInfluxException(s"RFC3339 time must be a string! [$other]")
  }
}

private[reactiveinflux] case class SimpleQueryResult(q: Query, result: Result) extends QueryResult
private[reactiveinflux] case class SimpleResult(series: Seq[Series]) extends Result
private[reactiveinflux] case class SimpleSeries(name: SeriesName, columns: Seq[ColumnName], values: Seq[Row]) extends Series
private[reactiveinflux] case class SimpleRow(time: Instant, items: Seq[Value]) extends Row