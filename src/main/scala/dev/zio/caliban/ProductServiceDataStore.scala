package dev.zio.caliban

import dev.zio.caliban.model.{Offer, OfferProduct}
import io.getquill.{Query, SnakeCase}
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZLayer}

import java.sql.SQLException

class ProductServiceDataStore(val quill: Quill.Postgres[SnakeCase]) {
  import quill._

  protected def profileVersionSelector(entityName: String, profile: String) =
    s"""
       SELECT id, profile, version
       FROM (
         SELECT id, profile, deleted, version, row_number() OVER (PARTITION BY id, profile ORDER BY version DESC) as rn
         FROM ${entityName}s
       ) as t WHERE rn = 1 and deleted = false and profile = '$profile'"""

  def fetchProducts(offerId: String): ZIO[Any, SQLException, Seq[OfferProduct]] = {
    val version = profileVersionSelector("offer", "main")
    run(getMainEntities("offer", version))
  }

  private def getMainEntities(entityName: String, versionSelector: String) = {
    quote {
      sql"""SELECT e.* FROM #${entityName}s as e
           JOIN (#$versionSelector) as vs ON e.id = vs.id and e.profile = vs.profile and e.version = vs.version"""
        .as[Query[OfferProduct]]
    }
  }
}

object ProductServiceDataStore {
  def getProducts(offerId: String): ZIO[ProductServiceDataStore, SQLException, Seq[OfferProduct]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.fetchProducts(offerId))

  val layer = ZLayer.fromFunction(new ProductServiceDataStore(_))
}
