package com.playground.dss.omp.graphql

object Errors {
  final case class DataAccessErrorMsg(msg: String) extends Throwable(msg)

}
