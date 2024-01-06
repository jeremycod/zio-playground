package dev.zio.caliban.table

case class OfferProduct(
    offerId: String,
    productId: String,
    priceId: Option[String],
    referenceOffer: Option[String],
    phaseId: Option[String],
    legacy: String,
    skus: String,
    version: String
)
