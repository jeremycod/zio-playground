package dev.zio.caliban.subgraph


import dev.zio.caliban.persist.ProductServiceDataStore
import dev.zio.caliban.resolver.{GetOffer, GetOfferProduct}
import dev.zio.caliban.table
import zio.query.ZQuery

import java.sql.SQLException
case class OfferView(
    id: String,
    name: String,
    description: Option[String],
    discountId: Option[String],
    products: ZQuery[ProductServiceDataStore, Throwable, Seq[OfferProductView]]
)
object OfferView {
  def fromTable(o: table.Offer): OfferView = {
    OfferView(
      o.id,
      o.name,
      o.description,
      o.discountId,
      GetOfferProduct.getProducts(o.id)

    )
  }

}