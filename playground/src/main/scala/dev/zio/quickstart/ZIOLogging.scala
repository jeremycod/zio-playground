package dev.zio.quickstart

import com.typesafe.scalalogging.StrictLogging
import zio._
import zio.logging.backend.SLF4J

object ZIOLogging extends App {
  def functionA(): IO[Object, Unit] = {
    val zioStrictLogging = new ZIOLoggingStrict
    for {
      _ <- ZIO.logInfo("Function A.1 ")
      _ <- zioStrictLogging.functionB
      - <- ZIO.logInfo("Function A.2")
    } yield (2)
  }

  val x = functionA()
    .provideLayer(Runtime.removeDefaultLoggers >>> SLF4J.slf4j(
      format = zio.logging.LogFormat.colored
    ))
  Unsafe.unsafe { implicit unsafe =>
    Runtime.default.unsafe.run {
      x.debug("program")
    }
  }

}

class ZIOLoggingStrict extends StrictLogging {
  def functionB: IO[Object, Unit] = {
    for {
      _ <- ZIO.logInfo("Function B.1 ")
      - <- ZIO.logInfo("Function B.2")
    } yield ()
  }
}
