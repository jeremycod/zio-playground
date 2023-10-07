package dev.zio.caliban.model

import java.time.OffsetDateTime

object Product {
  val EVENT_DATE = "eventDate"
  val CATALOG_DATE = "catalogDate"
  val START_DATE = "startDate"
  val END_DATE = "endDate"

  // Product.legacy Map keys
  val namespacesKey = "namespaces"
  val disneyLegacyProductIdKey = "disney_legacy_id"
  val disneyProductTypeKey = "disney_product_type"

  val startDateKey = "start_date"
  val minAgeRequiredKey = "min_age_required"

  val huluLegacyComponentIdKey = "hulu_component_id"
  val huluLegacyComponentIdentifierKey = "hulu_component_identifier"
  val huluLegacyCategoryKey = "hulu_category"
  val huluLegacyOtpIdKey = "hulu_otp_product_id"
}

final case class Product(
    id: String,
    name: String,
    description: Option[String],
    entitlements: Set[String], // entitlement IDs (which should be equal to uuid(entitlement.name))
    dependentProducts: Set[String],
    attributes: Map[String, Set[String]],
    messages: Map[String, String],
    metadata: Map[String, String],
    legacy: Map[String, String],
    author: String,
    datetime: OffsetDateTime,
    profile: String,
    version: Option[Long] = None
) extends AccessHelper {
  import Product._

  def eventDate: Option[OffsetDateTime] = one(EVENT_DATE).map(strToOffsetDateTime)
  def catalogDate: Option[OffsetDateTime] = one(CATALOG_DATE).map(strToOffsetDateTime)
  def startDate: Option[OffsetDateTime] = one(START_DATE).map(strToOffsetDateTime)
  def endDate: Option[OffsetDateTime] = one(END_DATE).map(strToOffsetDateTime)

  // The type is both translated and saved in the same place
  def legacyProductType: Option[String] = legacy.get(disneyProductTypeKey)
  def disneyProductLegacyId: Option[String] = legacy.get(disneyLegacyProductIdKey)
  def huluComponentLegacyId: Option[String] = legacy.get(huluLegacyComponentIdKey)
  def huluLegacyOtpLegacyId: Option[String] = legacy.get(huluLegacyOtpIdKey)
}
