package com.pygmalios.reactiveinflux.command.query

/**
  * Query in InfluxQL.
  */
trait Query extends Serializable {
  def influxQl: String
}

case class RawQuery(influxQl: String) extends Query {
  override def toString = influxQl
}
