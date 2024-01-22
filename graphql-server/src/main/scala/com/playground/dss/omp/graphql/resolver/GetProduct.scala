package com.playground.dss.omp.graphql.resolver

import com.playground.dss.omp.graphql.Queries.Env
import com.playground.dss.omp.graphql.persist.ProductServiceDataStore
import com.playground.dss.omp.graphql.security.SecurityHelpers
import com.playground.dss.omp.graphql.subgraph.{Entitlement, Product}
import com.playground.dss.omp.graphql.subgraph.Types
import com.playground.dss.omp.graphql.subgraph.Types.{ID, QueryProductArgs, QueryProductsArgs}
import com.playground.dss.omp.graphql.utils.LegacyHelper
import zio._
import zio.query.ZQuery

import java.util.UUID

object GetProduct {

  def getProduct(queryProductArg: QueryProductArgs): ZQuery[Env, Throwable, Option[Types.Product]] = ZQuery.fromZIO {
    for {
      profile <- SecurityHelpers.getProfile
      product <- ProductServiceDataStore.fetchProduct(profile, queryProductArg.id)
    } yield product.map(p => Product.fromTableWithAttributes(p))
  }

  def getAllProducts(productTypes: List[String], productIds: List[ID]): ZQuery[Env, Throwable, Seq[Types.Product]] = {
    ZQuery.fromZIO {
      for {
        profile <- SecurityHelpers.getProfile
        products <- ProductServiceDataStore.fetchProducts(profile, productIds)
          .tapError(e =>
            ZIO.logError(s"Get all products failed with error ${e.toString}"))

        // TODO: Ideally, we want this filtering to happen in the query itself
        // but it would require to change legacy to jsonb or lifting product type
        filteredProducts = products.filter(p =>
          if (productTypes.isEmpty) true
          else productTypes.contains(LegacyHelper.findInLegacyString(
            p.legacy,
            "disney_product_type").getOrElse("")))
        finalProducts <- ZIO.foreachPar(filteredProducts)(p => ZIO.succeed(Product.fromTableWithAttributes(p)))
      } yield finalProducts
    }
  }

  def getProductEntitlements(
      productId: UUID,
      profile: String,
      productVersion: Long
  ): ZQuery[ProductServiceDataStore, Throwable, List[Types.Entitlement]] = {
    ZQuery.fromZIO(ProductServiceDataStore.getProductEntitlements(productId, profile, productVersion)
      .tapError(e => ZIO.logError(s"Get product entitlements failed with error ${e.toString}")))
      .map(_.map { entitlement => Entitlement.fromTable(entitlement) }.toList)
  }

}
