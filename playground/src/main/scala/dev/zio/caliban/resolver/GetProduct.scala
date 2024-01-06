package dev.zio.caliban.resolver

import dev.zio.caliban.persist.ProductServiceDataStore
import dev.zio.caliban.subgraph.{Entitlement, Product}
import zio.ZIO
import zio.query.ZQuery

import java.util.UUID

object GetProduct {

  def getAllProducts(profile: String): ZQuery[ProductServiceDataStore, Throwable, Seq[Product]] = {
    val value = ZQuery.fromZIO(ProductServiceDataStore.fetchProducts(profile)
      .tapError(e => ZIO.logError(s"Get all products failed with error ${e.toString}")))
      .map(_.map { product => Product.fromTable(product) })

    value

  }

  def getProductEntitlements(
      productId: UUID,
      profile: String
  ): ZQuery[ProductServiceDataStore, Throwable, Set[Entitlement]] = {
    ZQuery.fromZIO(ProductServiceDataStore.getProductEntitlements(productId, profile)
      .tapError(e => ZIO.logError(s"Get product entitlements failed with error ${e.toString}")))
      .map(_.map { entitlement => Entitlement.fromTable(entitlement) }.toSet)
  }

}
