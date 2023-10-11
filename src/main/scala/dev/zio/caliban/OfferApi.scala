package dev.zio.caliban

import caliban.RootResolver
import caliban.GraphQL.graphQL
import caliban.schema.Annotations.GQLDescription
import dev.zio.caliban.model.Offer
import zio.{ZIO, ZLayer}
import zio.query.Query

class OfferApi(dataStore: OfferServiceDataStore) {

  case class FindAllOffersArgs(profile: String)

  case class Queries(
      @GQLDescription("Return all top entities")
      offers: ZIO[Any, Throwable, Seq[Offer]]
  )
  val queries = Queries(fetchAllOffers)

  private def fetchAllOffers =
    dataStore.fetchOffers
    //  .map(entities => entities.map(fetchOfferProducts))

  private def fetchOfferProducts = ???

  val interpreter = graphQL(RootResolver(queries)).interpreter

}
object OfferApi {
  private def create(dataStore: OfferServiceDataStore) = new OfferApi(dataStore)
  val layer: ZLayer[OfferServiceDataStore, Nothing, OfferApi] = ZLayer.fromFunction(create _)
}
