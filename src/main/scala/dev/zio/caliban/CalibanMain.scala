package dev.zio.caliban

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
//import caliban.schema.ArgBuilder.auto._
//import caliban.schema.Schema.auto._
import dev.zio.caliban.persist.{OfferServiceDataStore, ProductServiceDataStore}
//import dev.zio.caliban.resolver.GetOffer

object CalibanMain extends ZIOAppDefault {

  val program = for {
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
