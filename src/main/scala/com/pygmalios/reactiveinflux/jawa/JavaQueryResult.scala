package com.pygmalios.reactiveinflux.jawa

import com.pygmalios.{reactiveinflux => sc}

class JavaQueryResult(val underlying: sc.QueryResult) extends QueryResult {
  override def getQ: Query = new JavaQuery(underlying.q)
  override def getRow: Row = new JavaRow(underlying.row)
  override def getResult: Result = new JavaResult(underlying.result)

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
