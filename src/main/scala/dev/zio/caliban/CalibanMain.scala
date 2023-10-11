package dev.zio.caliban

import caliban.CalibanError.ValidationError
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{IO, Scope, ZEnvironment, ZIO, ZIOAppArgs, ZIOAppDefault}

object CalibanMain extends ZIOAppDefault {

  val program = for {
    offerApi <- ZIO.service[OfferApi]
    interpreter <- offerApi.interpreter
    result <- interpreter.execute(Queries.offers)
    _ <- zio.ZIO.debug(result.data.toString)

  } yield (result)
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    program.provide(
      OfferApi.layer,
      OfferServiceDataStore.layer,
      Quill.Postgres.fromNamingStrategy(SnakeCase),
      QuillDataSource.layer)
}
