package com.playground.dss.omp.graphql.resolver

import com.playground.dss.omp.graphql.Queries.Env
import com.playground.dss.omp.graphql.persist.ProductServiceDataStore
import com.playground.dss.omp.graphql.security.SecurityHelpers
import com.playground.dss.omp.graphql.subgraph.{Entitlement, Product}
import com.playground.dss.omp.graphql.subgraph.Types
import com.playground.dss.omp.graphql.subgraph.Types.{QueryProductArgs, QueryProductsArgs}
import com.playground.dss.omp.graphql.utils.LegacyHelper
import com.playground.dss.omp.graphql.subgraph.Types.{Product, QueryProductArgs}
//import com.playground.dss.omp.genie.data.v3.model
import zio._
import zio.query.ZQuery

import java.util.UUID

object GetProduct {

  def getProduct(queryProductArg: QueryProductArgs): ZQuery[Env, Throwable, Option[Product]] = ZQuery.fromZIO {
    for {
      profile <- SecurityHelpers.getProfile
      product <- ProductServiceDataStore.fetchProduct(profile, queryProductArg)
    } yield product.map(p => Product.fromTable(p))
  }

  def getAllProducts(queryProductArgs: QueryProductsArgs): ZQuery[Env, Throwable, Seq[Types.Product]] = {
    ZQuery.fromZIO {
      for {
        profile <- SecurityHelpers.getProfile
        products <- ProductServiceDataStore.fetchProducts(profile, queryProductArgs)
                      .tapError(e =>
                        ZIO.logError(s"Get all products failed with error ${e.toString}"))
        productTypes = queryProductArgs.types.getOrElse(Seq.empty)
        // TODO: Ideally, we want this filtering to happen in the query itself
        // but it would require to change legacy to jsonb or lifting product type
        filteredProducts = products.filter(p =>
                             if (productTypes.isEmpty) true
                             else productTypes.contains(LegacyHelper.findInLegacyString(
                               p.legacy,
                               "disney_product_type").getOrElse("")))
      } yield filteredProducts.map(product => Product.fromTable(product))
    }
  }

  def getProductEntitlements(
      productId: UUID,
      profile: String
  ): ZQuery[ProductServiceDataStore, Throwable, List[Types.Entitlement]] = {
    ZQuery.fromZIO(ProductServiceDataStore.getProductEntitlements(productId, profile)
      .tapError(e => ZIO.logError(s"Get product entitlements failed with error ${e.toString}")))
      .map(_.map { entitlement => Entitlement.fromTable(entitlement) }.toList)
  }

}
