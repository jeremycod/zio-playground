package dev.zio.caliban.model

import java.time.OffsetDateTime

final case class Phase(
    id: String,
    durationLength: Option[Int],
    durationUnit: Option[String],
    repeatCount: Option[Int],
    endDate: Option[OffsetDateTime],
    paymentType: String,
    discountId: Option[String],
    phaseOrdering: Int, // To determine in what order the phases are
    legacy: Map[String, String]
)

object Phase {

  val legacyPropsSkuProductIdKey = "disney_legacy_props_sku_product_id"
  val legacyHuluPolicyIdKey = "hulu_policy_id"

}
