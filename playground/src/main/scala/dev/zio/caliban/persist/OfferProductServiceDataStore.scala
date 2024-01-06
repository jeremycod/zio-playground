package dev.zio.caliban.persist

import dev.zio.caliban.table.OfferProduct
import io.getquill.jdbczio.Quill
import io.getquill.{Query, SnakeCase}
import zio.{ZIO, ZLayer}

import java.sql.SQLException
import dev.zio.caliban.table
class OfferProductServiceDataStore(val quill: Quill.Postgres[SnakeCase]) extends DataStoreService[table.Offer] {
  import quill._


  def fetchProducts(offerId: String): ZIO[Any, SQLException, Seq[OfferProduct]] = {
    val version = versionSelector("offer", "where profile = 'main' and author='transform'")
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

  override val entityName: String = "offer"
}

object OfferProductServiceDataStore {
  def getProducts(offerId: String): ZIO[OfferProductServiceDataStore, SQLException, Seq[OfferProduct]] =
    ZIO.serviceWithZIO[OfferProductServiceDataStore](_.fetchProducts(offerId))

  val layer = ZLayer.fromFunction(new OfferProductServiceDataStore(_))
}
