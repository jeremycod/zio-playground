package dev.zio.caliban

import pureconfig.generic.ProductHint
import zio.{Runtime, ZIO, ZLayer}
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigSource}
import pureconfig._
import pureconfig.generic.auto._
import zio.logging.{LogColor, LogFilter, LogFormat}
import zio.logging.backend.SLF4J

import java.time.format.DateTimeFormatter
case class DatabaseConfig(url: String, username: String, password: String)
case class AppConfig(databaseConfig: DatabaseConfig)

object AppConfig {
  implicit def productHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))

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
  val logLayer = Runtime.removeDefaultLoggers >>> SLF4J.slf4j(logFormat)
  val layer = ZLayer.fromZIO(ZIO.fromEither(ConfigSource.default.load[AppConfig])) +!+ logLayer
}
