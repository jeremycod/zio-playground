package com.playground.dss.omp.graphql.table

import cats.implicits.catsSyntaxOptionId
import com.playground.dss.omp.graphql.subgraph.Types.ProductEntityType
import zio.json._
import com.playground.dss.omp.graphql.table.public.{Entitlement => EntitlementTbl, Product => ProductTbl}

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.UUID

object TableHelpers {

  def makeProduct(
      productId: UUID,
      productName: String,
      productDescription: Option[String],
      productEntityType: ProductEntityType,
      user: String,
      profile: String
  ): ProductTbl = {
    val legacy = Map(
      "disney_legacy_id" -> productId.toString,
      "disney_product_type" -> productEntityType.toString
    ).toJson
    ProductTbl(
      id = productId.toString,
      name = productName,
      description = productDescription,
      legacy = legacy.some,
      author = user,
      datetime = OffsetDateTime.now(ZoneOffset.UTC).toString,
      profile = profile,
      version = 0L,
      deleted = false
    )
  }


  def makeEntitlement(ent: String, authorName: String, profile: String): EntitlementTbl = {
    val id = UUID.randomUUID().toString
    val legacy = Map("disney_legacy_id" -> id).toJson
    EntitlementTbl(
      id = id,
      name = ent,
      description = None,
      legacy = legacy.some,
      author = authorName,
      datetime = OffsetDateTime.now(ZoneOffset.UTC).toString,
      profile = profile,
      version = 0L,
      deleted = false
    )
  }

}
