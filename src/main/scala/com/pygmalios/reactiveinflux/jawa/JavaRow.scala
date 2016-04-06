package com.pygmalios.reactiveinflux.jawa

import java.util

import com.pygmalios.{reactiveinflux => sc}
import scala.collection.JavaConversions._
import com.pygmalios.reactiveinflux.jawa.Conversions._

class JavaRow(val underlying: sc.Row) extends Row {
  override def getTime: PointTime = new JavaPointTime(underlying.time)
  override def mkString(): String = underlying.mkString
  override def getValues: util.List[Object] = underlying.values.map(toJava)

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
