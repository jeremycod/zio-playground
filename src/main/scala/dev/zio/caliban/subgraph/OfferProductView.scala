package dev.zio.caliban.subgraph

import dev.zio.caliban.persist.{OfferServiceDataStore, ProductServiceDataStore}
import dev.zio.caliban.resolver.{GetOffer, GetOfferProduct}
import dev.zio.caliban.table
import zio.query.ZQuery
case class OfferProductView(
                           productId: String,
    priceId: Option[String],
    referenceOffer: Option[String],
    refOffer: ZQuery[OfferServiceDataStore, Throwable, Option[ReferenceOfferView]],
    phaseId: Option[String],
    legacy: String,
    skus: String,
    version: String
)

object OfferProductView {
  def fromTable(op: table.OfferProduct, attributes: Map[String, String]): OfferProductView = {
    val offerBasePrice = attributes.getOrElse("offer_base_price", "0").toInt
    val currencyCode = attributes.getOrElse("currencyCode", "USD")
    val countryCode = attributes.getOrElse("countryCode", "US")
    OfferProductView(
      op.productId,
      op.priceId,
      op.referenceOffer,
      GetOffer.getReferenceOffer(op.offerId, op.productId, offerBasePrice, currencyCode, countryCode),
      op.phaseId,
      op.legacy,
      op.skus,
      op.version
    )
  }
}
