package com.pygmalios.reactiveinflux.jawa

import java.util

import com.pygmalios.{reactiveinflux => sc}

import scala.collection.JavaConversions._

class JavaQueryParameters(val underlying: sc.command.query.QueryParameters) extends QueryParameters {
  override def getEpoch: String = underlying.epoch.map(_.q).orNull
  override def getChunkSize: Integer = underlying.chunkSize.map(_.asInstanceOf[Integer]).orNull
  override def getParams: util.Map[String, String] = underlying.params

  override def toString: String = underlying.toString
  override def hashCode(): Int = underlying.hashCode()
  override def equals(obj: scala.Any): Boolean = underlying.equals(obj)
}
