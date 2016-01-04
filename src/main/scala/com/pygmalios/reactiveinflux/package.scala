package com.pygmalios

import com.pygmalios.reactiveinflux.model.{EscapedString, EscapedStringWithEquals, FieldValue}

package object reactiveinflux {
  implicit def stringToEscapedString(value: String): EscapedString = new EscapedString(value)
  implicit def stringToEscapedStringWithEquals(value: String): EscapedStringWithEquals = new EscapedStringWithEquals(value)
  implicit def fieldToEscapedStringWithEquals[T <: FieldValue](field: (String, T)): (EscapedStringWithEquals, T) =
    (field._1, field._2)
  implicit def tagToEscapedStringWithEquals(tag: (String, String)): (EscapedStringWithEquals, EscapedStringWithEquals) =
    (tag._1, tag._2)
}
