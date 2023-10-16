package dev.zio.caliban.resolver

import dev.zio.caliban.persist.{OfferServiceDataStore, ProductServiceDataStore}
import dev.zio.caliban.subgraph.OfferView
import dev.zio.caliban.transformer.{OfferFinal, TiaraOfferTransformer}
import zio.ZIO
import zio.query.ZQuery


object GetOffer {
  type Env = OfferServiceDataStore with ProductServiceDataStore

  def fetchAllOffers(profile: String): ZQuery[Env, Throwable, Seq[OfferFinal]] = {
    val value = ZQuery.fromZIO(OfferServiceDataStore.getOffers)
      .map(_.map(offer => OfferView.fromTable(offer)))
      .map(_.map(offerView => for {
        r <- TiaraOfferTransformer.offerViewToTiara(offerView)
      } yield r))
    //value.flatten
  }

  def toUQuery() = {
    val effect = OfferServiceDataStore.getOffers
    val value: ZQuery[Any, Nothing, ZQuery[Any, Nothing, String]] = ZQuery.fromZIO(effect)
    value.flatten

}

}
