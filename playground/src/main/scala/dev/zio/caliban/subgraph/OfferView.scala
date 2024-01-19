package dev.zio.caliban.subgraph

import dev.zio.caliban.Queries.Env
import dev.zio.caliban.model
import dev.zio.caliban.persist.{OfferServiceDataStore, ProductServiceDataStore}
import dev.zio.caliban.resolver.{GetOffer, GetOfferProduct}
import dev.zio.caliban.table
import zio.query.ZQuery

import java.time.OffsetDateTime

case class OfferView(
    id: String,
    name: String,
    description: Option[String],
    discountId: Option[String],
    attributes: ZQuery[OfferServiceDataStore, Throwable, Map[String, String]],
    products: ZQuery[Env, Throwable, Seq[OfferProductView]]
)
object OfferView {
  def fromTable(o: table.Offer): OfferView = {
    OfferView(
      o.id,
      o.name,
      o.description,
      o.discountId,
      GetOffer.getAttributes(o.id),
      GetOfferProduct.getProducts(o.id)
    )
  }

}
