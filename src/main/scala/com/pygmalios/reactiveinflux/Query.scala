package com.pygmalios.reactiveinflux

/**
  * Query in InfluxQL.
  */
trait Query extends Serializable {
  def influxQl: String
}

object Query {
  def apply(influxQl: String): Query = RawQuery(influxQl)
}

private[reactiveinflux] case class RawQuery(influxQl: String) extends Query {
  override def toString = influxQl
}
