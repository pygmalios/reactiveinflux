package com.pygmalios.reactiveinflux.jawa

import com.pygmalios.reactiveinflux.jawa.wrapper.{JavaPingResult, JavaReactiveInflux}
import com.pygmalios.{reactiveinflux => sc}

object Conversions {
  def toScala(reactiveInfluxConfig: ReactiveInfluxConfig): sc.ReactiveInfluxConfig = {
    sc.ReactiveInfluxConfig(
      reactiveInfluxConfig.getUrl,
      Option(reactiveInfluxConfig.getUsername),
      Option(reactiveInfluxConfig.getPassword))
  }

  def toJava(reactiveInflux: sc.ReactiveInflux): ReactiveInflux = new JavaReactiveInflux(reactiveInflux)
  def toJava(pingResult: sc.PingResult): PingResult = new JavaPingResult(pingResult)
}
