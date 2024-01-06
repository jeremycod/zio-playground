package com.playground.dss.omp.graphql

sealed trait RequestError extends Throwable

object RequestError {
  sealed trait HTTPError extends RequestError

  final case class Unauthenticated(message: String) extends RequestError
  final case class Forbidden(message: String) extends RequestError
}
