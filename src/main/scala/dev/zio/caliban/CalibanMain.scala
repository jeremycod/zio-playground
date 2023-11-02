package dev.zio.caliban

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import dev.zio.caliban.persist.{OfferServiceDataStore, ProductServiceDataStore}

object CalibanMain extends ZIOAppDefault {

  val program = for {
    _ <- ZIO.logInfo("START")
    interpreter <- DMGraphQL.interpreter
    result <- interpreter.execute(Queries.offers)
    _ <- zio.ZIO.debug(result.data.toString)

  } yield (result)
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    program.provide(
      AppConfig.layer,
      OfferServiceDataStore.layer,
      ProductServiceDataStore.layer,
      Quill.Postgres.fromNamingStrategy(SnakeCase),
      QuillDataSource.layer)
}
