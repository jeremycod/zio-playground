package dev.zio.caliban.resolver

import dev.zio.caliban.persist.{DataStoreService, OfferServiceDataStore, ProductServiceDataStore}
import dev.zio.caliban.subgraph.OfferView
import zio.query.ZQuery

import java.sql.SQLException

object GetOffer {
  type Env = OfferServiceDataStore with ProductServiceDataStore

  def fetchAllOffers(profile: String): ZQuery[Env, Throwable, Seq[OfferView]] =
    ZQuery.fromZIO(OfferServiceDataStore.getOffers)
      .map(_.map(offer => OfferView.fromTable(offer)))

}
