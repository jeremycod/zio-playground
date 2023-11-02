package dev.zio.caliban

import dev.zio.caliban.Queries.Env
import dev.zio.caliban.persist.{OfferServiceDataStore, ProductServiceDataStore}
import dev.zio.caliban.resolver.GetOffer
import dev.zio.caliban.transformer.OfferFinal
import zio.query.ZQuery


case class OffersQueryArgs(profile: String)


case class FindAllOffersArgs(profile: String)
final case class Queries(
    offers: OffersQueryArgs => ZQuery[Env, Throwable, Seq[ZQuery[Env, Throwable, OfferFinal]]]
)
object Queries {
  type Env = OfferServiceDataStore with ProductServiceDataStore
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
    args => GetOffer.fetchAllOffers(args.profile)
  )

}
