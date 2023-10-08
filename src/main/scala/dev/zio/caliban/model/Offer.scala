package dev.zio.caliban.model

import dev.zio.caliban.model.Offer.{Cohort, TargetOfferId}

import java.time.OffsetDateTime

object Offer {
  // offer attributes keys
  val OfferTypeKey = "offerType"
  val StartDateKey = "startDate"
  val EndDateKey = "endDate"
  val EligibleCohortsKey = "eligibleCohorts"
  val IneligibleCohortsKey = "ineligibleCohorts"
  val PackageKey = "Package"

  val offerTypeD2C = "DIRECT"
  val offerType3PP = "PARTNER"
  val offerTypeIAP = "IAP"

  val CurrencyCodeKey = "currencyCode"
  val NamespacesKey = "namespaces"
  val ReferenceLinkKey = "referenceLinks"
  val ReferenceOfferIdsKey = "reference_offer_ids"
  val OfferSaleTypeKey = "offer_sale_type"
  val PromotionType3PPKey = "promotion_type_3pp"
  val PartnerPaidAmountKey = "partner_paid_amount"
  val PartnerPaidUponKey = "partner_paid_upon"
  val Legacy3PPPartnerKey = "legacy_3pp_partner_id"
  val CountriesKey = "countries"

  // Offer.legacy map keys
  val DisneyOfferPeriodKey = "disney_offer_period"
  val NamespaceKey = "namespace"
  val StatusKey = "offer_status"
  // store original legacy product ID, in cases when there's just 1, so useful way to get back to original bundle IDs
  val LegacyProductIdKey = "disney_original_product_id"
  val ChannelTypeLegacyMapKey = "channel_type"
  val LegacySkuKeyKey = "legacy_sku_key"
  val DisneyLegacyOfferIdKey = "disney_offer_id"
  val HuluLegacyProgramIdKey = "hulu_program_id"
  val HuluLegacyBundleIdKey = "hulu_bundle_id"
  val HuluLegacyPlanIdKey = "hulu_plan_id"
  val HuluLegacyComponentIdKey = "hulu_component_id"
  val RevenueShareKey = "revenueShare"
  val PaymentTypeKey = "paymentType"
  // Using this legacy data for 3pp sku metadata report
  // since we don't have reference with sub-partners yet
  val PlatformValue = "platformValue"
  val PlatformLabel = "platformLabel"
  val LegacyTransitionsKey = "legacy_transition_key"

  object Offer3PP {
    val LegacyPartnerIdKey = "legacy_partner_id"
  }
  // This is here for IAP only currently. 3PP external mapping ID's
  val ExternalMappingIdKey = "external_mapping_id"

  // Message keys
  val DISPLAY_NAME = "displayName"

  // Epoch 0
  // OffsetDateTime.parse("1970-01-01T00:00:00Z") -> val res0: java.time.OffsetDateTime = 1970-01-01T00:00Z
  // This is hear because it is used in two places.
  //   In the hard-coded offers in the Disney Translation this is added in as they are D2C and SHOULD have a start date
  //   In the conversion from DB to GraphQL model, as the contract states there must be a start date, but there is not
  //   one populated by the transform so far.  The intent is for discussion to happen around if we need it or not as
  //   having 'data' not stored in the DB makes our source of truth diverge and that will create problems, this is
  //   purely to un-block and allow the frontend to integrate as-is for now
  // I do think this should be defaulted on all offers we create if we are not porting over start dates or they do not
  // have them but would like a conversation before I make that decision.
  val defaultStartDate = "1970-01-01T00:00:00Z"

  type TargetOfferId = String
  type Cohort = String
}

final case class TransitionEligibility(
    targetOfferId: String,
    transitionReason: String,
    transitionDate: String,
    transitionDateOperator: String,
    eligibleCohorts: Set[Cohort],
    ineligibleCohorts: Set[Cohort]
)

final case class OfferTransition(
    eligibleCohorts: Set[Cohort],
    ineligibleCohorts: Set[Cohort],
    transitionReason: String
)

final case class CountryCohortEligibility(countries: Set[String], eligibleCohorts: Set[String])

final case class OfferEligibility(eligibility: Set[CountryCohortEligibility])

final case class Offer(
    id: String,
    name: String,
    description: Option[String],
    discountId: Option[String],
   // products: Map[String, OfferProduct], // Product ID -> OfferProduct
    attributes: Map[String, Set[String]],
    //messages: Map[String, String],
    //metadata: Map[String, String],
    //transitions: Map[TargetOfferId, OfferTransition], // Target Offer Id -> OfferTransition
    legacy: Map[String, String],
    author: String,
    datetime: OffsetDateTime,
    profile: String,
    version: Option[Long] = None,
    // not saved to the db, is used for populating campaign offer cohorts on campaign creation
   // eligibility: Option[OfferEligibility]
)

  /*extends AccessHelper {
  import Offer._
  val products: Map[String, OfferProduct] = Map.empty
  val legacy: Map[String, String] = Map.empty
  val attributes: Map[String, Set[String]] = Map.empty
  def countriesFromSkus: Set[String] = products.values.map(_.skus.map {
    case (_, attrs) => attrs.getOrElse("countries", Set.empty[String])
  }.foldLeft(Set.empty[String])((acc, b) =>
    acc.union(b))) // same product, many SKUs - union countries (one of them will match)
    .reduceOption((a, b) => a.intersect(b)) // different products - intersect countries (cannot be half offer)
    .getOrElse(Set.empty[String])

  def legacyOfferId: Option[String] = legacy.get(DisneyLegacyOfferIdKey)
  def startDate: Option[OffsetDateTime] = one(StartDateKey).map(strToOffsetDateTime)
  def endDate: Option[OffsetDateTime] = one(EndDateKey).map(strToOffsetDateTime)
  def eligibleCohorts: Set[String] = many(EligibleCohortsKey)
  def ineligibleCohorts: Set[String] = many(IneligibleCohortsKey)
  def offerType: Option[String] = one(OfferTypeKey)
  def status: Option[String] = one(StatusKey)
  def referenceOfferIds: Set[String] = many(ReferenceOfferIdsKey)
  def externalMappingId: Option[String] = one(ExternalMappingIdKey)

  def threePPSaleType: Option[String] = one(OfferSaleTypeKey)
  def threePPPromotionType: Option[String] = one(PromotionType3PPKey)
  def threePPPaidAmount: Option[String] = one(PartnerPaidAmountKey)
  def threePPPaidUpon: Option[String] = one(PartnerPaidUponKey)
  def countries: Set[String] = many(CountriesKey)
}*/
