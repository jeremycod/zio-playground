package dev.zio.caliban.model

import java.time.OffsetDateTime

final case class Sku(
    id: String,
    name: String,
    description: Option[String],
    platform: Option[String],
    countries: Set[String],
    products: Set[String], // UUIDs
    offers: Set[String], // UUIDs
    campaigns: Set[String], // UUIDs
    author: String,
    datetime: OffsetDateTime,
    profile: String,
    version: Option[Long] = None,
    deleted: Boolean = false,
    legacy: Map[String, String]
) {

  def legacyOrigin: Option[String] = legacy.get("legacyOriginKey")
}

object Sku {

  val ORIGIN_D2C = "D2C"
  val ORIGIN_3PP = "3PP"
  val ORIGIN_IAP = "IAP"

  val legacyOriginKey = "legacy_billing_origin"
  val legacyProductId = "legacyProductId"
  val legacySkuId = "legacySkuId"
//  val isIAPSku = "isIAPSku"
//  val is3PPSku = "is3PPSku"

}
