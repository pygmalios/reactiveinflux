package com.pygmalios.reactiveinflux.impl

import akka.http.scaladsl.model.Uri
import com.pygmalios.reactiveinflux.ReactiveInfluxCore
import com.pygmalios.reactiveinflux.command.write._
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class ActorSystemReactiveInfluxDbSpec extends FlatSpec with MockitoSugar {
  behavior of "write of single point"

  it should "create WriteCommand and execute it" in new TestScope {
    // Execute
    db.write(PointSpec.point1)

    // Verify
    verify(core).execute(new WriteCommand(
      baseUri     = uri,
      dbName      = dbName,
      dbUsername  = Some(dbUsername),
      dbPassword  = Some(dbPassword),
      points      = Seq(PointSpec.point1),
      params      = WriteParameters()
    ))
  }

  behavior of "write of multiple points"

  it should "create WriteCommand and execute it" in new TestScope {
    val writeCommand = new WriteCommand(
      baseUri     = uri,
      dbName      = dbName,
      dbUsername  = Some(dbUsername),
      dbPassword  = Some(dbPassword),
      points      = Seq(PointSpec.point1, PointSpec.point2),
      params      = WriteParameters(
        retentionPolicy = Some("x"),
        precision = Some(Minute),
        consistency = Some(All)
      )
    )

    // Execute
    db.write(writeCommand.points, writeCommand.params)

    // Verify
    verify(core).execute(writeCommand)
  }

  private class TestScope {
    val dbName = "db"
    val dbUsername = "u"
    val dbPassword = "p"
    val core = mock[ReactiveInfluxCore]
    val config = mock[DefaultReactiveInfluxConfig]
    val uri = Uri("http://whatever/")
    when(config.uri).thenReturn(uri)
    when(core.config).thenReturn(config)
    val db = new ActorSystemReactiveInfluxDb(dbName, Some(dbUsername), Some(dbPassword), core)
  }
}
