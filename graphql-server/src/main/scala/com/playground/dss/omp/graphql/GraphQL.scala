package com.playground.dss.omp.graphql

import caliban.CalibanError.{ExecutionError, ParsingError, ValidationError}
import caliban.ResponseValue.ObjectValue
import caliban.Value.StringValue
import caliban.schema._
import caliban.{CalibanError, GraphQL, GraphQLInterpreter, RootResolver, graphQL}
import com.playground.dss.omp.graphql.Queries.Env
import zio._
import caliban.schema.ArgBuilder.auto._
import com.playground.dss.omp.graphql.Errors.GraphQLError

object customSchema extends GenericSchema[Env]

object GraphQL {

  import customSchema.auto._

  val api: GraphQL[Env] = graphQL(
    RootResolver(Queries.live, Mutations.live)
  )

  @SuppressWarnings(Array("org.wartremover.warts.All"))
  private def errorHandler[R](
                               interpreter: GraphQLInterpreter[R, CalibanError]
                             ): GraphQLInterpreter[R, CalibanError] = interpreter.mapError {

    case err @ ExecutionError(_, _, _, Some(error: GraphQLError), _) =>
      err.copy(
        extensions = Some(
          ObjectValue(
            List(
              ("errorCode", StringValue(error.code.toString)),
              ("metadata", ObjectValue(error.metadata.map { case (k, v) => k -> StringValue(v) }.toList))
            )
          )
        ),
        msg = error.msg
      )
    case err: ExecutionError =>
      err.copy(extensions = Some(ObjectValue(List(("errorCode", StringValue("EXECUTION_ERROR"))))))
    case err: ValidationError =>
      err.copy(extensions = Some(ObjectValue(List(("errorCode", StringValue("VALIDATION_ERROR"))))))
    case err: ParsingError =>
      err.copy(extensions = Some(ObjectValue(List(("errorCode", StringValue("PARSING_ERROR"))))))
  }

  val interpreter: IO[CalibanError.ValidationError, GraphQLInterpreter[Env, CalibanError]] = api.interpreter
    .map(errorHandler)

}
