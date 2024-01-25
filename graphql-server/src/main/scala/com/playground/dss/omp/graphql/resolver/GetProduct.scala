package com.playground.dss.omp.graphql.resolver

import com.playground.dss.omp.graphql.Queries.Env
import com.playground.dss.omp.graphql.persist.ProductServiceDataStore
import com.playground.dss.omp.graphql.security.SecurityHelpers
import com.playground.dss.omp.graphql.subgraph.{Entitlement, Product}
import com.playground.dss.omp.graphql.subgraph.Types
import com.playground.dss.omp.graphql.subgraph.Types.ID
import com.playground.dss.omp.graphql.utils.LegacyHelper
import zio._
import zio.query.{DataSource, Request, ZQuery}

import java.util.UUID

object GetProduct {
  private val PRODUCT_ENTITLEMENTS_BATCH_MAX = 500
  final case class ProductsRequest(productTypes: List[String], productIds: List[ID])
    extends Request[Throwable, List[Types.Product]]

  def getProduct(id: ID): ZQuery[Env, Throwable, Option[Types.Product]] = ZQuery.fromZIO {
    for {
      profile <- SecurityHelpers.getProfile
      product <- ProductServiceDataStore.fetchProduct(profile, id)
    } yield product.map(p => Product.fromTableWithAttributes(p))
  }

  def getAllProducts(productTypes: List[String], productIds: List[ID]): ZQuery[Env, Throwable, Seq[Types.Product]] =
    ZQuery.fromRequest(ProductsRequest(productTypes, productIds))(productsDataSource)

  private val productsDataSource: DataSource[Env, ProductsRequest] = {
    DataSource.fromFunctionBatchedZIO("GetProduct") { requests =>
    {
      ZIO.foreach(requests) { req =>
        for {
          profile <- SecurityHelpers.getProfile
          products <- ProductServiceDataStore.fetchProducts(profile, req.productIds)
            .tapError(e =>
              ZIO.logError(s"Get all products failed with error ${e.toString}"))

          // TODO: Ideally, we want this filtering to happen in the query itself
          // but it would require to change legacy to jsonb or lifting product type
          filteredProducts = products.filter(p =>
            if (req.productTypes.isEmpty) true
            else req.productTypes.contains(LegacyHelper.findInLegacyString(
              p.legacy,
              "disney_product_type").getOrElse("")))
          finalProducts <- ZIO.succeed(filteredProducts.map(p => Product.fromTableWithAttributes(p)))
        } yield finalProducts.toList
      }
    }

    }
  }

  final private case class ProductEntitlementsRequest(productId: UUID, profile: String, productVersion: Long)
    extends Request[Throwable, List[Types.Entitlement]]

  def getProductEntitlements(
                              productId: UUID,
                              profile: String,
                              productVersion: Long
                            ): ZQuery[Env, Throwable, List[Types.Entitlement]] =
    ZQuery.fromRequest(ProductEntitlementsRequest(productId, profile, productVersion))(productEntitlementsDataSource)

  private val productEntitlementsDataSource: DataSource[Env, ProductEntitlementsRequest] = {
    DataSource.fromFunctionBatchedZIO[Env, Throwable, ProductEntitlementsRequest, List[Types.Entitlement]](
      "GetProductEntitlements") { chunk =>
      NonEmptyChunk.fromChunk(chunk).fold {
        val empty: ZIO[Env, Throwable, Chunk[List[Types.Entitlement]]] =
          ZIO.succeed(Chunk.empty)
        empty
      } { nonEmpty =>
        val profile = nonEmpty.headOption.map(_.profile).getOrElse("")
        for {
          productEntitlements <-
            ProductServiceDataStore.getProductEntitlements(
              profile,
              nonEmpty.map(ne => (ne.productId.toString, ne.productVersion)).toList)
              .tapError(e => ZIO.logError(s"Get product entitlements failed with error ${e.toString}"))
              .map(_.map {
                prodEnt => (prodEnt._1, Entitlement.fromTable(prodEnt._2))
              }.groupMap(_._1)(_._2))
        } yield chunk.map(request =>
          productEntitlements.get(request.productId.toString)).collect {
          case Some(value) => value.toList
        }
      }
    }.batchN(PRODUCT_ENTITLEMENTS_BATCH_MAX)
  }

}