package com.playground.dss.omp.graphql.services

import com.playground.dss.omp.graphql.security.SecurityHelpers
import com.playground.dss.omp.graphql.subgraph.Types.CreateOrEditBaseProductInput
import com.playground.dss.omp.graphql.Queries.Env
import com.playground.dss.omp.graphql.persist.{ProductServiceDataStore, ProductServiceWriteDataStore}
import com.playground.dss.omp.graphql.security.SecurityHelpers
import com.playground.dss.omp.graphql.subgraph.{Entitlement, Product, Types}
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

  def updateProductStatus(id: ID, productStatusType: ProductStatusType): ZIO[Env, Throwable, scala.Option[Product]]

  def createEntitlement(entitlement: CreateEntitlementInput): ZIO[Env, Throwable, scala.Option[Types.Entitlement]]
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

  override def updateProductStatus(
                                    id: ID,
                                    productStatusType: ProductStatusType
                                  ): ZIO[Env, Throwable, scala.Option[Product]] = for {
    profile <- SecurityHelpers.getProfile
    existingProduct <- ProductServiceDataStore.fetchProduct(profile, id).someOrFail(Errors.DataAccessErrorMsg(
      Errors.ErrorCode.DATA_ACCESS_ERROR,
      s"Failed to update product status. Product: ${id.toString} not found in DB",
      Map("product id" -> id.toString, "new status" -> productStatusType.toString)
    ))

    entitlements <- ProductServiceDataStore.getProductEntitlements(profile, List((id.toString, existingProduct.version)))
    productType = Product.retrieveProductEntityType(existingProduct.legacy)

    newAttributes = if (Product.oneTimePurchaseTypes.contains(productType))
      Map(
        "status" -> productStatusType.toString,
        "eventDate" -> existingProduct.eventDate.getOrElse(""),
        "catalogDate" -> existingProduct.catalogDate.getOrElse("")
      )
    else Map(
      "status" -> productStatusType.toString
    )
    _ <- createUpdateProduct(
      id,
      existingProduct.name,
      existingProduct.description,
      productType,
      newAttributes,
      entitlements.map(_._2.name).toList)
    updatedProduct <- ProductServiceDataStore.fetchProduct(profile, id)
  } yield updatedProduct.map(p => Product.fromTableWithAttributes(p))

  override def createEntitlement(entitlement: CreateEntitlementInput)
  : ZIO[Env, Throwable, scala.Option[Types.Entitlement]] = for {
    profile <- SecurityHelpers.getProfile
    user <- SecurityHelpers.getUser
    entitlementToCreate = TableHelpers.makeEntitlement(entitlement.name, user, profile)
    _ <- ProductServiceWriteDataStore.createEntitlement(entitlementToCreate)
    entitlement <- ProductServiceDataStore.fetchEntitlementById(entitlementToCreate.profile, entitlementToCreate.id)

  } yield entitlement.map(Entitlement.fromTable)
}
object ProductService {
  def createBaseProduct(product: CreateOrEditBaseProductInput): ZIO[Env, Throwable, scala.Option[Product]] =
    ZIO.serviceWithZIO[ProductService](_.createBaseProduct(product))

  def createOneTimePurchaseProduct(product: CreateOrEditOneTimePurchaseProductInput)
  : ZIO[Env, Throwable, scala.Option[Product]] =
    ZIO.serviceWithZIO[ProductService](_.createOneTimePurchaseProduct(product))

  def updateBaseProduct(id: ID, product: CreateOrEditBaseProductInput): ZIO[Env, Throwable, scala.Option[Product]] =
    ZIO.serviceWithZIO[ProductService](_.updateBaseProduct(id, product))

  def updateOneTimePurchaseProduct(
                                    id: ID,
                                    product: CreateOrEditOneTimePurchaseProductInput
                                  ): ZIO[Env, Throwable, scala.Option[Product]] =
    ZIO.serviceWithZIO[ProductService](_.updateOneTimePurchaseProduct(id, product))

  def updateProductStatus(id: ID, productStatusType: ProductStatusType): ZIO[Env, Throwable, scala.Option[Product]] =
    ZIO.serviceWithZIO[ProductService](_.updateProductStatus(id, productStatusType))

  def createEntitlement(entitlement: CreateEntitlementInput): ZIO[Env, Throwable, scala.Option[Entitlement]] =
    ZIO.serviceWithZIO[ProductService](_.createEntitlement(entitlement))

  val layer: ULayer[ProductServiceLive] = ZLayer.succeed(new ProductServiceLive())

}
