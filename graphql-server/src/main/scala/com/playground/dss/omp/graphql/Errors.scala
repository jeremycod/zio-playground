package com.playground.dss.omp.graphql

object Errors {
  sealed trait ErrorCode extends scala.Product with scala.Serializable
  object ErrorCode {
    case object DATA_ACCESS_ERROR extends ErrorCode
    case object DATA_WRITE_ERROR extends ErrorCode
    case object INCORRECT_REQUEST extends ErrorCode
  }

  abstract class GraphQLError(val code: ErrorCode, val msg: String, val metadata: Map[String, String] = Map.empty)
    extends Throwable(s"${code.toString}: $msg")
  final case class DataAccessErrorMsg(
                                       override val code: ErrorCode,
                                       override val msg: String,
                                       override val metadata: Map[String, String] = Map.empty
                                     ) extends GraphQLError(code, msg, metadata)
  final case class DataInputErrorMsg(
                                      override val code: ErrorCode,
                                      override val msg: String,
                                      override val metadata: Map[String, String] = Map.empty
                                    ) extends GraphQLError(code, msg, metadata)

}
