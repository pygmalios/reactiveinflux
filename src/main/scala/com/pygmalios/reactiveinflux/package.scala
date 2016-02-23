package com.pygmalios

import com.pygmalios.reactiveinflux.command.write.{BigDecimalFieldValue, FieldValue}
import com.pygmalios.reactiveinflux.impl.{EscapedString, EscapedStringWithEquals}

package object reactiveinflux {
  implicit def stringToEscapedString(value: String): EscapedString = new EscapedString(value)
  implicit def stringToEscapedStringWithEquals(value: String): EscapedStringWithEquals = new EscapedStringWithEquals(value)
  implicit def fieldToEscapedStringWithEquals[T <: FieldValue](field: (String, T)): (EscapedStringWithEquals, T) =
    (field._1, field._2)
  implicit def tagToEscapedStringWithEquals(tag: (String, String)): (EscapedStringWithEquals, EscapedStringWithEquals) =
    (tag._1, tag._2)
  implicit def bigDecimalFieldToBigDecimalFieldValue(field: (String, BigDecimal)): (EscapedStringWithEquals,BigDecimalFieldValue) =
    (new EscapedStringWithEquals(field._1), BigDecimalFieldValue(field._2))
  implicit def intFieldToBigDecimalFieldValue(field: (String, Int)): (EscapedStringWithEquals,BigDecimalFieldValue) =
    (new EscapedStringWithEquals(field._1), BigDecimalFieldValue(BigDecimal(field._2)))
}
