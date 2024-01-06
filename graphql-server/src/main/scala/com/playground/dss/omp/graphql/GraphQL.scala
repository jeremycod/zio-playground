package com.playground.dss.omp.graphql

import caliban.schema._
import caliban.{graphQL, CalibanError, GraphQL, GraphQLInterpreter, RootResolver}
import com.playground.dss.omp.graphql.Queries.Env
import zio._
import caliban.schema.ArgBuilder.auto._

object customSchema extends GenericSchema[Env]

object GraphQL {

  import customSchema.auto._

  val api: GraphQL[Env] = graphQL(
    RootResolver(Queries.live)
  )

  private def errorHandler[R](
      interpreter: GraphQLInterpreter[R, CalibanError]
  ): GraphQLInterpreter[R, CalibanError] = interpreter.mapError {
    err: CalibanError => err
  }
  val interpreter: IO[CalibanError.ValidationError, GraphQLInterpreter[Env, CalibanError]] = api.interpreter
    .map(errorHandler)

}
