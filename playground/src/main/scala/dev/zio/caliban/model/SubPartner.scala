package dev.zio.caliban.model

import java.time.OffsetDateTime

object SubPartner {
  val TypesKey = "subPartnerType"
  val ReferenceLinkKey = "referenceLink"
  val CountryKey = "country"
  val MinimumGuaranteesKey = "minimumGuarantees"

  val LegacyId = "legacyId"
  val LegacyPartnerId = "legacyPartnerId"
}

final case class SubPartner(
    id: String,
    name: String,
    displayName: String,
    partnerId: String,
    attributes: Map[String, Set[String]],
    legacy: Map[String, String],
    author: String,
    datetime: OffsetDateTime,
    profile: String,
    version: Option[Long] = None
) extends AccessHelper {
  import SubPartner._

  def types: Set[String] = many(TypesKey)
  def referenceLinks: Set[String] = many(ReferenceLinkKey)
  def minimumGuarantees: Set[String] = many(MinimumGuaranteesKey)
  def country: Option[String] = one(CountryKey)

}
