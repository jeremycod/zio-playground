package dev.zio.caliban

import caliban.CalibanError.ValidationError
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{IO, Scope, ZEnvironment, ZIO, ZIOAppArgs, ZIOAppDefault}

object PocMain extends ZIOAppDefault {

  val program = for {
    //offerApi <- ZIO.
      allOffers <- OfferServiceDataStore.getOffers
    _ = allOffers

  } yield (allOffers)
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    program.provide(OfferServiceDataStore.layer, Quill.Postgres.fromNamingStrategy(SnakeCase), QuillDataSource.layer)
}
