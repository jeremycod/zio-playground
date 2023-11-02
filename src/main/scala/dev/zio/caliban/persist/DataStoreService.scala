package dev.zio.caliban.persist

trait DataStoreService {
  def versionSelector(entityName: String, whereClause: String): String =
    s"""
       |SELECT id, profile, MAX(version) AS version FROM ${entityName}s $whereClause GROUP BY id, profile
       |""".stripMargin

}
