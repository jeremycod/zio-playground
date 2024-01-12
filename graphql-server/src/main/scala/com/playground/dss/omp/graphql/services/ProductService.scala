package com.playground.dss.omp.graphql.services


import com.playground.dss.omp.graphql.security.SecurityHelpers
import com.playground.dss.omp.graphql.subgraph.Types.CreateOrEditBaseProductInput
import com.playground.dss.omp.graphql.Queries.Env
import com.playground.dss.omp.graphql.persist.{ProductServiceDataStore, ProductServiceWriteDataStore}
import com.playground.dss.omp.graphql.security.SecurityHelpers
import com.playground.dss.omp.graphql.subgraph.Product
import com.playground.dss.omp.graphql.subgraph.Types._
import com.playground.dss.omp.graphql.table.TableHelpers
import zio._

import java.util.UUID

trait ProductService {
  def createBaseProduct(product: CreateOrEditBaseProductInput): ZIO[Env, Throwable, scala.Option[Product]]
  def createOneTimePurchaseProduct(product: CreateOrEditOneTimePurchaseProductInput)
  : ZIO[Env, Throwable, scala.Option[Product]]
}
class ProductServiceLive() extends ProductService {

  private def createProduct(
                             productName: String,
                             productDescription: scala.Option[String],
                             productType: ProductEntityType,
                             attributes: Map[String, String],
                             entitlements: List[String]
                           ): ZIO[Env, Throwable, scala.Option[Product]] = {
    for {
      profile <- SecurityHelpers.getProfile
      user <- SecurityHelpers.getUser
      productId = UUID.randomUUID()
      existingEntitlements <- ProductServiceDataStore.fetchEntitlementsByNames(entitlements, profile)
      productToCreate =
        TableHelpers.makeProduct(productId, productName, productDescription, productType, user, profile)
      existingNames = existingEntitlements.map(ent => ent.name)
      missingEntitlements =
        entitlements.filterNot(e => existingNames.contains(e)).map(e =>
          TableHelpers.makeEntitlement(e, user, profile))
      _ <- ProductServiceWriteDataStore.createProduct(
        productToCreate,
        attributes,
        existingEntitlements.map(_.id).toList,
        missingEntitlements)

      productWithAttributes <- ProductServiceDataStore.fetchProduct(profile, productId)

    } yield productWithAttributes.map(p => Product.fromTableWithAttributes(p))
  }

  override def createBaseProduct(product: CreateOrEditBaseProductInput): ZIO[Env, Throwable, scala.Option[Product]] = {
    val attributes = Map(
      "status" -> "DRAFT"
    )
    createProduct(product.name, product.description, product.`type`, attributes, product.entitlements)
  }

  override def createOneTimePurchaseProduct(product: CreateOrEditOneTimePurchaseProductInput)
  : ZIO[Env, Throwable, scala.Option[Product]] = {
    val attributes = Map(
      "status" -> "DRAFT",
      "eventDate" -> product.startDate,
      "catalogDate" -> product.endDate
    )
    createProduct(product.name, product.description, product.`type`, attributes, product.entitlements)
  }
}
object ProductService {
  def createBaseProduct(product: CreateOrEditBaseProductInput): ZIO[Env, Throwable, scala.Option[Product]] =
    ZIO.serviceWithZIO[ProductService](_.createBaseProduct(product))

  def createOneTimePurchaseProduct(product: CreateOrEditOneTimePurchaseProductInput)
  : ZIO[Env, Throwable, scala.Option[Product]] =
    ZIO.serviceWithZIO[ProductService](_.createOneTimePurchaseProduct(product))

  val layer: ULayer[ProductServiceLive] = ZLayer.succeed(new ProductServiceLive())

}
