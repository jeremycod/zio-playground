package dev.zio.caliban

/*import caliban.schema.ArgBuilder.auto._
import caliban.schema.Schema.auto._*/
import dev.zio.caliban.Queries.Env
import dev.zio.caliban.persist.{OfferServiceDataStore, ProductServiceDataStore}
import dev.zio.caliban.resolver.GetOffer
import dev.zio.caliban.subgraph.OfferView
import dev.zio.caliban.transformer.OfferFinal
import zio.query.ZQuery


case class OffersQueryArgs(profile: String)

//case class Query(offers: OffersQueryArgs => ZQuery[GetOffer.Env, Throwable, Seq[OfferView]])

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
      | products {
      | priceId
      | referenceOffer
      | phaseId
      | legacy
      | skus
      | version
      | }
      |}
      |}
      |""".stripMargin


  val live = Queries(
    args => GetOffer.fetchAllOffers(args.profile)
  )

}
