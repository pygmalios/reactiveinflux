package com.pygmalios.reactiveinflux

import akka.http.scaladsl.model.Uri
import com.pygmalios.reactiveinflux.impl.DefaultReactiveInfluxConfig
import com.typesafe.config.Config

trait ReactiveInfluxConfig extends Serializable {
  def reactiveinflux: Config
  def uri: Uri
}

object ReactiveInfluxConfig {
  def apply(config: Option[Config] = None): ReactiveInfluxConfig = new DefaultReactiveInfluxConfig(config)
}