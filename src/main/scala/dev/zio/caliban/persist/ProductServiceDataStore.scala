package dev.zio.caliban.persist

import dev.zio.caliban.table.OfferProduct
import io.getquill.jdbczio.Quill
import io.getquill.{Query, SnakeCase}
import zio.{ZIO, ZLayer}

import java.sql.SQLException

class ProductServiceDataStore(val quill: Quill.Postgres[SnakeCase]) extends DataStoreService {
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
    run(getOfferProducts("offer_product", version, offerId))
  }

  private def getOfferProducts(entityName: String, versionSelector: String, offerId: String) = {
    quote {
      sql"""SELECT e.* FROM #${entityName}s as e
           JOIN (#$versionSelector) as vs ON e.offer_id = vs.id and e.profile = vs.profile and e.version = vs.version
           WHERE e.offer_id = '#${offerId}'"""
        .as[Query[OfferProduct]]
    }
  }
}

object ProductServiceDataStore {
  def getProducts(offerId: String): ZIO[ProductServiceDataStore, SQLException, Seq[OfferProduct]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.fetchProducts(offerId))

  val layer = ZLayer.fromFunction(new ProductServiceDataStore(_))
}
