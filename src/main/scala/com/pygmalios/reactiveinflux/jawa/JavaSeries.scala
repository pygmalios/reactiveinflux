package com.pygmalios.reactiveinflux.jawa
import java.util

import com.pygmalios.{reactiveinflux => sc}
import scala.collection.JavaConversions._

class JavaSeries(val underlying: sc.Series) extends Series {
  override def getName: String = underlying.name
  override lazy val getRows: util.List[Row] = underlying.rows.map(new JavaRow(_))
  override lazy val getColumns: util.List[String] = underlying.columns

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
