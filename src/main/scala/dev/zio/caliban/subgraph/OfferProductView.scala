package dev.zio.caliban.subgraph

import dev.zio.caliban.table
case class OfferProductView(
    priceId: Option[String],
    referenceOffer: Option[String],
    phaseId: Option[String],
    legacy: String,
    skus: String,
    version: String
)

object OfferProductView {
  def fromTable(op: table.OfferProduct): OfferProductView = {
    OfferProductView(
      op.priceId,
      op.referenceOffer,
      op.phaseId,
      op.legacy,
      op.skus,
      op.version
    )
  }
}
