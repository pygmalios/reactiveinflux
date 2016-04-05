package com.pygmalios.reactiveinflux

trait PingResult extends Serializable {
  def influxDbVersion: String
}
