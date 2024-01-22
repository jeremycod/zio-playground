package com.playground.dss.omp.graphql.services

import com.playground.dss.omp.graphql.security.SecurityHelpers
import com.playground.dss.omp.graphql.subgraph.Types.CreateOrEditBaseProductInput
import com.playground.dss.omp.graphql.Queries.Env
import com.playground.dss.omp.graphql.persist.{ProductServiceDataStore, ProductServiceWriteDataStore}
import com.playground.dss.omp.graphql.security.SecurityHelpers
import com.playground.dss.omp.graphql.subgraph.Product
import com.playground.dss.omp.graphql.subgraph.Types._
import com.playground.dss.omp.graphql.table.TableHelpers
import com.playground.dss.omp.graphql.Errors
import com.playground.dss.omp.graphql.Errors.DataInputErrorMsg
import zio._

import java.util.UUID

trait ProductService {
  def createBaseProduct(product: CreateOrEditBaseProductInput): ZIO[Env, Throwable, scala.Option[Product]]
  def createOneTimePurchaseProduct(product: CreateOrEditOneTimePurchaseProductInput)
      : ZIO[Env, Throwable, scala.Option[Product]]

  def updateBaseProduct(id: ID, product: CreateOrEditBaseProductInput): ZIO[Env, Throwable, scala.Option[Product]]

  def updateOneTimePurchaseProduct(
                                    id: ID,
                                    product: CreateOrEditOneTimePurchaseProductInput
                                  ): ZIO[Env, Throwable, scala.Option[Product]]
}
class ProductServiceLive() extends ProductService {

  private def createUpdateProduct(
                                   productId: ID,
                                   productName: String,
                                   productDescription: scala.Option[String],
                                   productType: ProductEntityType,
                                   attributes: Map[String, String],
                                   entitlementNames: List[String]
                                 ): ZIO[Env, Throwable, scala.Option[Product]] = {
    for {
      profile <- SecurityHelpers.getProfile
      user <- SecurityHelpers.getUser
      productId = UUID.randomUUID()
      existingEntitlements <- ProductServiceDataStore.fetchEntitlementsByNames(entitlementNames, profile)
      productToCreate =
        TableHelpers.makeProduct(productId, productName, productDescription, productType, user, profile)
      existingNames = existingEntitlements.map(ent => ent.name)
      missingEntitlements =
        entitlementNames.filterNot(e => existingNames.contains(e)).map(e => TableHelpers.makeEntitlement(e, user, profile))
      _ <-
        if (missingEntitlements.sizeIs > 0) ZIO.fail(DataInputErrorMsg(
          Errors.ErrorCode.INCORRECT_REQUEST,
          s"Error while trying to create product. Entitlements ${missingEntitlements.mkString(",")} not found in DB",
          Map(
            "missing entitlements" -> missingEntitlements.mkString(",")
          )
        ))
        else ZIO.succeed(())
      productToCreate =
        TableHelpers.makeProduct(productId, productName, productDescription, productType, user, profile)

      _ <- ProductServiceWriteDataStore.createProduct(
        productToCreate,
        attributes,
        existingEntitlements.map(_.id).toList)

      productWithAttributes <- ProductServiceDataStore.fetchProduct(profile, productId)

    } yield productWithAttributes.map(p => Product.fromTableWithAttributes(p))
  }

  override def createBaseProduct(product: CreateOrEditBaseProductInput): ZIO[Env, Throwable, scala.Option[Product]] = {
    val attributes = Map(
      "status" -> "DRAFT"
    )
    createUpdateProduct(
      UUID.randomUUID(),
      product.name,
      product.description,
      product.`type`,
      attributes,
      product.entitlements)
  }

  override def createOneTimePurchaseProduct(product: CreateOrEditOneTimePurchaseProductInput)
      : ZIO[Env, Throwable, scala.Option[Product]] = {
    val attributes = Map(
      "status" -> "DRAFT",
      "eventDate" -> product.startDate,
      "catalogDate" -> product.endDate
    )
    createUpdateProduct(
      UUID.randomUUID(),
      product.name,
      product.description,
      product.`type`,
      attributes,
      product.entitlements)
  }

  override def updateBaseProduct(
                                  id: ID,
                                  product: CreateOrEditBaseProductInput
                                ): ZIO[Env, Throwable, scala.Option[Product]] = {
    val attributes = Map(
      "Status" -> "DRAFT"
    )
    createUpdateProduct(id, product.name, product.description, product.`type`, attributes, product.entitlements)
  }

  override def updateOneTimePurchaseProduct(
                                             id: ID,
                                             product: CreateOrEditOneTimePurchaseProductInput
                                           ): ZIO[Env, Throwable, scala.Option[Product]] = {
    val attributes = Map(
      "Status" -> "DRAFT",
      "eventDate" -> product.startDate,
      "catalogDate" -> product.endDate
    )
    createUpdateProduct(
      UUID.randomUUID(),
      product.name,
      product.description,
      product.`type`,
      attributes,
      product.entitlements)
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
