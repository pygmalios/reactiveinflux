package com.pygmalios.reactiveinflux.model

class EscapedString(value: String) extends BaseEscapedString(value)
class EscapedStringWithEquals(value: String) extends BaseEscapedString(value) {
  override val escaped = super.escaped.replace("=", "\\=")
}

abstract class BaseEscapedString(val value: String) extends Serializable {
  def escaped: String = value.replace(" ", "\\ ").replace(",", "\\,")

  def canEqual(other: Any): Boolean = other.isInstanceOf[EscapedString]

  override def equals(other: Any): Boolean = other match {
    case that: EscapedString =>
      (that canEqual this) &&
        value == that.value
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(value)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = escaped
}