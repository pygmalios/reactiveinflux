package com.pygmalios.reactiveinflux.impl

import java.net.URI

import com.pygmalios.reactiveinflux.ReactiveInfluxDbName
import com.pygmalios.reactiveinflux.ReactiveInfluxCore
import com.pygmalios.reactiveinflux.command.write._
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar

@RunWith(classOf[JUnitRunner])
class PlayWSReactiveInfluxDbSpec extends FlatSpec with MockitoSugar {
  behavior of "write of single point"

  it should "create WriteCommand and execute it" in new TestScope {
    // Execute
    db.write(PointSpec.point1)

    // Verify
    verify(core).execute(new WriteCommand(
      baseUri     = uri,
      dbName      = dbName,
      points      = Seq(PointSpec.point1),
      params      = WriteParameters()
    ))
  }

  behavior of "write of multiple points"

  it should "create WriteCommand and execute it" in new TestScope {
    val writeCommand = new WriteCommand(
      baseUri     = uri,
      dbName      = dbName,
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
    val dbName = ReactiveInfluxDbName("db")
    val core = mock[ReactiveInfluxCore]
    val config = mock[DefaultReactiveInfluxConfig]
    val uri = new URI("http://whatever/")
    when(config.url).thenReturn(uri)
    when(core.config).thenReturn(config)
    val db = new PlayWSReactiveInfluxDb(dbName, core)
  }
}
