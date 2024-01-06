package com.playground.dss.omp.graphql.security

import zio._

object SecurityHelpers {
  private def getSessionScope = ZIO.serviceWithZIO[Auth](_.currentSessionScope)

  def getUser: ZIO[Auth, AuthenticationFailure, String] = getSessionScope.map(_.name)

  def getProfile: ZIO[Auth, AuthenticationFailure, String] = getSessionScope.map(_.profile)

}
