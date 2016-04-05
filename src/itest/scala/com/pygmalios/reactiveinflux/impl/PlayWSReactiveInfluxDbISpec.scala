package com.pygmalios.reactiveinflux.impl

import com.pygmalios.reactiveinflux.ReactiveInfluxDbName
import com.pygmalios.reactiveinflux.command.query._
import com.pygmalios.reactiveinflux.command.write._
import com.pygmalios.reactiveinflux.error.{DatabaseNotFound, ReactiveInfluxError}
import com.pygmalios.reactiveinflux.itest.ITestConfig
import com.pygmalios.reactiveinflux.{ReactiveInfluxResultError, _}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.concurrent.{AsyncAssertions, IntegrationPatience, ScalaFutures}
import org.scalatest.junit.JUnitRunner

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class PlayWSReactiveInfluxDbISpec extends FlatSpec with ScalaFutures with IntegrationPatience with AsyncAssertions {
  behavior of "create"

  it should "create a test DB" in new TestScope {
    try {
      db.create().futureValue
    }
    finally {
      db.drop().futureValue
    }
  }

  it should "not fail if DB already exists" in new TestScope {
    try {
      db.create().futureValue
      db.create().futureValue
    }
    finally {
      db.drop().futureValue
    }
  }

  behavior of "drop"

  it should "not fail if DB doesn't already exists" in new TestScope {
    db.drop(failIfNotExists = false).futureValue
    db.drop(failIfNotExists = false).futureValue
  }

  ignore should "fail if DB doesn't already exists" in new TestScope {
    db.drop(failIfNotExists = false).futureValue
    assertError(db.drop(failIfNotExists = true), classOf[DatabaseNotFound], "database not found: PlayWSReactiveInfluxDbISpec")
  }

  behavior of "write"

  it should "write a single point" in new TestScope {
    withDb { db =>
      db.write(PointSpec.point1)
    }
  }

  it should "write two points" in new TestScope {
    withDb { db =>
      db.write(Seq(PointSpec.point1, PointSpec.point2))
    }
  }

  it should "write a batch of 1000 points" in new TestScope {
    val measurement = "m1"
    val points = (1 to 1000).map { i =>
      val dateTime = PointSpec.dateTime1.plusSeconds(i)
      Point(dateTime,
        measurement,
        Map(
          "tk1" -> (i % 7).toString,
          "tk2" -> (i % 23).toString,
          "tk3" -> (i % 47).toString),
        Map("fk" -> LongFieldValue(i)))
    }

    withDb { db =>
      db.write(points).flatMap { _ =>
        db.query(Query(s"SELECT * FROM $measurement"))
          .map { queryResult =>
            val rows = queryResult.result.singleSeries.rows
            assert(rows.size == 1000)
          }
      }
    }
  }

  behavior of "query"

  it should "get a single point with no provided time format" in new QueryTestScope {
    writeAndTestEpoch(None)
  }

  it should "get a single point with nanosecond time format" in new QueryTestScope {
    writeAndTestEpoch(Some(NanoEpoch))
  }

  it should "get a single point with microsecond time format" in new QueryTestScope {
    writeAndTestEpoch(Some(MicroEpoch))
  }

  it should "get a single point with millisecond time format" in new QueryTestScope {
    writeAndTestEpoch(Some(MilliEpoch))
  }

  it should "get a single point with second time format" in new QueryTestScope {
    writeAndTestEpoch(Some(SecondEpoch))
  }

  it should "get a single point with minute time format" in new QueryTestScope {
    writeAndTestEpoch(Some(MinuteEpoch))
  }

  private class QueryTestScope extends TestScope {
    def writeAndTestEpoch(epoch: Option[Epoch]): Any = {
      withDb { db =>
        val writeParameters = WriteParameters(precision = epoch.map(Precision(_)))
        db.write(PointSpec.point1, writeParameters).flatMap { _ =>
          testEpoch(epoch, writeParameters)
        }
      }
    }

    def testEpoch(epoch: Option[Epoch], writeParameters: WriteParameters): Future[Unit] = {
      db.query(Query("SELECT * FROM " + PointSpec.point1.measurement), QueryParameters(epoch = epoch)).map { queryResult =>
        val series = queryResult.result.singleSeries
        assert(series.name == PointSpec.point1.measurement.unescaped)

        val row = series.singleRow

        val expextedTime = writeParameters.precision.map(_.round(PointSpec.point1.time)).getOrElse(PointSpec.point1.time)
        assert(row.time == expextedTime, epoch)
        assert(series(row, "fk") == BigDecimalValue(-1))
      }
    }
  }

  private class TestScope {
    val client = new PlayWSReactiveInflux(ITestConfig.reactiveInfluxConfig)
    val db = new PlayWSReactiveInfluxDb(
      dbName      = ReactiveInfluxDbName("PlayWSReactiveInfluxDbISpec"),
      core        = client)

    def withDb(action: (PlayWSReactiveInfluxDb) => Future[Any]): Any = {
      val result = db.create().flatMap { _ =>
        action(db)
      }
      try {
        result.futureValue
      }
      finally {
        db.drop().futureValue
      }
    }

    def assertError(f: => Future[_], error: Class[_ <: ReactiveInfluxError], message: String): Unit =
      assertError(f, error, Some(message))
    def assertError(f: => Future[_], error: Class[_ <: ReactiveInfluxError], message: Option[String] = None): Unit = {
      whenReady(f.failed) {
        case ex: ReactiveInfluxResultError =>
          ex.errors.find(_.getClass == error) match {
            case Some(e) if message != Some(e.message) => fail(s"Expected error message [$message] got [${e.message}]")
            case None => fail(s"Expected error not found. [$ex]")
            case _ =>
          }
        case other => fail(s"Unexpected exception.", other)
      }
    }
  }
}
