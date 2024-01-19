package com.playground.dss.omp.graphql

import caliban.ZHttpAdapter
import caliban.interop.tapir.HttpInterpreter
import com.playground.dss.omp.graphql.persist.{ProductServiceDataStore, ProductServiceWriteDataStore}
import com.playground.dss.omp.graphql.security.Auth
import com.playground.dss.omp.graphql.services.ProductService
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.http._
import zio.http.Server
import zio._

import scala.annotation.nowarn
import sttp.tapir.json.circe._

@nowarn
object GraphQLZio extends ZIOAppDefault {
  /*  val authenticator: ZIO[ApplicationConfiguration, Nothing, Authenticator] = for {
    jwkKey <- ZIO.serviceWithZIO[ApplicationConfiguration](config => ZIO.succeed(config.genieToken.jwkKey))
  } yield Authenticator.make(jwkKey)*/

  private val program = for {
    _ <- ZIO.logInfo("Initializing GraphQL server")
    interpreter <- GraphQL.interpreter
    //auth <- authenticator
    httpApp = Routes(
                Method.ANY / "api" / "graphql" ->
                  ZHttpAdapter.makeHttpService(HttpInterpreter(interpreter)) @@ Auth.middleware()
              ).toHttpApp
    _ <- Server.serve(httpApp)
    _ <- ZIO.logInfo("GraphQL server started")
  } yield ()

  override def run =
    program.provide(
      AppUtils.layer,
      AppConfig.layer,
      ProductServiceDataStore.layer,
      ProductServiceWriteDataStore.layer,
      ProductService.layer,
      Quill.Postgres.fromNamingStrategy(SnakeCase),
      QuillDataSource.layer,
      Auth.http,
      ZLayer.fromZIO(ZIO.succeed(
        Server.Config.default.port(8091))),
      Server.live
    )

}
