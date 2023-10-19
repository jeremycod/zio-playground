package dev.zio.caliban

import pureconfig.generic.ProductHint
import zio.{ZIO, ZLayer}
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigSource}
import pureconfig._
import pureconfig.generic.auto._
case class DatabaseConfig(url: String, username: String, password: String)
case class AppConfig( databaseConfig: DatabaseConfig)

object AppConfig {
  implicit def productHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))
  val layer                   = ZLayer.fromZIO(ZIO.fromEither(ConfigSource.default.load[AppConfig]))
}
