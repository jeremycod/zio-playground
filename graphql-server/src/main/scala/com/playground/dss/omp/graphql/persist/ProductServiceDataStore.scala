package com.playground.dss.omp.graphql.persist

import com.playground.dss.omp.graphql.{Errors, FixedSnakeCase}
import com.playground.dss.omp.graphql.subgraph.Types.{QueryProductArgs, QueryProductsArgs}
import io.getquill.jdbczio.Quill
import io.getquill.{Query, Quoted}
import zio._

import java.sql.SQLException
import com.playground.dss.omp.graphql.table.{Entitlement, Product}
import com.playground.dss.omp.graphql.subgraph.Types.{QueryProductArgs, QueryProductsArgs}
import com.playground.dss.omp.graphql.table.Entitlement
import com.playground.dss.omp.graphql.{Errors, FixedSnakeCase, table}

import java.util.UUID

@SuppressWarnings(Array("org.wartremover.warts.All"))
class ProductServiceDataStore(val quill: Quill.Postgres[FixedSnakeCase.type]) extends DataStoreService[table.Product] {
  import quill._
  override val entityName: String = "product"

  def fetchProduct(profile: String, queryProductArgs: QueryProductArgs): ZIO[Any, SQLException, Option[table.Product]] = {

    run {
      for {
        prodVersion <-
          query[table.Product].filter(p => p.id == lift(queryProductArgs.id.toString) && p.profile == lift(profile))
            .groupByMap(p => (p.id, p.profile))(p =>
              (p.id, p.profile, max(p.version)))
        product <-
          query[table.Product].join(p => p.id == prodVersion._1 && p.profile == prodVersion._2 && p.version == prodVersion._3)
      } yield (product)
    }.map(_.headOption)
  }

  def fetchProducts(profile: String, queryProductArgs: QueryProductsArgs): ZIO[Any, SQLException, Seq[table.Product]] = {
    val whereClause = if (queryProductArgs.productIds.isDefined) {
      val ids = queryProductArgs.productIds.getOrElse(Seq.empty).map(id => s"'$id'").mkString(",")
      s"WHERE profile = '$profile' and id in ($ids)"
    } else
      s"WHERE profile = '$profile'"
    val prodVersionSelector = versionSelector(whereClause)
    run(
      getProducts(prodVersionSelector)
    )
  }

  protected def getProducts(
      versionSelector: String
  ): Quoted[Query[table.Product]] = {
    quote {
      sql"""SELECT e.* FROM products as e
         JOIN (#$versionSelector) as vs ON e.id = vs.id and e.profile = vs.profile and e.version = vs.version"""
        .as[Query[table.Product]]
    }
  }
  def getProductEntitlements(productId: UUID, profile: String): ZIO[Any, SQLException, Seq[Entitlement]] = {
    val entVersionSelector = versionSelectorByEntity("entitlement", s"WHERE profile = '$profile'")
    val prodVersionSelector = versionSelector(s"WHERE profile = '$profile' AND id = '$productId'")
    run(
      quote {
        sql"""SELECT e.* from entitlements as e JOIN(#$entVersionSelector)  as vs ON e.id = vs.id and e.profile = vs.profile and e.version = vs.version
                JOIN product_entitlements pe on e.id = pe.entitlement_id
                  JOIN (#$prodVersionSelector) as pvs ON pe.product_id = pvs.id and pe.profile = pvs.profile and pe.version = pvs.version"""
          .as[Query[Entitlement]]
      }
    )
  }
}

object ProductServiceDataStore {

  def fetchProduct(
      profile: String,
      queryProductArgs: QueryProductArgs
  ): ZIO[ProductServiceDataStore, Throwable, Option[table.Product]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.fetchProduct(profile, queryProductArgs)
      .catchAllCause(cause =>
        ZIO.logErrorCause(s"Failed to retrieve product, ", cause) *> ZIO.fail(
          Errors.DataAccessErrorMsg(
            s"Failed to retrieve product ${queryProductArgs.id} from DB: ${cause.failureOption.getOrElse("").toString} "))))

  def fetchProducts(
      profile: String,
      queryProductArgs: QueryProductsArgs
  ): ZIO[ProductServiceDataStore, Throwable, Seq[table.Product]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.fetchProducts(profile, queryProductArgs))
      .catchAllCause(cause =>
        ZIO.logErrorCause(s"Failed to retrieve product, ", cause) *> ZIO.fail(
          Errors.DataAccessErrorMsg(
            s"Failed to retrieve products with arguments: ${queryProductArgs.toString} from DB: ${cause.failureOption.getOrElse(
                "").toString} ")))

  def getProductEntitlements(
      productId: UUID,
      profile: String
  ): ZIO[ProductServiceDataStore, Throwable, Seq[Entitlement]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.getProductEntitlements(productId, profile))

  val layer: ZLayer[Quill.Postgres[FixedSnakeCase.type], Nothing, ProductServiceDataStore] =
    ZLayer.fromFunction(new ProductServiceDataStore(_))
}
