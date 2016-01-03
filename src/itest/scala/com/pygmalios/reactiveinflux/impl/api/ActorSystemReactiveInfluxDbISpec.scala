package com.pygmalios.reactiveinflux.impl.api

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.pygmalios.reactiveinflux.api.ReactiveinfluxResultError
import com.pygmalios.reactiveinflux.api.response.errors.DatabaseAlreadyExists
import com.pygmalios.reactiveinflux.itest.ITestConfig
import org.scalatest.concurrent.{AsyncAssertions, IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}

class ActorSystemReactiveInfluxDbISpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with FunSuiteLike with BeforeAndAfterAll with ScalaFutures with IntegrationPatience with AsyncAssertions {
  def this() = this(ActorSystem("ActorSystemReactiveInfluxDbISpec", ITestConfig.reactiveInfluxConfig.reactiveinflux))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  test("Create and drop test DB") {
    val testScope = new TestScope
    import testScope._

    try {
      db.create().futureValue
    }
    finally {
      db.drop().futureValue
    }
  }

  test("Create should fail if DB already exists") {
    val testScope = new TestScope
    import testScope._

    try {
      db.create(failIfExists = true).futureValue
      whenReady(db.create(failIfExists = true).failed) {
        case ex: ReactiveinfluxResultError => assert(ex.errors == Set(DatabaseAlreadyExists))
        case other => fail(s"Unexpected exception. [$other]")
      }
    }
    finally {
      db.drop().futureValue
    }
  }

  test("Create should not fail if DB already exists") {
    val testScope = new TestScope
    import testScope._

    try {
      db.create(failIfExists = true).futureValue
      db.create(failIfExists = false).futureValue
    }
    finally {
      db.drop().futureValue
    }
  }

  private class TestScope {
    val client = new ActorSystemReactiveInfluxClient(system, ITestConfig.reactiveInfluxConfig)
    val db = new ActorSystemReactiveInfluxDb("ActorSystemReactiveInfluxDbISpec", client)
  }
}
