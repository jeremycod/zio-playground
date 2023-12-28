package dev.zio.caliban.persist

import io.getquill.jdbczio.Quill
import io.getquill.{Query, Quoted, SnakeCase}
import zio._

import java.sql.SQLException
import java.util.UUID
import dev.zio.caliban.table

class ProductServiceDataStore (val quill: Quill.Postgres[SnakeCase]) extends DataStoreService[table.Product] {
  import quill._
  override val entityName: String = "product"

  protected def getProducts(versionSelector: String): Quoted[Query[table.Product]] = {
    quote {
      sql"""SELECT e.* FROM products as e
         JOIN (#$versionSelector) as vs ON e.id = vs.id and e.profile = vs.profile and e.version = vs.version"""
        .as[Query[table.Product]]
    }
  }

  def fetchProducts(profile: String): ZIO[Any, SQLException, Seq[table.Product]] = {
    val prodVersionSelector = versionSelector(s"WHERE profile = '$profile'")
    run(getProducts(prodVersionSelector))
  }
  def getProductEntitlements(productId: UUID, profile: String): ZIO[Any, SQLException, Seq[table.Entitlement]] = {
    val entVersionSelector = versionSelectorByEntity("entitlement", s"WHERE profile = '$profile'")
    val prodVersionSelector = versionSelector(s"WHERE profile = '$profile' AND id = '$productId'")
    run(
      quote {
        sql"""SELECT e.* from entitlements as e JOIN(#$entVersionSelector)  as vs ON e.id = vs.id and e.profile = vs.profile and e.version = vs.version
                JOIN product_entitlements pe on e.id = pe.entitlement_id
                  JOIN (#$prodVersionSelector) as pvs ON pe.product_id = pvs.id and pe.profile = pvs.profile and pe.version = pvs.version"""
          .as[Query[table.Entitlement]]

      }

    )
  }
}

object ProductServiceDataStore {
  def fetchProducts(profile: String): ZIO[ProductServiceDataStore, Throwable, Seq[table.Product]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.fetchProducts(profile)
      .fold(
        fail => {
          throw fail
        },
        success => success

      ))

  def getProductEntitlements(productId: UUID, profile: String): ZIO[ProductServiceDataStore, Throwable, Seq[table.Entitlement]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.getProductEntitlements(productId, profile))

  val layer = ZLayer.fromFunction(new ProductServiceDataStore(_))
}