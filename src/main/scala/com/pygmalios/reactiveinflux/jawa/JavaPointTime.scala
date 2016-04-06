package com.pygmalios.reactiveinflux.jawa

import java.util.Date

import com.pygmalios.{reactiveinflux => sc}
import org.joda.time.{DateTime, Instant}

class JavaPointTime(val underlying: sc.PointTime) extends PointTime {
  def this(seconds: Long, nano: Int) {
    this(sc.PointTime.ofEpochSecond(seconds, nano))
  }

  def this(dateTime: DateTime) {
    this(sc.PointTime(dateTime))
  }

  def this(instant: Instant) {
    this(sc.PointTime(instant))
  }

  def this(date: Date) {
    this(sc.PointTime(date))
  }

  override def getSeconds: Long = underlying.seconds
  override def getNano: Int = underlying.nanos

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}