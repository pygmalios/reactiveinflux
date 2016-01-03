package com.pygmalios.reactiveinflux.api.result

trait PingResponse extends Serializable {
  def influxDbVersion: String
}
