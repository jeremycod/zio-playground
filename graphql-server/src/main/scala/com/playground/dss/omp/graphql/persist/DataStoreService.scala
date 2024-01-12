package com.playground.dss.omp.graphql.persist

import io.getquill.jdbczio.Quill
import io.getquill.{Query, Quoted, SnakeCase}
import com.playground.dss.omp.graphql.table.public

@SuppressWarnings(Array("org.wartremover.warts.All"))
abstract class DataStoreService[T](val quill: Quill.Postgres[SnakeCase.type]) {
  val entityName: String
  import quill._

  implicit val productSchema = schemaMeta[public.Product]("products")
  implicit val productIdSchema = schemaMeta[public.ProductId]("product_ids")
  implicit val entitlementSchema = schemaMeta[public.Entitlement]("entitlements")
  implicit val entitlementIdSchema = schemaMeta[public.EntitlementId]("entitlement_ids")
  implicit val productAttributeValue = schemaMeta[public.ProductAttributeValue]("product_attribute_values")

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
