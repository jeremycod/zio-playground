package com.playground.dss.omp.graphql.subgraph

import com.playground.dss.omp.common.EntityStatuses
import com.playground.dss.omp.graphql.table.public.{ProductWithAttributes, Product => ProductTbl}
//import com.playground.dss.omp.genie.data.v3

import java.util.Locale
import com.playground.dss.omp.graphql.resolver.GetProduct

import java.util.UUID
import com.playground.dss.omp.graphql.subgraph.Types.ProductEntityType._
import com.playground.dss.omp.graphql.utils.LegacyHelper._

object Product {

  private val defaultStartDate = "1970-01-01T00:00:00Z"

  val allProductEntityTypes: Set[Types.ProductEntityType] =
    Set(LIVE_TV, PAY_PER_VIEW, PREMIUM_NETWORKS, BASE, TEST, PREMIER_ACCESS, SEASONAL, FEATURE, ONE_TIME_PURCHASE)
  def findProductEntityTypeByName(name: String): Types.ProductEntityType =
    allProductEntityTypes.find(_.toString.toLowerCase(Locale.US) == name.toLowerCase(Locale.US)).getOrElse(TEST)
  // TODO: Would be better to fallback to UNKNOWN

  def fromTableWithAttributes(r: ProductWithAttributes): Types.Product = {
    val legacyMap = fromLegacyOptionString(r.legacy)
    val productId = UUID.fromString(r.id)
    val productType = findProductEntityTypeByName(legacyMap.getOrElse("disney_product_type", ""))
    if (productType == ONE_TIME_PURCHASE) {
      Types.OneTimePurchaseProduct(
        id = productId,
        legacyId = legacyMap.get("disney_legacy_id"),
        name = r.name,
        description = r.description,
        `type` = productType,
        createdBy = r.author,
        createdDate = r.datetime,
        updatedBy = Some(r.author),
        updatedDate = Some(r.datetime),
        entitlements = GetProduct.getProductEntitlements(productId, r.profile, r.version),
        tierDefinitions = List.empty,
        status = r.status.getOrElse(EntityStatuses.Live.stringRep),
        startDate = r.eventDate.getOrElse(defaultStartDate),
        endDate = r.catalogDate
      )
    } else
      Types.BaseProduct(
        id = productId,
        legacyId = legacyMap.get("disney_legacy_id"),
        name = r.name,
        description = r.description,
        `type` = productType,
        createdBy = r.author,
        createdDate = r.datetime,
        updatedBy = Some(r.author),
        updatedDate = Some(r.datetime),
        entitlements = GetProduct.getProductEntitlements(productId, r.profile, r.version),
        tierDefinitions = List.empty,
        status = r.status.getOrElse(EntityStatuses.Live.stringRep)
      )
  }
}
