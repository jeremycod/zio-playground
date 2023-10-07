package dev.zio.caliban.model

import java.time.OffsetDateTime

object Campaign {
  val START_DATE = "startDate"
  val END_DATE = "endDate"
  val COUNTRIES = "countries"
  val PLATFORMS = "platforms"
  val BRAND = "brand"
  val BRANDS = "brands"
  val BILLING_TYPE = "billingType"
  val REQUIRES_REDEMPTION_CODE = "requires_redemption_code"
  val REQUIRES_PAYMENT_METHOD = "requires_payment_instrument"
  val SEND_EXPIRATION_NOTICES = "sendExpirationNotices"
  val HAS_SIGNUP_FLOW = "hasSignupFlow"
  val HAS_ACCOUNT_MANAGEMENT = "hasAccountManagement"
  val HAS_PRE_CANCEL = "hasPreCancel"
  val HAS_CONCESSIONS = "hasConcessions"
  val CUSTOM_URL = "customUrl"
  val STATUS = "status"
  val REFERENCE_LINKS = "referenceLinks"
  val REPORT_AS_PAID_DURING_PROMO = "report_as_paid_during_promo"
  val EDC_ID = "edc_id"
  val ELIGIBLE_COHORTS = "eligible_cohorts"

  object CampaignOffer {
    val SetNumberKey = "setNumber"
  }

  val d2cBillingType = "BILLING_D2C"
  val iapBillingType = "BILLING_IAP"
  val threePPBillingType = "BILLING_3PP"

  object LegacyVoucherKeys {
    val countries = "countries"
    val platforms = "platforms"
    val isActive = "isActive"
    val voucherId = "disney_voucher_id"
  }

  def campaignOfferEligibleCohortsKey(offerId: String): String = s"${Offer.EligibleCohortsKey}_$offerId"
  def campaignOfferIneligibleCohortsKey(offerId: String): String = s"${Offer.IneligibleCohortsKey}_$offerId"

  object CampaignBillingTypes {
    val BILLING_D2C = "BILLING_D2C"
    val BILLING_IAP = "BILLING_IAP"
    val BILLING_3PP = "BILLING_3PP"
  }

  val PURCHASE_FLOWS = "purchaseFlows"
  object PURCHASE_FLOW_TYPES {
    val SIGNUP = "signup"
    val SWITCH = "switch"
    val CANCEL = "cancel"
    val CONCESSION = "concession"
    val LANDING = "landing"
    val REDEMPTION = "redemption"
    val UPSELL = "upsell"
  }

  val LANDING_FLOW_URL = "landingURL"

  val CODE_TYPE = "codeType"
  object CODE_TYPES {
    val STANDARD = "STANDARD"
    val VIP = "VIP"
  }

  val PARTNER = "partner"
  val PARTNER_NAME = "partner_name"
  val PROVIDERS = "providers"

  val disneyLegacyCampaignIdKey = "disney_legacy_id"
  val huluLegacyProgramIdKey = "hulu_program_id"
  val disneyLegacyPromotionIdsKey = "disney_promotion_ids"
  val defaultStartDate = "1970-01-01T00:00:00Z"

  object PLATFORM_VALUES {
    val LG: String = "LG"
    val VIZIO: String = "VIZIO"
    val SAMSUNG: String = "SAMSUNG_VERIFIABLE"
    val PS4: String = "SONY_VERIFIABLE"
    val XBOX: String = "XBOX"
    val WEB: String = "WEB"
    val ROKU: String = "ROKU_VERIFIABLE"
    val IOS_AND_APPLE_TV: String = "APPLE_VERIFIABLE"
    val ANDROID: String = "ANDROID_TV"
  }

}

final case class Campaign(
    id: String,
    name: String,
    description: Option[String],
    partnerId: Option[String],
    attributes: Map[String, Set[String]],
    offers: Map[
      String,
      Map[String, Map[String, Set[String]]]
    ], //  map of offer id -> vouchers containing attributes of type String -> Set[String]
    campaignOfferEligibility: Map[String, Set[String]], // offerId -> set of eligible cohorts
    legacy: Map[String, Set[String]],
    author: String,
    datetime: OffsetDateTime,
    profile: String,
    eligibility: Set[CampaignEligibility],
    version: Option[Long] = None
) extends AccessHelper {
  import Campaign._

  def startDate: Option[OffsetDateTime] = one(START_DATE).map(strToOffsetDateTime)
  def endDate: Option[OffsetDateTime] = one(END_DATE).map(strToOffsetDateTime)

  def countries: Set[String] = many(COUNTRIES)
  def platforms: Set[String] = many(PLATFORMS)
  def brands: Set[String] = many(BRANDS)
  def billingType: Option[String] = one(BILLING_TYPE)

  // BRAND singular - currently we're populating multiple BRANDS into the attributes map from the promotions object in
  // legacy genie, this is not a model that is supported in genie + so we should re-think that logic.
  // However right now we need to have a single BRAND for the campaign and from the DisneyTranslation this will always
  // be set to the namespace of the promotion (the primary namespace) in order to facilitate DATO's and SIT (whatever SIT stands for)
  def brand: Option[String] = one(BRAND)

  def purchaseFlows: Set[String] = many(PURCHASE_FLOWS)
  def landingURL: Option[String] = one(LANDING_FLOW_URL)
  def codeType: Option[String] = one(CODE_TYPE)
  def status: Option[String] = one(STATUS)

  def referenceLinks: Set[String] = many(REFERENCE_LINKS)

  private def toBooleanWithDefaultFalse(key: String): Boolean = {
    one(key).map(strToBoolean).getOrElse(false)
  }

  def requiresRedemptionCode: Boolean = toBooleanWithDefaultFalse(REQUIRES_REDEMPTION_CODE)
  def requiresPaymentMethod: Boolean = toBooleanWithDefaultFalse(REQUIRES_PAYMENT_METHOD)
  def hasSendExpirationNotices: Boolean = toBooleanWithDefaultFalse(SEND_EXPIRATION_NOTICES)
  def hasSignupFlow: Boolean = toBooleanWithDefaultFalse(HAS_SIGNUP_FLOW)
  def hasAccountManagement: Boolean = toBooleanWithDefaultFalse(HAS_ACCOUNT_MANAGEMENT)
  def hasPreCancel: Boolean = toBooleanWithDefaultFalse(HAS_PRE_CANCEL)
  def hasConcessions: Boolean = toBooleanWithDefaultFalse(HAS_CONCESSIONS)

  def customUrl: Option[String] = one(CUSTOM_URL)

  def partner: Option[String] = one(PARTNER)
  def partnerName: Option[String] = one(PARTNER_NAME)
  def providers: Set[String] = many(PROVIDERS)

  def edcIds: Option[String] = one(EDC_ID)

  def eligibleCohorts: Set[String] = many(ELIGIBLE_COHORTS)

}
