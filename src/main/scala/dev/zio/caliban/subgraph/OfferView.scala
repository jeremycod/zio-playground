package dev.zio.caliban.subgraph


import dev.zio.caliban.model
import dev.zio.caliban.persist.ProductServiceDataStore
import dev.zio.caliban.resolver.GetOfferProduct
import dev.zio.caliban.table
import zio.query.ZQuery

import java.time.OffsetDateTime

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

  def toDomainOffer(offerView: OfferView): model.Offer = {
    model.Offer(
      id = offerView.id,
      name = offerView.name,
      description = offerView.description,
      discountId = offerView.discountId,
      products = offerView.products.map(_.),
      attributes = Map.empty,
      messages = Map.empty[String, String],
      metadata = Map.empty[String, String],
      transitions = Map.empty,
      legacy = Map.empty,
      author = "",
      datetime = OffsetDateTime.now(),
      profile = "test",
      version = None,
      eligibility = None
    )
  }

}