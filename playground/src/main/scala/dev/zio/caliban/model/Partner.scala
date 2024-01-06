package dev.zio.caliban.model

import java.time.OffsetDateTime

object Partner {
  val TypeKey = "partnerType"
  val ReferenceLinkKey = "referenceLink"
  val StatusKey = "status"
  val MinimumGuaranteesKey = "minimumGuarantees"
  val MinimumGuaranteesAreUsedKey = "minimumGuaranteesAreUsed"

  val LegacyCreator = "legacyCreator"
  val LegacyCreatedDate = "legacyCreatedDate"
  val LegacyId = "legacyId"

  // Probably should have an 'enum' type for all of these but need the full set first
  val PartnerTypePartnerBilling = "PartnerBilling"
}

final case class Partner(
    id: String,
    name: String,
    description: Option[String],
    attributes: Map[String, Set[String]],
    legacy: Map[String, String],
    author: String,
    datetime: OffsetDateTime,
    profile: String,
    version: Option[Long] = None
) extends AccessHelper {
  import Partner._

  def types: Set[String] = many(TypeKey)
  def links: Set[String] = many(ReferenceLinkKey)
  def minimumGuarantees: Set[String] = many(MinimumGuaranteesKey)
  def status: Option[String] = one(StatusKey)
  def minimumGuaranteesAreUsed: Boolean = one(MinimumGuaranteesAreUsedKey).exists(strToBoolean)

}
