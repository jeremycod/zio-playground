package dev.zio.caliban.resolver


import dev.zio.caliban.Queries.Env
import dev.zio.caliban.persist.OfferProductServiceDataStore
import dev.zio.caliban.subgraph.OfferProductView
import zio.query.ZQuery

object GetOfferProduct {


  def getProducts(offerId: String): ZQuery[Env, Throwable, Seq[OfferProductView]] = {
    GetOffer.getAttributes(offerId).flatMap { attributes =>
      ZQuery.fromZIO(OfferProductServiceDataStore.getProducts(offerId))
        .map(_.map{offerProduct =>
          val offerProductView = OfferProductView.fromTable(offerProduct, attributes)

          offerProductView
        })




    }

  }

}
