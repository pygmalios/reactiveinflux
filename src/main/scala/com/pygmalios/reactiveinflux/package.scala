package com.pygmalios

import java.net.URI

import com.pygmalios.reactiveinflux.Query
import com.pygmalios.reactiveinflux.impl.{EscapedString, EscapedStringWithEquals}
import com.pygmalios.reactiveinflux.sync.{SyncReactiveInflux, SyncReactiveInfluxDb}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

package object reactiveinflux {
  implicit def stringToEscapedString(value: String): EscapedString = new EscapedString(value)
  implicit def stringToEscapedStringWithEquals(value: String): EscapedStringWithEquals = new EscapedStringWithEquals(value)
  implicit def fieldToEscapedStringWithEquals[T <: FieldValue](field: (String, T)): (EscapedStringWithEquals, T) =
    (field._1, field._2)
  implicit def tagToEscapedStringWithEquals(tag: (String, String)): (EscapedStringWithEquals, EscapedStringWithEquals) =
    (tag._1, tag._2)
  implicit def stringFieldToStringFieldValue(field: (String, String)): (EscapedStringWithEquals,StringFieldValue) =
    (new EscapedStringWithEquals(field._1), StringFieldValue(field._2))
  implicit def booleanFieldToBooleanFieldValue(field: (String, Boolean)): (EscapedStringWithEquals,BooleanFieldValue) =
    (new EscapedStringWithEquals(field._1), BooleanFieldValue(field._2))
  implicit def bigDecimalFieldToBigDecimalFieldValue(field: (String, BigDecimal)): (EscapedStringWithEquals,BigDecimalFieldValue) =
    (new EscapedStringWithEquals(field._1), BigDecimalFieldValue(field._2))
  implicit def intFieldToBigDecimalFieldValue(field: (String, Int)): (EscapedStringWithEquals,BigDecimalFieldValue) =
    (new EscapedStringWithEquals(field._1), BigDecimalFieldValue(BigDecimal(field._2)))
  implicit def doubleFieldToBigDecimalFieldValue(field: (String, Double)): (EscapedStringWithEquals,BigDecimalFieldValue) =
    (new EscapedStringWithEquals(field._1), BigDecimalFieldValue(BigDecimal(field._2)))
  implicit def stringToReactiveInfluxDbName(dbName: String): ReactiveInfluxDbName = ReactiveInfluxDbName(dbName)
  implicit def uriToReactiveInfluxConfig(url: URI): ReactiveInfluxConfig = ReactiveInfluxConfig(url)
  implicit def stringToQuery(query: String) = Query(query)

  def withInfluxDb[T](config: ReactiveInfluxConfig, dbName: ReactiveInfluxDbName)(action: ReactiveInfluxDb => Future[T]): Future[T] = {
    val reactiveInflux = ReactiveInflux(config)
    action(reactiveInflux.database(dbName))
      .andThen {
        case _ =>
          reactiveInflux.close()
      }
  }

  def syncInfluxDb[T](config: ReactiveInfluxConfig, dbName: ReactiveInfluxDbName)(action: SyncReactiveInfluxDb => T): T = {
    val syncReactiveInflux = SyncReactiveInflux(config)
    try {
      action(syncReactiveInflux.database(dbName))
    }
    finally {
      syncReactiveInflux.close()
    }
  }
}
