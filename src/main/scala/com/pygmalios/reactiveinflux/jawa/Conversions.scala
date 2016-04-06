package com.pygmalios.reactiveinflux.jawa

import java.util

import com.pygmalios.reactiveinflux.Point.{FieldKey, TagKey, TagValue}
import com.pygmalios.reactiveinflux._
import com.pygmalios.reactiveinflux.command.write.{Consistency, Precision}
import com.pygmalios.reactiveinflux.impl.EscapedStringWithEquals
import com.pygmalios.reactiveinflux.jawa.sync.{JavaSyncReactiveInflux, JavaSyncReactiveInfluxDb, SyncReactiveInflux, SyncReactiveInfluxDb}
import com.pygmalios.{reactiveinflux => sc}

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration

object Conversions {
  def toScala(reactiveInfluxConfig: ReactiveInfluxConfig): sc.ReactiveInfluxConfig = {
    sc.ReactiveInfluxConfig(
      reactiveInfluxConfig.getUrl,
      Option(reactiveInfluxConfig.getUsername),
      Option(reactiveInfluxConfig.getPassword))
  }

  def toJava(reactiveInflux: sc.sync.SyncReactiveInflux)(implicit awaitAtMost: Duration): SyncReactiveInflux =
    new JavaSyncReactiveInflux(reactiveInflux)(awaitAtMost)

  def toJava(reactiveInfluxConfig: sc.ReactiveInfluxConfig): ReactiveInfluxConfig =
    new JavaReactiveInfluxConfig(reactiveInfluxConfig)

  def toJava(syncReactiveInfluxDb: sc.sync.SyncReactiveInfluxDb)(implicit awaitAtMost: Duration): SyncReactiveInfluxDb =
    new JavaSyncReactiveInfluxDb(syncReactiveInfluxDb)

  def toJava(pingResult: sc.PingResult): PingResult =
    new JavaPingResult(pingResult)

  def toJava(promise: Future[Unit]): Future[Void] = promise.map(_ => null)

  def tagsToScala(t: util.Map[String, String]): Map[TagKey, TagValue] = {
    t.toMap.map {
      case (k: String, v: String) => (new EscapedStringWithEquals(k), v: TagValue)
      case (k, v) => throw new ReactiveInfluxException(s"Unsupported java tag types! [${k.getClass}, ${v.getClass}]")
    }
  }

  def fieldsToScala(t: util.Map[String, Object]): Map[FieldKey, FieldValue] = {
    t.toMap.map {
      case (k: String, v: java.lang.String) => (k: FieldKey, StringFieldValue(v))
      case (k: String, v: java.math.BigDecimal) => (k: FieldKey, BigDecimalFieldValue(v))
      case (k: String, v: java.lang.Double) => (k: FieldKey, BigDecimalFieldValue(BigDecimal(v)))
      case (k: String, v: java.lang.Byte) => (k: FieldKey, LongFieldValue(v.toLong))
      case (k: String, v: java.lang.Integer) => (k: FieldKey, LongFieldValue(v.toLong))
      case (k: String, v: java.lang.Long) => (k: FieldKey, LongFieldValue(v))
      case (k: String, v: java.lang.Boolean) => (k: FieldKey, BooleanFieldValue(v))
      case (k, v) => throw new ReactiveInfluxException(s"Unsupported java field types! [${k.getClass}, ${v.getClass}]")
    }
  }

  def tagsToJava(t: Map[TagKey, TagValue]): util.Map[String, String] = {
    t.toMap.map {
      case (k: TagKey, v: TagValue) => (k.unescaped, v.unescaped)
      case (k, v) => throw new ReactiveInfluxException(s"Unsupported scala tag types! [${k.getClass}, ${v.getClass}]")
    }
  }

  def fieldsToJava(t: Map[FieldKey, FieldValue]): util.Map[String, Object] = {
    t.toMap.map {
      case (k: FieldKey, StringFieldValue(v)) => (k.unescaped, v: String)
      case (k: FieldKey, BigDecimalFieldValue(v)) => (k.unescaped, v.bigDecimal)
      case (k: FieldKey, LongFieldValue(v)) => (k.unescaped, v: java.lang.Long)
      case (k: FieldKey, BooleanFieldValue(v)) => (k.unescaped, v: java.lang.Boolean)
      case (k, v) => throw new ReactiveInfluxException(s"Unsupported field types! [${k.getClass}, ${v.getClass}]")
    }
  }

  def toScala(pointNoTime: PointNoTime): sc.PointNoTime = pointNoTime match {
    case javaPoint: JavaPoint => javaPoint.underlying
    case _ => ??? // TODO:
  }

  def toScala(writeParameters: WriteParameters): sc.command.write.WriteParameters = {
    sc.command.write.WriteParameters(
      retentionPolicy = Option(writeParameters.getRetentionPolicy),
      precision = Option(writeParameters.getPrecision).map(Precision.apply),
      consistency = Option(writeParameters.getConsistency).map(Consistency.apply)
    )
  }
}
