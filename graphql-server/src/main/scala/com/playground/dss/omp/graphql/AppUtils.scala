package com.playground.dss.omp.graphql

import zio.logging.backend.SLF4J
import zio._
import zio.logging.{LogColor, LogFilter, LogFormat}

import java.time.format.DateTimeFormatter

object AppUtils {
  val logFormat = {
    import zio.logging.LogFormat._

    val userId = LogFormat.annotation("user_id")

    label("timestamp", timestamp(DateTimeFormatter.ISO_LOCAL_DATE_TIME).fixed(28)).color(LogColor.BLUE) |-|
      label("level", level).highlight |-|
      label("thread", fiberId).color(LogColor.WHITE) |-|
      (space + label("user_id", userId).highlight) |-|
      label("message", quoted(line)).highlight +
      (space + label("cause", cause).highlight).filter(LogFilter.causeNonEmpty)

  }
  val layer = Runtime.removeDefaultLoggers >>> SLF4J.slf4j(logFormat)

  // val layer: ZLayer[Any, Nothing, ApplicationConfiguration with Unit] = ZLayer.fromZIO(ZIO.succeed(config)) +!+ logLayer

}
