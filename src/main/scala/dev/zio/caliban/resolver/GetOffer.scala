package dev.zio.caliban.resolver

import dev.zio.caliban.Queries
import dev.zio.caliban.persist.{OfferServiceDataStore, ProductServiceDataStore}
import dev.zio.caliban.subgraph.OfferView
import dev.zio.caliban.transformer.{OfferFinal, TiaraOfferTransformer}
import zio.ZIO
import zio.query.ZQuery

object GetOffer {

  def fetchAllOffers(profile: String) = {
    val value = ZQuery.fromZIO(OfferServiceDataStore.getOffers)
      .map(_.map { offer =>
        val oView = OfferView.fromTable(offer)
        val tiaraOfferView = TiaraOfferTransformer.offerViewToTiara(oView)
        tiaraOfferView
      })

    value

  }

}
