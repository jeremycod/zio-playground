package dev.zio.caliban.persist
import io.getquill.{Query, Quoted}
import io.getquill.mirrorContextWithQueryProbing.{SqlInfixInterpolator, quote}
trait DataStoreService[T] {
  val entityName: String
  def versionSelector(entityName: String, whereClause: String): String =
    s"""
       |SELECT id, profile, MAX(version) AS version FROM ${entityName}s $whereClause GROUP BY id, profile
       |""".stripMargin

  def versionSelector(whereClause: String): String = versionSelectorByEntity(entityName, whereClause)

  def versionSelectorByEntity(entName: String, whereClause: String): String =
    s"""
       |SELECT id, profile, MAX(version) AS version FROM ${entName}s $whereClause GROUP BY id, profile
       |""".stripMargin

  protected def getMainEntityByProfile(profile: String): Quoted[Query[T]] = {
    val whereClause = s"WHERE profile = '$profile'"
    val versionSql = versionSelector(whereClause)
    val q = quote {
      sql"""SELECT e.* FROM #${entityName}s as e
         JOIN (#$versionSql) as vs ON e.id = vs.id and e.profile = vs.profile and e.version = vs.version"""
        .as[Query[T]]
    }
    q
  }
}
