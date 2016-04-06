package com.pygmalios.reactiveinflux.jawa

import com.pygmalios.{reactiveinflux => sc}

class JavaQuery(val underlying: sc.Query) extends Query {
  override def getInfluxQl: String = underlying.influxQl

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
