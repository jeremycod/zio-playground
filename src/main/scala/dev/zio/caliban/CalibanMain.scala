package dev.zio.caliban

import caliban.ZHttpAdapter
import caliban.interop.tapir.HttpInterpreter
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}
import dev.zio.caliban.persist.{OfferProductServiceDataStore, OfferServiceDataStore, ProductServiceDataStore}
import zio.http._
import zio.http.Server
/*import zio.json.JsonEncoder
import zio.json.JsonDecoder*/
import sttp.tapir.json.circe._
object CalibanMain extends ZIOAppDefault {

  private val program = for {
    _ <- ZIO.logInfo("Initializing GraphQL server")
    interpreter <- DMGraphQL.interpreter
    httpApp = Routes(
      Method.ANY / "api" / "graphql" ->
        ZHttpAdapter.makeHttpService(HttpInterpreter(interpreter))
    ).toHttpApp
    _ <- Server.serve(httpApp)
    _ <- ZIO.logInfo("GraphQL server started")
  } yield ()
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    program.provide(
      AppConfig.layer,
      OfferServiceDataStore.layer,
      OfferProductServiceDataStore.layer,
      ProductServiceDataStore.layer,
      Quill.Postgres.fromNamingStrategy(SnakeCase),
      QuillDataSource.layer,
      ZLayer.succeed(Server.Config.default.port(8091)),
      Server.live)

}
