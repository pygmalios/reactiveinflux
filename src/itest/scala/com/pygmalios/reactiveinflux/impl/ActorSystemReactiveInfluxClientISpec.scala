package com.pygmalios.reactiveinflux.impl

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.pygmalios.reactiveinflux.itest.ITestConfig
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}

class ActorSystemReactiveInfluxClientISpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with FunSuiteLike with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
  def this() = this(ActorSystem("ActorSystemReactiveInfluxClientISpec", ITestConfig.reactiveInfluxConfig.reactiveinflux))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  test("Send ping to test InfluxDB") {
    val testScope = new TestScope
    import testScope._

    // Execute
    assert(client.ping().futureValue.influxDbVersion == "0.9.6.1")
  }

  private class TestScope {
    val client = new ActorSystemReactiveInflux(system, ITestConfig.reactiveInfluxConfig)
  }
}