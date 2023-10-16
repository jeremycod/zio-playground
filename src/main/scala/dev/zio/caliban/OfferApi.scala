/*package dev.zio.caliban

import caliban.RootResolver
import caliban.graphQL
import caliban.schema.ArgBuilder.auto._
import caliban.schema.Schema.auto._
import dev.zio.caliban.table
import zio.{ZIO, ZLayer}
import zio.query.ZQuery
import Model._
import dev.zio.caliban.persist.{DataStoreService, OfferServiceDataStore, ProductServiceDataStore}
import dev.zio.caliban.subgraph.{OfferProductView, OfferView}

import java.sql.SQLException

object Model {
  type MyQuery[+A] = ZQuery[Any, SQLException, A]

  case class OffersQueryArgs(profile: String)

  case class Query(offers: OffersQueryArgs => MyQuery[ Seq[OfferView]])

  case class FindAllOffersArgs(profile: String)
}

class OfferApi(dataStore: OfferServiceDataStore, productDataStore: ProductServiceDataStore) {

  /*  case class Query(
      @GQLDescription("Return all top entities")
      offers: OffersQueryArgs => MyQuery[Seq[OfferView]]
  )*/
  val query: Query = Query(args => fetchAllOffers(args.profile))

  def getProducts(offerId: String): MyQuery[Seq[OfferProductView]] =
    ZQuery.fromZIO(productDataStore.fetchProducts(offerId))
      .map(_.map(offerProduct => OfferProductView.fromTable(offerProduct)))

  def fetchAllOffers(profile: String): MyQuery[Seq[OfferView]] =
    ZQuery.fromZIO(dataStore.fetchOffers)
      .map(_.map(offer => OfferView.fromTable(offer)))
  //.map(offer => entities.map(fetchOfferProducts))

  val interpreter = graphQL(RootResolver(query)).interpreter

}
object OfferApi {
  private def create(dataStore: OfferServiceDataStore, productDataStore: ProductServiceDataStore) =
    new OfferApi(dataStore, productDataStore)
  val layer: ZLayer[OfferServiceDataStore with ProductServiceDataStore, Nothing, OfferApi] =
    ZLayer.fromFunction(create _)
}*/
