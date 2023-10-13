package dev.zio.caliban

import caliban.RootResolver
import caliban.GraphQL.graphQL
import caliban.schema.Annotations.GQLDescription
import dev.zio.caliban.model.{Offer, OfferProduct}
import zio.{ZIO, ZLayer}
import zio.query.{Query, ZQuery}

import java.sql.SQLException

class OfferApi(dataStore: OfferServiceDataStore, productDataStore: ProductServiceDataStore) {

  type MyQuery[+A] = ZQuery[Any, SQLException, A]

  case class OffersQueryArgs(profile: String)
  case class Query(offers: OffersQueryArgs => MyQuery[Seq[OfferView]])

  case class OfferView(id: String, offer: Offer, products: MyQuery[Seq[OfferProductView]])
  case class OfferProductView(offerProduct: OfferProduct)
  case class FindAllOffersArgs(profile: String)

  /*  case class Query(
      @GQLDescription("Return all top entities")
      offers: OffersQueryArgs => MyQuery[Seq[OfferView]]
  )*/
  val resolver: Query = Query(args => fetchAllOffers(args.profile))

  def getProducts(offerId: String): MyQuery[Seq[OfferProductView]] =
    ZQuery.fromZIO(productDataStore.fetchProducts(offerId))
      .map(_.map(offerProduct => OfferProductView( offerProduct)))

  private def fetchAllOffers(profile: String): MyQuery[Seq[OfferView]] =
    ZQuery.fromZIO(dataStore.fetchOffers)
      .map(_.map(offer => OfferView(offer.id, offer, getProducts(offer.id))))
  //.map(offer => entities.map(fetchOfferProducts))


  val interpreter = graphQL(RootResolver(resolver)).interpreter

}
object OfferApi {
  private def create(dataStore: OfferServiceDataStore, productDataStore: ProductServiceDataStore) =
    new OfferApi(dataStore, productDataStore)
  val layer: ZLayer[OfferServiceDataStore with ProductServiceDataStore, Nothing, OfferApi] =
    ZLayer.fromFunction(create _)
}
