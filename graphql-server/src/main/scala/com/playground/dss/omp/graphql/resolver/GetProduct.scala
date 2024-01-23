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
      ZIO.foreachPar(requests) { req =>
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
          finalProducts <- ZIO.foreachPar(filteredProducts)(p => ZIO.succeed(Product.fromTableWithAttributes(p)))
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
    DataSource.fromFunctionBatchedZIO("GetProductEntitlements") { requests =>
      ZIO.foreachPar(requests) { req =>
        ProductServiceDataStore.getProductEntitlements(req.productId, req.profile, req.productVersion)
          .tapError(e => ZIO.logError(s"Get product entitlements failed with error ${e.toString}"))
          .map(_.map { entitlement => Entitlement.fromTable(entitlement) }.toList)
      }
    }
  }

}