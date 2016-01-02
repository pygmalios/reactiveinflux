package com.pygmalios.reactiveinflux.impl.api

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.pygmalios.reactiveinflux.impl.ReactiveInfluxConfig
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}

class ActorSystemReactiveInfluxClientISpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with FunSuiteLike with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
  private lazy val reactiveInfluxConfig = ReactiveInfluxConfig()

  def this() = this(ActorSystem("ActorSystemReactiveInfluxClientISpec", ReactiveInfluxConfig().reactiveinflux))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  test("Send ping to test InfluxDB") {
    // Prepare
    val client = new ActorSystemReactiveInfluxClient(system, reactiveInfluxConfig)

    // Execute
    assert(client.ping().futureValue.influxDbVersion == "0.9.6.1")
  }
}