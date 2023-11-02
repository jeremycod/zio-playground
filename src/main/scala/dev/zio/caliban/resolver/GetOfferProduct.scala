package dev.zio.caliban.resolver


import dev.zio.caliban.Queries.Env
import dev.zio.caliban.persist.ProductServiceDataStore
import dev.zio.caliban.subgraph.OfferProductView
import dev.zio.caliban.transformer.{OfferProductFinal, OfferProductTransformer}
import zio.{Runtime, Unsafe, ZIO}
import zio.query.ZQuery

import java.sql.SQLException

object GetOfferProduct {



  def getProducts(offerId: String): ZQuery[Env, Throwable, Seq[OfferProductView]] = {
    GetOffer.getAttributes(offerId).flatMap { attributes =>
      ZQuery.fromZIO(ProductServiceDataStore.getProducts(offerId))
        .map(_.map{offerProduct =>
          val offerProductView = OfferProductView.fromTable(offerProduct, attributes)

          offerProductView
        })




    }

  }

}
