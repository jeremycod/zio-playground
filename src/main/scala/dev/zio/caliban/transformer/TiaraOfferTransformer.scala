package dev.zio.caliban.transformer

import dev.zio.caliban.persist.ProductServiceDataStore
import dev.zio.caliban.subgraph.{OfferProductView, OfferView}
import zio.query.ZQuery

case class OfferFinal(
                      id: String,
                      name: String,
                      description: Option[String],
                      discountId: Option[String],
                      products: Seq[OfferProductView]
                    )
object TiaraOfferTransformer {
  def offerViewToTiara(offerView: OfferView): ZQuery[ProductServiceDataStore, Throwable, OfferFinal] = {
    val result = for {
      prods <- offerView.products
    } yield OfferFinal(
      offerView.id,
      offerView.name,
      offerView.description,
      offerView.discountId,
      prods
    )
   result
  }

}
