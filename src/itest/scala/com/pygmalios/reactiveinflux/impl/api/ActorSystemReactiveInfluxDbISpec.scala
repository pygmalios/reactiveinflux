package com.pygmalios.reactiveinflux.impl.api

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.pygmalios.reactiveinflux.itest.ITestConfig
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}

class ActorSystemReactiveInfluxDbISpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with FunSuiteLike with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
  def this() = this(ActorSystem("ActorSystemReactiveInfluxDbISpec", ITestConfig.reactiveInfluxConfig.reactiveinflux))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  test("Create and drop test DB") {
    val testScope = new TestScope
    import testScope._

    val dbName = "ActorSystemReactiveInfluxClientISpec"
    try {
      db.create().futureValue
    }
    finally {
      db.drop().futureValue
    }
  }

  private class TestScope {
    val client = new ActorSystemReactiveInfluxClient(system, ITestConfig.reactiveInfluxConfig)
    val db = new ActorSystemReactiveInfluxDb("ActorSystemReactiveInfluxClientISpec", client)
  }
}
