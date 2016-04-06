package com.pygmalios.reactiveinflux.jawa.sync

import java.lang.Iterable
import java.util

import com.pygmalios.reactiveinflux.jawa.Conversions._
import com.pygmalios.reactiveinflux.jawa.{QueryParameters, _}
import com.pygmalios.{reactiveinflux => sc}

import scala.collection.JavaConversions._
import scala.concurrent.duration.Duration

class JavaSyncReactiveInfluxDb(val underlying: sc.sync.SyncReactiveInfluxDb)
                              (implicit awaitAtMost: Duration) extends SyncReactiveInfluxDb {
  override def create(): Unit = underlying.create()
  override def drop(): Unit = underlying.drop()
  override def write(point: PointNoTime): Unit = underlying.write(toScala(point))
  override def write(points: Iterable[PointNoTime]): Unit = underlying.write(points.toIterable.map(toScala))
  override def write(point: PointNoTime, params: WriteParameters): Unit = underlying.write(toScala(point), toScala(params))
  override def write(points: Iterable[PointNoTime], params: WriteParameters): Unit = underlying.write(points.toIterable.map(toScala), toScala(params))
  override def query(q: Query): QueryResult = ???
  override def query(q: Query, params: QueryParameters): QueryResult = ???
  override def query(qs: util.List[Query]): QueryResult = ???
  override def query(qs: util.List[Query], params: QueryParameters): QueryResult = ???
  override def getConfig: ReactiveInfluxConfig = ???

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
