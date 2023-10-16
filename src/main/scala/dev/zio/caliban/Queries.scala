package dev.zio.caliban

/*import caliban.schema.ArgBuilder.auto._
import caliban.schema.Schema.auto._*/
import dev.zio.caliban.Queries.Env
import dev.zio.caliban.persist.{OfferServiceDataStore, ProductServiceDataStore}
import dev.zio.caliban.resolver.GetOffer
import dev.zio.caliban.subgraph.OfferView
import zio.query.ZQuery


case class OffersQueryArgs(profile: String)

//case class Query(offers: OffersQueryArgs => ZQuery[GetOffer.Env, Throwable, Seq[OfferView]])

case class FindAllOffersArgs(profile: String)
final case class Queries(
    offers: OffersQueryArgs => ZQuery[Env, Throwable, Seq[OfferView]]
)
object Queries {
  type Env = OfferServiceDataStore with ProductServiceDataStore
  val offers: String =
    """{
      |offers(profile : "main") {
      | id
      | offer {
      |   id
      |   name
      |   description
      |   discountId
      |   author
      |   profile
      |   version
      | }
      | products {
      |   offerProduct{
      |     referenceOffer
      |   }
      | }
      |}
      |}
      |""".stripMargin


  val live = Queries(
    args => GetOffer.fetchAllOffers(args.profile)
  )

}
