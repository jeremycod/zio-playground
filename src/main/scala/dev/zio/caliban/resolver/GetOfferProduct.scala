package dev.zio.caliban.resolver


import dev.zio.caliban.persist.ProductServiceDataStore
import dev.zio.caliban.subgraph.OfferProductView
import zio.query.ZQuery

import java.sql.SQLException

object GetOfferProduct {
  type Env = ProductServiceDataStore

  def getProducts(offerId: String): ZQuery[Env, Throwable, Seq[OfferProductView]] =
    ZQuery.fromZIO(ProductServiceDataStore.getProducts(offerId))
      .map(_.map(offerProduct => OfferProductView.fromTable(offerProduct)))

}
