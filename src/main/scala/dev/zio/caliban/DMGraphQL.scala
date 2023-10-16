package dev.zio.caliban

import caliban.CalibanError.{ExecutionError, ParsingError, ValidationError}
import caliban.ResponseValue.ObjectValue
import caliban.Value.StringValue
import caliban.schema._
import caliban.{CalibanError, GraphQLInterpreter, RootResolver, graphQL}
import dev.zio.caliban.Queries.Env

import caliban.schema.ArgBuilder.auto._

object customSchema extends GenericSchema[Env]

import customSchema.auto._
object DMGraphQL {

  val api = graphQL(
    RootResolver(Queries.live)
  )
  val interpreter = api.interpreter//.map(errorHandler)

  private def errorHandler[R](
      interpreter: GraphQLInterpreter[R, CalibanError]
  ): GraphQLInterpreter[R, CalibanError] = interpreter.mapError {
    case err: ExecutionError =>
      err.copy(extensions = Some(ObjectValue(List("errorCode" -> StringValue("EXECUTION_ERROR")))))
    case err: ValidationError =>
      err.copy(extensions = Some(ObjectValue(List("errorCode" -> StringValue("VALIDATION_ERROR")))))
    case err: ParsingError =>
      err.copy(extensions = Some(ObjectValue(List("errorCode" -> StringValue("PARSING_ERROR")))))
    case err: CalibanError =>
      err
  }
}
