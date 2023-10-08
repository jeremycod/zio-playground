package dev.zio.caliban

import caliban.CalibanError.ValidationError
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{IO, Scope, ZEnvironment, ZIO, ZIOAppArgs, ZIOAppDefault}

object PocMain extends ZIOAppDefault {

  val program = for {
    offers <- OfferService.getOffers
    _ = offers

  } yield (offers)
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    program.provide(OfferService.live, Quill.Postgres.fromNamingStrategy(SnakeCase), QuillDataSource.live)
}
