package com.pygmalios.reactiveinflux.command.query

/**
  * Query in InfluxQL.
  */
trait Query {
  def influxQl: String
}

case class RawQuery(influxQl: String) extends Query
