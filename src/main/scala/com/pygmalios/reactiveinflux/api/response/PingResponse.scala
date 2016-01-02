package com.pygmalios.reactiveinflux.api.response

trait PingResponse extends Serializable {
  def influxDbVersion: String
}
