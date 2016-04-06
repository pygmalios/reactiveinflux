package com.pygmalios.reactiveinflux.jawa

import java.util

import com.pygmalios.{reactiveinflux => sc}

import scala.collection.JavaConversions._

class JavaResult(val underlying: sc.Result) extends Result {
  override lazy val getSeries: util.List[Series] = underlying.series.map(new JavaSeries(_))

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
