package dev.zio.caliban.model

import java.time.OffsetDateTime

final case class Discount(
    id: String,
    currency: String,
    amounts: Map[String, Int], // productId -> amount of discount
    attributes: Map[String, Set[String]],
    messages: Map[String, String],
    legacy: Map[String, String],
    author: String,
    datetime: OffsetDateTime,
    profile: String,
    version: Option[Long] = None
) extends AccessHelper {

  def startDate: Option[OffsetDateTime] = one("startDate").map(strToOffsetDateTime)
  def endDate: Option[OffsetDateTime] = one("endDate").map(strToOffsetDateTime)
  def discountType: Option[String] = one("type")
}

object Discount {
  val legacyIdKey = "dss_legacy_offer_id"
  val legacyDiscountIdKey = "hulu_discount_id"
  val discountTypeKey = "type"

  val freeTrialDiscountType = "FREE_TRIAL"
  val percentDiscountType = "PERCENT"
  val amountDiscountType = "AMOUNT"
  val prePaidDiscountType = "PREPAID"
  val discountPhaseTypes: Set[String] =
    Set(freeTrialDiscountType, percentDiscountType, amountDiscountType, prePaidDiscountType)
  val splitBillingType = "SPLIT_BILLING"
  val offerDiscountType = "OFFER_DISCOUNT"
}
