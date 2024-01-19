package dev.zio.caliban.transformer

import dev.zio.caliban.Queries.Env
import dev.zio.caliban.subgraph.{OfferProductView, OfferView}
import zio.query.ZQuery

case class OfferFinal(
    id: String,
    name: String,
    description: Option[String],
    discountId: Option[String],
    attributes: Map[String, String],
    products: Seq[OfferProductView]
)
object TiaraOfferTransformer {
  def offerViewToTiara(offerView: OfferView): ZQuery[Env, Throwable, OfferFinal] = {
    val result =
      for {
        prods <- offerView.products
        //transformedProducts = prods.map(p => OfferProductTransformer.offerProductViewToFinalProduct(p))
        //newProds <- transformedProducts
        attributes <- offerView.attributes
      } yield OfferFinal(
        offerView.id,
        offerView.name,
        offerView.description,
        offerView.discountId,
        attributes,
        prods
      )
    result
  }

}
