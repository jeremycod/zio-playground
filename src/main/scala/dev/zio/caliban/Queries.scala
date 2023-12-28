package dev.zio.caliban

import dev.zio.caliban.Queries.Env
import dev.zio.caliban.persist.{OfferProductServiceDataStore, OfferServiceDataStore, ProductServiceDataStore}
import dev.zio.caliban.resolver.{GetOffer, GetProduct}
import dev.zio.caliban.subgraph.{OfferView, Product}
import dev.zio.caliban.transformer.OfferFinal
import zio.query.ZQuery


case class OffersQueryArgs(profile: String)
case class ProductsQueryArgs(profile: String)

final case class Queries(
   // offers: OffersQueryArgs => ZQuery[Env, Throwable, Seq[OfferView]],
    products: ProductsQueryArgs => ZQuery[Env, Throwable, Seq[Product]]
)
object Queries {
  type Env = OfferServiceDataStore with OfferProductServiceDataStore with ProductServiceDataStore
  val offers: String =
    """{
      |offers(profile : "main") {
      | id
      | name
      | description
      | discountId
      | attributes {key, value}
      | products {
      |   priceId
      |   referenceOffer
      |   refOffer {
      |     id
      |   }
      |   phaseId
      |   legacy
      |   skus
      |   version
      | }
      |}
      |}
      |""".stripMargin


  val live = Queries(
 //   args => GetOffer.fetchAllOffers(args.profile),
    args => GetProduct.getAllProducts(args.profile)
  )

}
