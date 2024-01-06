package dev.zio.caliban.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import java.time.OffsetDateTime
import java.time.OffsetDateTime

final case class Entitlement(
    id: String,
    name: String,
    description: Option[String],
    attributes: Map[String, Set[String]],
    legacy: Map[String, String],
    author: String,
    datetime: OffsetDateTime,
    profile: String,
    version: Option[Long] = None
) {
  import Entitlement._
  def legacyEntitlementCategory: Option[String] = legacy.get(legacyCategory)
}

object Entitlement {
  implicit lazy val decoder: Decoder[Entitlement] = deriveDecoder[Entitlement]
  implicit lazy val encoder: Encoder.AsObject[Entitlement] = deriveEncoder[Entitlement]

  val legacyEntitlementIdKey = "disney_legacy_id"
  val legacyCategory = "disney_legacy_category"

  val legacyHuluOriginKey = "hulu_origin_type"
  val legacyHuluEntitlementIdKey = "hulu_entitlement_id"
  val legacyHuluGroupsKey = "groups"
  val legacyHuluFeatureDefinitionsKey = "hulu_feature_def"

  val legacyHuluPackage = "package"
  val legacyHuluFeature = "feature"
  val legacyHuluOneTimePurchaseComponent = "otp"

  val legacyPackageIdKey = "hulu_package_id"
  val legacyFeatureIdKey = "hulu_feature_id"
  val legacyComponentIdKey = "hulu_component_id"

}
