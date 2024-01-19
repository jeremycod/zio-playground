package dev.zio.caliban.transformer

import dev.zio.caliban.Queries.Env
import dev.zio.caliban.subgraph.{OfferProductView, ReferenceOfferView}
import zio.query.ZQuery

case class OfferProductFinal(
    productId: String,
    priceId: Option[String],
    referenceOffer: Option[String],
    refOffer: Option[ReferenceOfferView],
    phaseId: Option[String],
    legacy: String,
    skus: String,
    version: String
)
object OfferProductTransformer {
  def offerProductViewToFinalProduct(opv: OfferProductView): ZQuery[Env, Throwable, OfferProductFinal] = {
    val result =
      for {
        refOffer <- opv.refOffer
      } yield OfferProductFinal(
        opv.productId,
        opv.priceId,
        opv.referenceOffer,
        refOffer,
        opv.phaseId,
        opv.legacy,
        opv.skus,
        opv.version
      )

    result
  }

}
