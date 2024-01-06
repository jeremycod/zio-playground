package com.playground.dss.omp.graphql.persist

import io.getquill.{Query, Quoted}
import io.getquill.mirrorContextWithQueryProbing.{quote, SqlInfixInterpolator}

trait DataStoreService[T] {
  val entityName: String

  def versionSelector(whereClause: String): String = versionSelectorByEntity(entityName, whereClause)

  def versionSelectorByEntity(entName: String, whereClause: String): String =
    s"""
      |SELECT id, profile, MAX(version) AS version FROM ${entName}s $whereClause GROUP BY id, profile
      |""".stripMargin

  protected def getMainEntityByProfile(profile: String): Quoted[Query[T]] = {
    val whereClause = s"WHERE profile = '$profile'"
    val versionSql = versionSelector(whereClause)
    quote {
      sql"""SELECT e.* FROM #${entityName}s as e
           JOIN (#$versionSql) as vs ON e.id = vs.id and e.profile = vs.profile and e.version = vs.version"""
        .as[Query[T]]
    }
  }
}
