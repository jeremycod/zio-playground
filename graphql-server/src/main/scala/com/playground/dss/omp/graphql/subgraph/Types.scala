package com.playground.dss.omp.graphql.subgraph

import caliban.schema.Annotations.{GQLDescription, GQLInterface}
import com.playground.dss.omp.graphql.Queries.Env
import zio.query.ZQuery

object Types {

  type ID = java.util.UUID

  final case class QueryOfferArgs(id: ID)

  final case class QueryOffersArgs(offerIds: scala.Option[List[ID]], offerFilter: scala.Option[OffersFilter])

  final case class QueryReferenceOffersArgs(currency: String, billingFrequency: String, productIds: List[String])

  final case class QueryProductArgs(id: ID)

  final case class QueryProductsArgs(types: scala.Option[List[String]], productIds: scala.Option[List[ID]])

  final case class QueryEntitlementsFromEDCArgs(`type`: scala.Option[EntitlementType], tags: scala.Option[List[Tag]])

  final case class QueryEntitlementFromEDCArgs(id: ID)

  final case class QueryCampaignArgs(id: ID)

  final case class QueryPartnerArgs(id: ID)

  final case class QuerySubpartnerArgs(id: ID)

  final case class QueryCohortResolversArgs(view: scala.Option[CohortView])

  final case class QueryCohortsArgs(view: scala.Option[CohortView])

  final case class QueryCohortArgs(id: ID)

  final case class QueryPackageArgs(id: String)

  final case class QueryPriceChangeEventArgs(id: ID)

  final case class QueryEventCohortArgs(priceChangeEventId: ID, cohort: ID)

  final case class QueryEventCohortsArgs(priceChangeEventId: ID)

  final case class QueryVanellopeGatewayPayloadArgs(payload: PriceChangeEventStatusInput)

  final case class QueryConfigServicePayloadArgs(priceChangeEventId: scala.Option[ID])

  final case class QuerySkuArgs(id: ID)

  final case class QueryRedemptionCodesArgs(campaignId: ID, taskId: ID, brand: Brand)

  final case class QueryRedemptionCodeTasksArgs(campaignId: ID)

  final case class QueryRedemptionCodeInfoArgs(code: String)

  final case class QueryTierDefinitionArgs(id: ID)

  final case class MutationCreateOfferD2CArgs(offer: CreateOrEditOfferD2CInput)

  final case class MutationUpdateOfferD2CArgs(id: ID, offer: CreateOrEditOfferD2CInput)

  final case class MutationCreateOfferIAPArgs(offer: CreateOrEditOfferIAPInput)

  final case class MutationUpdateOfferIAPArgs(id: ID, offer: CreateOrEditOfferIAPInput)

  final case class MutationUpdateOfferStatusArgs(id: ID, status: OfferStatusEnum)

  final case class MutationUpdateOfferAccountingArgs(id: ID, offerAccounting: List[EditOfferAccountingInput])

  final case class MutationCreateBaseProductArgs(product: CreateOrEditBaseProductInput)

  final case class MutationUpdateBaseProductArgs(id: ID, product: CreateOrEditBaseProductInput)

  final case class MutationCreateOneTimePurchaseProductArgs(product: CreateOrEditOneTimePurchaseProductInput)

  final case class MutationUpdateOneTimePurchaseProductArgs(id: ID, product: CreateOrEditOneTimePurchaseProductInput)

  final case class MutationUpdateProductStatusArgs(id: ID, status: ProductStatusType)

  final case class MutationCreateEntitlementArgs(entitlement: CreateEntitlementInput)

  final case class MutationCreateCampaignD2CArgs(campaign: CreateOrEditCampaignD2CInput)

  final case class MutationUpdateCampaignD2CArgs(id: ID, campaign: CreateOrEditCampaignD2CInput)

  final case class MutationCreateCampaignIAPArgs(campaign: CreateOrEditCampaignIAPInput)

  final case class MutationUpdateCampaignIAPArgs(id: ID, campaign: CreateOrEditCampaignIAPInput)

  final case class MutationUpdateCampaignStatusArgs(id: ID, status: CampaignStatusEnum)

  final case class MutationUpdateCampaignReportAsPaidArgs(id: ID, isReportAsPaid: Boolean)

  final case class MutationUpdatePartnerArgs(id: ID, partialPartnerPayload: UpdatePartnerPartialPayload)

  final case class MutationCreatePartnerArgs(partner: CreatePartnerPayload)

  final case class MutationCreateCohortArgs(cohortPayload: CreateCohortPayload)

  final case class MutationUpdateCohortArgs(id: ID, cohortPayload: UpdateCohortPayload)

  final case class MutationCreatePriceChangeEventArgs(event: CreatePriceChangeEventInput)

  final case class MutationCreatePriceChangeCohortsArgs(payload: CreatePriceChangeCohortsInput)

  final case class MutationCreateAndUpdatePriceChangeEventCohortArgs(payload: CreateUpdatePriceChangeEventCohortInput)

  final case class MutationAnalyzePriceChangeEventArgs(payload: PriceChangeEventStatusInput)

  final case class MutationValidatePriceChangeEventArgs(payload: PriceChangeEventStatusInput)

  final case class MutationSubmitEventApprovalArgs(payload: SubmitEventApprovalInput)

  final case class MutationCreatePriceChangeOffersArgs(payload: CreatePriceChangeOffersInput)

  final case class MutationCreateSkuArgs(sku: CreateSkuInput)

  final case class MutationUpdateSkuArgs(id: ID, input: UpdateSkuInput)

  final case class MutationCreateRedemptionCodesArgs(redemptionCodesInput: CreateRedemptionCodesInput)

  final case class ReferenceLink(key: String, url: String)

  final case class Country(id: String, countryCode: String, name: String, region: String, currencies: List[Currency])

  final case class OfferSku(name: String, key: String, countries: List[Country], countryIds: List[String])

  final case class CohortEligibility(
      includedCohorts: List[Cohort],
      excludedCohorts: List[Cohort],
      includedCohortIds: List[ID],
      excludedCohortIds: List[ID]
  )

  final case class Transition(
      id: ID,
      eligibility: CohortEligibility,
      transitionDate: scala.Option[String],
      transitionDateOperator: scala.Option[TransitionDateOperatorEnum],
      transitionOffer: Offer,
      transitionReason: TransitionReason
  )

  final case class DurationLength(durationLength: scala.Option[Int], durationUnit: scala.Option[DurationUnit])
      extends Duration

  final case class DurationDate(endDate: String) extends Duration

  final case class OfferD2C(
      id: ID,
      legacyId: scala.Option[ID],
      name: String,
      description: scala.Option[String],
      createdBy: String,
      createdDate: String,
      updatedBy: String,
      updatedDate: String,
      offerDiscountId: scala.Option[ID],
      priceType: PriceType,
      billingType: BillingType,
      startDate: String,
      endDate: scala.Option[String],
      referenceLinks: List[ReferenceLink],
      countries: List[Country],
      status: OfferStatusEnum,
      packageId: scala.Option[String],
      `package`: scala.Option[Package],
      currency: Currency,
      billingFrequency: BillingFrequency,
      products: List[Product],
      referenceOffers: List[Offer],
      productPhaseData: List[ProductPhaseData],
      legacySkus: List[OfferSku],
      transitions: List[Transition],
      campaigns: scala.Option[List[Campaign]],
      brands: List[Brand],
      offerPhases: List[OfferPhase]
  ) extends Offer

  final case class Offer3PP(
      id: ID,
      legacyId: scala.Option[ID],
      name: String,
      description: scala.Option[String],
      createdBy: String,
      createdDate: String,
      updatedBy: String,
      updatedDate: String,
      offerDiscountId: scala.Option[ID],
      priceType: PriceType,
      billingType: BillingType,
      startDate: String,
      endDate: scala.Option[String],
      referenceLinks: List[ReferenceLink],
      countries: List[Country],
      status: OfferStatusEnum,
      packageId: scala.Option[String],
      `package`: scala.Option[Package],
      promoType3PP: PromoType3PP,
      partnerSaleType: PartnerSaleTypeEnum,
      partnerPaidAmount: PartnerPaidAmount,
      paidUpon: PaidUpon,
      products: List[Product],
      referenceOffers: List[Offer],
      productPhaseData: List[ProductPhaseData],
      legacySkus: List[OfferSku],
      transitions: List[Transition],
      campaigns: scala.Option[List[Campaign]],
      brands: List[Brand],
      durationLength: scala.Option[DurationLength]
  ) extends Offer

  final case class OfferIAP(
      id: ID,
      legacyId: scala.Option[ID],
      name: String,
      description: scala.Option[String],
      createdBy: String,
      createdDate: String,
      updatedBy: String,
      updatedDate: String,
      offerDiscountId: scala.Option[ID],
      billingFrequency: BillingFrequency,
      priceType: PriceType,
      billingType: BillingType,
      startDate: String,
      endDate: scala.Option[String],
      referenceLinks: List[ReferenceLink],
      countries: List[Country],
      status: OfferStatusEnum,
      packageId: scala.Option[String],
      `package`: scala.Option[Package],
      products: List[Product],
      referenceOffers: List[Offer],
      productPhaseData: List[ProductPhaseData],
      legacySkus: List[OfferSku],
      transitions: List[Transition],
      campaigns: scala.Option[List[Campaign]],
      brands: List[Brand],
      offerPhases: List[IAPPhase]
  ) extends Offer

  final case class ProductPhaseData(
      offerId: ID,
      productId: ID,
      phaseId: scala.Option[ID],
      retailPriceId: scala.Option[ID],
      referenceOfferId: scala.Option[ID],
      legacy: scala.Option[String]
  )

  final case class RetailPhase(
      id: ID,
      `type`: PhaseType,
      phaseOrder: Int,
      breakdowns: List[Breakdown],
      finalPrice: Int,
      products: List[Product],
      offer: Offer,
      offerId: ID
  ) extends OfferPhase

  final case class DiscountPhase(
      id: ID,
      `type`: PhaseType,
      phaseOrder: Int,
      durationLength: scala.Option[DurationLength],
      finalPrice: Int,
      breakdowns: List[Breakdown],
      products: List[Product],
      offer: Offer,
      discountAmount: Int,
      discountCurrency: Currency,
      offerId: ID
  ) extends OfferPhase

  final case class FreeTrialPhase(
      id: ID,
      `type`: PhaseType,
      phaseOrder: Int,
      duration: Duration,
      paymentPaths: List[FreeTrialPath],
      finalPrice: Int,
      breakdowns: List[Breakdown],
      products: List[Product],
      offer: Offer,
      offerId: ID
  ) extends OfferPhase

  final case class PrePaidPhase(
      id: ID,
      `type`: PhaseType,
      phaseOrder: Int,
      durationLength: scala.Option[DurationLength],
      finalPrice: Int,
      breakdowns: List[Breakdown],
      products: List[Product],
      offer: Offer,
      offerId: ID
  ) extends OfferPhase

  final case class IAPPhase(
      id: ID,
      `type`: PhaseType,
      phaseOrder: Int,
      products: List[Product],
      duration: scala.Option[Duration],
      offerId: ID
  )

  final case class Breakdown(
      phaseOrder: Int,
      phaseId: ID,
      phaseType: PhaseType,
      offerId: ID,
      productId: ID,
      discountId: ID,
      finalPrice: scala.Option[Int],
      finalPriceAfterDiscount: scala.Option[Int],
      discount: Discount,
      product: Product,
      currency: Currency
  )

  final case class Discount(id: String, discountAmount: Int, currency: Currency, product: Product, productId: ID)

  final case class FreeTrialBreakdown(id: String, paymentType: PaymentType, durationLength: DurationLength)

  final case class FreeTrialPath(
      eligibility: List[Eligibility],
      paymentBreakdown: List[FreeTrialBreakdown],
      products: List[Product]
  )

  final case class BaseProduct(
      id: ID,
      legacyId: scala.Option[String],
      name: String,
      description: scala.Option[String],
      `type`: ProductEntityType,
      createdBy: String,
      createdDate: String,
      updatedBy: scala.Option[String],
      updatedDate: scala.Option[String],
      status: String,
      entitlements: ZQuery[Env, Throwable, List[Entitlement]],
      tierDefinitions: List[TierDefinition]
  ) extends Product

  final case class OneTimePurchaseProduct(
      id: ID,
      legacyId: scala.Option[String],
      name: String,
      description: scala.Option[String],
      `type`: ProductEntityType,
      createdBy: String,
      createdDate: String,
      updatedBy: scala.Option[String],
      updatedDate: scala.Option[String],
      entitlements: ZQuery[Env, Throwable, List[Entitlement]],
      tierDefinitions: List[TierDefinition],
      status: String,
      startDate: String,
      endDate: scala.Option[String]
  ) extends Product

  final case class Entitlement(id: String, name: String)

  final case class EntitlementFromEDC(
      id: ID,
      description: scala.Option[String],
      `type`: scala.Option[EntitlementType],
      values: scala.Option[EntitlementValues],
      resolver: scala.Option[EntitlementResolver],
      tags: List[Tag],
      valueType: scala.Option[String]
  )

  final case class EntitlementValues(
      min: scala.Option[Int],
      max: scala.Option[Int],
      default: scala.Option[Int],
      enumerations: List[String]
  )

  final case class Voucher(
      id: String,
      offerId: String,
      countries: List[String],
      platforms: List[String],
      isActive: scala.Option[Boolean]
  )

  final case class CampaignD2C(
      id: ID,
      legacyIds: List[String],
      name: String,
      description: scala.Option[String],
      billingType: CampaignBilling,
      brand: Brand,
      countries: List[Country],
      referenceLinks: List[ReferenceLink],
      createdBy: scala.Option[String],
      createdDate: scala.Option[String],
      updatedBy: scala.Option[String],
      updatedDate: scala.Option[String],
      status: String,
      vouchers: List[Voucher],
      startDate: String,
      @GQLDescription("null for Unlimited")
      endDate: scala.Option[String],
      @GQLDescription("Bamtech as default when checkbox is checked")
      promoPartner: PromoPartner,
      provider: scala.Option[String],
      hasPaymentMethod: Boolean,
      hasSendExpirationNotices: Boolean,
      hasSignupFlow: Boolean,
      hasAccountManagement: Boolean,
      hasPreCancel: Boolean,
      hasConcessions: Boolean,
      isReportAsPaid: scala.Option[Boolean],
      @GQLDescription("null for customUrl field means to no customURL and string means the url")
      customUrl: scala.Option[String],
      hasRedemptionCodeRequired: Boolean,
      isUpsell: Boolean,
      edcEntitlement: scala.Option[String],
      upsellCohorts: List[Cohort],
      platforms: List[Platform],
      offerSets: List[OfferSet],
      legacySKUs: List[SKU]
  ) extends Campaign

  final case class Platform(id: ID, name: String, isActive: Boolean)

  final case class CampaignIAP(
      id: ID,
      legacyIds: List[String],
      name: String,
      description: scala.Option[String],
      billingType: CampaignBilling,
      brand: Brand,
      countries: List[Country],
      platforms: List[Platform],
      referenceLinks: List[ReferenceLink],
      createdBy: scala.Option[String],
      createdDate: scala.Option[String],
      updatedBy: scala.Option[String],
      updatedDate: scala.Option[String],
      status: String,
      startDate: String,
      @GQLDescription("null for Unlimited")
      endDate: scala.Option[String],
      offerSets: List[OfferSet],
      legacySKUs: List[SKU],
      hasSignupFlow: Boolean,
      hasAccountManagement: Boolean,
      @GQLDescription("null for customUrl field means to no customURL and string means the url")
      isUpsell: Boolean,
      edcEntitlement: scala.Option[String],
      upsellCohorts: List[Cohort]
  ) extends Campaign

  final case class Campaign3PP(
      id: ID,
      legacyIds: List[String],
      name: String,
      description: scala.Option[String],
      billingType: CampaignBilling,
      brand: Brand,
      countries: List[Country],
      referenceLinks: List[ReferenceLink],
      createdBy: scala.Option[String],
      createdDate: scala.Option[String],
      updatedBy: scala.Option[String],
      updatedDate: scala.Option[String],
      status: String,
      startDate: String,
      @GQLDescription("null for Unlimited")
      endDate: scala.Option[String],
      billingPartner: Partner,
      sourceProvider: SourceProvider,
      offers: List[Offer3PP],
      legacySKUs: List[SKU]
  ) extends Campaign

  final case class PromoPartner(id: String, name: String)

  final case class OfferSet(id: ID, offers: List[Offer], eligibility: scala.Option[EligibilityCohortInfo])

  final case class SourceProvider(id: String, name: String, country: Country)

  final case class EligibilityCohortInfo(includedCohorts: List[Cohort], excludedCohorts: List[Cohort])

  final case class Partner(
      id: ID,
      name: String,
      status: PartnerStatus,
      referenceLinks: List[ReferenceLink],
      paidUpon: BountyPaidUpon,
      specialRules: List[PartnerBountyRule],
      bounties: List[BasePartnerBounty],
      types: List[PartnerTypes],
      subpartners: List[Subpartner],
      updatedBy: String,
      updatedDate: String
  ) extends BasePartner

  final case class Subpartner(
      id: ID,
      name: String,
      status: PartnerStatus,
      referenceLinks: List[ReferenceLink],
      paidUpon: BountyPaidUpon,
      specialRules: List[PartnerBountyRule],
      bounties: List[BasePartnerBounty],
      types: List[SubpartnerTypes],
      sourceProviderDisplayName: scala.Option[String],
      sourceProviderCountry: scala.Option[Country],
      updatedBy: String,
      updatedDate: String
  ) extends BasePartner

  final case class Option(value: String, name: String)

  final case class BooleanOption(value: Boolean, name: String)

  final case class OptionsParam(
      fieldName: String,
      displayName: String,
      description: scala.Option[String],
      canBeNull: Boolean,
      `type`: ParamType,
      defaultValue: scala.Option[Option],
      enumOptions: List[Option]
  ) extends Param

  final case class StringParam(
      fieldName: String,
      displayName: String,
      description: scala.Option[String],
      canBeNull: Boolean,
      `type`: ParamType,
      defaultValue: scala.Option[List[String]]
  ) extends Param

  final case class DateTimeStringParam(
      fieldName: String,
      displayName: String,
      description: scala.Option[String],
      canBeNull: Boolean,
      `type`: ParamType,
      defaultValue: scala.Option[String]
  ) extends Param

  final case class IntParam(
      fieldName: String,
      displayName: String,
      description: scala.Option[String],
      canBeNull: Boolean,
      `type`: ParamType,
      defaultValue: scala.Option[Int]
  ) extends Param

  final case class BooleanParam(
      fieldName: String,
      displayName: String,
      description: scala.Option[String],
      canBeNull: Boolean,
      `type`: ParamType,
      defaultValue: scala.Option[Boolean],
      nestedParams: List[Param]
  ) extends Param

  final case class Cohort(id: ID, name: String, description: String, definition: String, active: Boolean)

  final case class Resolver(
      resolver: String,
      selectorName: String,
      description: String,
      operatorOptions: List[BooleanOption],
      params: List[Param]
  )

  final case class ParamContainer(
      fieldName: String,
      displayName: String,
      description: scala.Option[String],
      canBeNull: Boolean,
      `type`: ParamType,
      defaultValue: scala.Option[Param],
      nestedParams: List[Param]
  ) extends Param

  final case class DateTimeComparator(
      fieldName: String,
      displayName: String,
      description: scala.Option[String],
      canBeNull: Boolean,
      `type`: ParamType,
      defaultValue: scala.Option[Param],
      nestedParams: List[Param]
  ) extends Param

  final case class Package(
      id: String,
      name: String,
      description: scala.Option[String],
      `type`: ProductEntityType,
      products: List[Product],
      status: scala.Option[String],
      updatedDate: scala.Option[String],
      updatedBy: scala.Option[String]
  )

  final case class BasePartnerBounty(
      id: ID,
      product: Product,
      bountyAmount: Int,
      durationLength: DurationLength,
      subpartner: scala.Option[Subpartner]
  ) extends Bounty

  final case class Currency(code: String)

  final case class PriceChangeEvent(
      identifier: String,
      name: String,
      eventCohorts: List[EventCohort],
      definitionId: scala.Option[String],
      total: scala.Option[Int],
      totalByCohort: List[CohortCount],
      isCalculating: Boolean,
      status: PriceChangeEventStatus,
      updatedDate: String,
      updatedBy: String,
      `type`: PriceChangeEventType,
      newCustomersStartDate: scala.Option[String],
      existingCustomersStartDate: scala.Option[String],
      version: Int,
      eventDefinition: scala.Option[List[EventDefinition]]
  )

  final case class EventCohort(
      cohort: Cohort,
      eventOffers: List[EventOffer],
      messagingConfigs: List[MessagingConfig],
      cohortId: ID,
      priceChangeEventId: ID,
      version: Int
  )

  final case class EventOffer(
      id: ID,
      fromOffer: OfferD2C,
      toOffer: scala.Option[OfferD2C],
      newPlanName: scala.Option[String],
      newPlanPrice: scala.Option[Int],
      newPlanCurrency: scala.Option[Currency],
      version: Int,
      newPlanCurrencyId: scala.Option[String],
      fromOfferId: String,
      toOfferId: scala.Option[String],
      cohortId: ID,
      priceChangeEventId: ID
  )

  final case class MessagingConfig(
      id: ID,
      messageType: MessageType,
      qaConfigurationId: String,
      prodConfigurationId: String,
      messageTiming: MessageTiming,
      messageTimingFixedDate: scala.Option[String],
      messageTimingDynamicDate: scala.Option[String],
      messageTimingRangeDates: scala.Option[MessageTimingRangeDates],
      priceChangeEventId: ID,
      cohortId: ID,
      version: Int
  )

  final case class MessageTimingRangeDates(startDate: String, endDate: scala.Option[String])

  final case class SyncEvent(publishedConfiguration: String)

  final case class CohortCount(id: String, count: Int)

  final case class EventDefinition(offer: OfferD2C, transitionToOffer: OfferD2C, cohorts: List[Cohort])

  final case class SKU(
      id: ID,
      key: ID,
      name: String,
      description: scala.Option[String],
      platform: String,
      countries: List[Country],
      packages: List[Package],
      products: List[Product],
      offers: List[Offer],
      campaigns: scala.Option[List[Campaign]],
      updatedDate: scala.Option[String],
      updatedBy: scala.Option[String],
      billingType: BillingType,
      billingFrequency: scala.Option[BillingFrequency],
      brand: scala.Option[Brand],
      referenceLinks: List[ReferenceLink]
  )

  final case class CodeGenTask(
      id: String,
      brand: Brand,
      status: CodeGenTaskStatus,
      fileUrl: String,
      isTest: Boolean,
      numberOfCodes: Int,
      createdDate: String,
      offerId: scala.Option[ID],
      campaignId: scala.Option[ID],
      createdBy: scala.Option[String],
      offer: scala.Option[Offer],
      campaign: scala.Option[Campaign],
      lastDownloadedDate: scala.Option[String],
      lastDownloadedBy: scala.Option[String]
  )

  final case class RedemptionCodeResponse(
      redemptionCode: RedemptionCodeInfo,
      promotionInfo: PromotionInfo,
      creationInfo: CreationInfo
  )

  final case class PromotionInfo(campaignId: String, offerId: String)

  final case class Rule(length: Int, pattern: String, prefix: String, postfix: String, excludes: String, `case`: String)

  final case class RedemptionCodeInfo(code: String, status: String, usage: String, partner: String)

  final case class CreationInfo(createdAt: String, createdBy: String)

  final case class TierDefinition(
      id: ID,
      name: String,
      description: scala.Option[String],
      entitlementTierDefinitions: List[EntitlementTierDefinitions],
      countryIds: List[ID],
      countries: scala.Option[List[Country]],
      createdBy: String,
      createdDate: String,
      updatedBy: String,
      updatedDate: String
  )

  final case class EntitlementTierDefinitions(
      entitlementId: ID,
      entitlement: EntitlementFromEDC,
      value: scala.Option[Int]
  )

  final case class ReferenceLinkPayload(key: String, url: String)

  final case class CountryPayload(
      id: scala.Option[String],
      countryCode: scala.Option[String],
      name: scala.Option[String],
      region: scala.Option[String],
      currencies: scala.Option[List[scala.Option[CurrencyPayload]]]
  )

  final case class OfferAccountingDiscountInput(discountId: String, discountAmount: Int, productId: ID)

  final case class EditOfferAccountingInput(phaseId: ID, discounts: List[OfferAccountingDiscountInput])

  final case class OfferTransitionInput(
      targetOfferId: String,
      transitionReason: String,
      transitionDate: scala.Option[String],
      transitionDateOperator: scala.Option[String],
      eligibleCohorts: List[String],
      ineligibleCohorts: List[String]
  )

  final case class RetailPhaseInput(finalPrice: Int)

  final case class PromoPhaseInput(
      `type`: PhaseTypeEnum,
      finalPrice: Int,
      durationLength: scala.Option[String],
      durationUnit: scala.Option[DurationUnit],
      phaseEndDate: scala.Option[String]
  )

  final case class CreateOrEditOfferD2CInput(
      name: String,
      description: scala.Option[String],
      offerType: scala.Option[String],
      priceType: PriceTypeInput,
      startDate: scala.Option[String],
      endDate: scala.Option[String],
      referenceLinks: List[ReferenceLinkPayload],
      products: List[String],
      countries: List[String],
      billingFrequency: BillingFrequency,
      currencyCode: String,
      referenceOffers: List[String],
      status: OfferStatusEnum,
      packageId: scala.Option[String],
      transitions: List[OfferTransitionInput],
      retailPhases: List[RetailPhaseInput],
      promoPhases: List[PromoPhaseInput]
  )

  final case class CreateOrEditOfferIAPInput(
      id: scala.Option[ID],
      name: String,
      description: scala.Option[String],
      offerType: scala.Option[String],
      priceType: PriceTypeInput,
      startDate: scala.Option[String],
      endDate: scala.Option[String],
      referenceLinks: List[ReferenceLinkPayload],
      products: List[String],
      countries: List[String],
      billingFrequency: BillingFrequency,
      status: OfferStatusEnum,
      packageId: scala.Option[String],
      iapPhases: List[IAPPhaseInput]
  )

  final case class IAPPhaseInput(
      `type`: PhaseTypeEnum,
      durationLength: scala.Option[String],
      durationUnit: scala.Option[DurationUnit]
  )

  final case class OffersFilter(billingType: BillingType)

  final case class CreateOrEditBaseProductInput(
      name: String,
      description: scala.Option[String],
      `type`: ProductEntityType,
      entitlements: List[String],
      tierDefinitions: List[EntitlementTierDefinition]
  )

  final case class CreateOrEditOneTimePurchaseProductInput(
      name: String,
      description: scala.Option[String],
      `type`: ProductEntityType,
      entitlements: List[String],
      tierDefinitions: List[EntitlementTierDefinition],
      startDate: String,
      endDate: String
  )

  final case class EntitlementTierDefinition(
      id: scala.Option[ID],
      name: String,
      entitlements: List[ProductTierEntitlement],
      countries: List[String]
  )

  final case class ProductTierEntitlement(id: scala.Option[String], value: scala.Option[Int])

  final case class CreateEntitlementInput(name: String)

  final case class PromoPartnerPayload(id: String, name: String)

  final case class OfferSetPayload(
      id: ID,
      offers: List[String],
      eligibility: scala.Option[EligibilityCohortInfoPayload]
  )

  final case class EligibilityCohortInfoPayload(
      includedCohorts: scala.Option[List[String]],
      excludedCohorts: scala.Option[List[String]]
  )

  final case class VoucherBody(
      id: String,
      offerId: String,
      countries: List[String],
      platforms: List[String],
      isActive: scala.Option[Boolean]
  )

  final case class CreateOrEditCampaignD2CInput(
      name: String,
      description: String,
      billingType: CampaignBilling,
      brand: Brand,
      countries: List[String],
      referenceLinks: scala.Option[List[ReferenceLinkPayload]],
      startDate: String,
      endDate: scala.Option[String],
      promoPartner: scala.Option[String],
      provider: scala.Option[String],
      hasPaymentMethod: scala.Option[Boolean],
      hasSendExpirationNotices: scala.Option[Boolean],
      hasSignupFlow: scala.Option[Boolean],
      hasAccountManagement: scala.Option[Boolean],
      hasPreCancel: scala.Option[Boolean],
      hasConcessions: scala.Option[Boolean],
      isReportAsPaid: scala.Option[Boolean],
      customUrl: scala.Option[String],
      hasRedemptionCodeRequired: scala.Option[Boolean],
      isUpsell: scala.Option[Boolean],
      edcEntitlement: scala.Option[String],
      upsellCohortIds: scala.Option[List[String]],
      platforms: scala.Option[List[String]],
      offerSets: scala.Option[List[OfferSetPayload]],
      vouchers: scala.Option[List[VoucherBody]],
      status: CampaignStatusEnum
  )

  final case class CreateOrEditCampaignIAPInput(
      name: String,
      description: scala.Option[String],
      billingType: CampaignBilling,
      brand: Brand,
      startDate: String,
      endDate: scala.Option[String],
      countries: List[String],
      platforms: List[String],
      offerSets: scala.Option[List[OfferSetPayload]],
      referenceLinks: List[ReferenceLinkPayload],
      status: CampaignStatusEnum,
      hasSignupFlow: scala.Option[Boolean],
      hasAccountManagement: scala.Option[Boolean],
      isUpsell: scala.Option[Boolean],
      edcEntitlement: scala.Option[String],
      upsellCohortIds: List[String]
  )

  final case class CreatePartnerPayload(
      name: String,
      types: List[PartnerTypes],
      referenceLinks: List[ReferenceLinkPayload],
      status: PartnerStatus
  )

  final case class UpdatePartnerPartialPayload(
      name: scala.Option[String],
      types: scala.Option[List[scala.Option[PartnerTypes]]],
      referenceLinks: scala.Option[List[scala.Option[ReferenceLinkPayload]]],
      subpartners: scala.Option[List[scala.Option[SubpartnerPayload]]],
      paidUpon: scala.Option[BountyPaidUpon],
      specialRules: scala.Option[List[scala.Option[PartnerBountyRule]]],
      status: scala.Option[String]
  )

  final case class SubpartnerPayload(
      referenceLinks: scala.Option[List[scala.Option[ReferenceLinkPayload]]],
      sourceProviderDisplayName: scala.Option[String],
      sourceProviderCountry: scala.Option[CountryPayload]
  )

  final case class CreateCohortPayload(name: String, description: scala.Option[String], definition: String)

  final case class UpdateCohortPayload(name: String, description: scala.Option[String], definition: String)

  final case class CurrencyPayload(code: String)

  final case class CreatePriceChangeEventInput(
      identifier: String,
      name: String,
      `type`: PriceChangeEventType,
      newCustomersStartDate: scala.Option[String],
      existingCustomersStartDate: scala.Option[String]
  )

  final case class CreatePriceChangeCohortsInput(identifier: String, cohorts: List[String])

  final case class PriceChangeEventStatusInput(identifier: String)

  final case class SubmitEventApprovalInput(identifier: String, definitionId: String)

  final case class CreateUpdatePriceChangeEventCohortInput(
      priceChangeId: ID,
      cohortId: ID,
      eventOffers: List[EventOfferInput],
      messagingConfigs: List[MessagingConfigInput]
  )

  final case class EventOfferInput(
      id: scala.Option[ID],
      fromOffer: String,
      toOffer: scala.Option[String],
      newPlanName: scala.Option[String],
      newPlanPrice: scala.Option[Int],
      newPlanCurrency: scala.Option[String]
  )

  final case class MessagingConfigInput(
      id: scala.Option[ID],
      messageType: MessageType,
      qaConfigurationId: String,
      prodConfigurationId: String,
      messageTiming: MessageTiming,
      messageTimingFixedDate: scala.Option[String],
      messageTimingDynamicDate: scala.Option[String],
      messageTimingRangeDates: scala.Option[MessageTimingRangeDatesInput]
  )

  final case class MessageTimingRangeDatesInput(startDate: String, endDate: scala.Option[String])

  final case class CreatePriceChangeOffersInput(identifier: String, offers: List[OfferTransitionOfferInput])

  final case class OfferTransitionOfferInput(offerId: String, transitionToOffer: scala.Option[String])

  final case class CreateSkuInput(
      id: ID,
      name: String,
      description: scala.Option[String],
      platform: String,
      countries: List[String],
      packageIds: List[String],
      products: List[String],
      offers: scala.Option[List[String]],
      campaigns: scala.Option[List[String]],
      billingType: String,
      billingFrequency: scala.Option[String],
      brand: scala.Option[Brand],
      referenceLinks: scala.Option[List[ReferenceLinkPayload]]
  )

  final case class UpdateSkuInput(
      description: String,
      countries: List[String],
      packageIds: List[String],
      products: List[String],
      offers: List[String],
      campaigns: List[String],
      billingFrequency: scala.Option[String],
      brand: scala.Option[Brand],
      referenceLinks: scala.Option[List[ReferenceLinkPayload]]
  )

  final case class CreateRedemptionCodesInput(
      promotionInfo: PromotionInfoInput,
      codeConfig: RuleInput,
      isTest: Boolean,
      numberOfCodes: Int,
      brand: String
  )

  final case class PromotionInfoInput(campaignId: String, offerId: String)

  final case class RuleInput(
      length: Int,
      pattern: RulePattern,
      prefix: String,
      postfix: String,
      excludes: String,
      `case`: RuleCase
  )

  sealed trait Duration extends scala.Product with scala.Serializable

  @GQLInterface
  sealed trait Offer extends scala.Product with scala.Serializable {
    def id: ID

    def legacyId: scala.Option[ID]

    def name: String

    def description: scala.Option[String]

    def createdBy: String

    def createdDate: String

    def updatedBy: String

    def updatedDate: String

    def offerDiscountId: scala.Option[ID]

    def priceType: PriceType

    def billingType: BillingType

    def startDate: String

    def endDate: scala.Option[String]

    def referenceLinks: List[ReferenceLink]

    def countries: List[Country]

    def status: OfferStatusEnum

    def packageId: scala.Option[String]

    def `package`: scala.Option[Package]

    def products: List[Product]

    def productPhaseData: List[ProductPhaseData]

    def legacySkus: List[OfferSku]

    def transitions: List[Transition]

    def campaigns: scala.Option[List[Campaign]]

    def brands: List[Brand]
  }

  @GQLInterface
  sealed trait OfferPhase extends scala.Product with scala.Serializable {
    def id: ID

    def `type`: PhaseType

    def phaseOrder: Int

    def breakdowns: List[Breakdown]

    def finalPrice: Int

    def products: List[Product]

    def offer: Offer

    def offerId: ID
  }

  @GQLInterface
  sealed trait Product extends scala.Product with scala.Serializable {
    def id: ID

    def legacyId: scala.Option[String]

    def name: String

    def description: scala.Option[String]

    def `type`: ProductEntityType

    def createdBy: String

    def createdDate: String

    def updatedBy: scala.Option[String]

    def updatedDate: scala.Option[String]

    def status: String

    def entitlements: ZQuery[Env, Throwable, List[Entitlement]]

    def tierDefinitions: List[TierDefinition]
  }

  @GQLInterface
  sealed trait Campaign extends scala.Product with scala.Serializable {
    def id: ID

    def legacyIds: List[String]

    def name: String

    def description: scala.Option[String]

    def billingType: CampaignBilling

    def brand: Brand

    def countries: List[Country]

    def referenceLinks: List[ReferenceLink]

    def createdBy: scala.Option[String]

    def createdDate: scala.Option[String]

    def updatedBy: scala.Option[String]

    def updatedDate: scala.Option[String]

    def status: String

    def startDate: String

    @GQLDescription("null for Unlimited")
    def endDate: scala.Option[String]

    def legacySKUs: List[SKU]
  }

  @GQLInterface
  sealed trait BasePartner extends scala.Product with scala.Serializable {
    def id: ID

    def name: String

    def status: PartnerStatus

    def referenceLinks: List[ReferenceLink]

    def paidUpon: BountyPaidUpon

    def specialRules: List[PartnerBountyRule]

    def bounties: List[BasePartnerBounty]

    def updatedBy: String

    def updatedDate: String
  }

  @GQLInterface
  sealed trait Param extends scala.Product with scala.Serializable {
    def fieldName: String

    def displayName: String

    def description: scala.Option[String]

    def canBeNull: Boolean

    def `type`: ParamType
  }

  @GQLInterface
  sealed trait Bounty extends scala.Product with scala.Serializable {
    def id: ID

    def product: Product

    def bountyAmount: Int

    def durationLength: DurationLength
  }

  sealed trait BillingType extends scala.Product with scala.Serializable

  object BillingType {
    case object DIRECT_BILLING extends BillingType

    case object ThirdPP extends BillingType

    case object IAP extends BillingType
  }

  sealed trait OfferStatusEnum extends scala.Product with scala.Serializable

  object OfferStatusEnum {
    case object DRAFT extends OfferStatusEnum

    case object NEEDS_REVIEW extends OfferStatusEnum

    case object FINANCE_REVIEW extends OfferStatusEnum

    case object FINANCE_APPROVED extends OfferStatusEnum

    case object LIVE extends OfferStatusEnum

    case object LIVE_WITH_EDITS extends OfferStatusEnum

    case object DEACTIVATED extends OfferStatusEnum

    case object UNSUPPORTED extends OfferStatusEnum
  }

  sealed trait PriceType extends scala.Product with scala.Serializable

  object PriceType {
    case object RETAIL extends PriceType

    case object PROMO extends PriceType

    case object NONE extends PriceType
  }

  sealed trait Brand extends scala.Product with scala.Serializable

  object Brand {
    case object DISNEY extends Brand

    case object LAUNCHPAD extends Brand

    case object STAR extends Brand

    case object ESPN extends Brand

    case object HULU extends Brand
  }

  sealed trait BillingFrequency extends scala.Product with scala.Serializable

  object BillingFrequency {
    case object NONE extends BillingFrequency

    case object ONCE extends BillingFrequency

    case object DAY extends BillingFrequency

    case object MONTH extends BillingFrequency

    case object YEAR extends BillingFrequency
  }

  sealed trait PartnerPaidAmount extends scala.Product with scala.Serializable

  object PartnerPaidAmount {
    case object RETAIL extends PartnerPaidAmount

    case object WHOLESALE extends PartnerPaidAmount

    case object NONE_PAID extends PartnerPaidAmount
  }

  sealed trait PaidUpon extends scala.Product with scala.Serializable

  object PaidUpon {
    case object ENROLLMENT extends PaidUpon

    case object ACTIVATION extends PaidUpon

    case object NONE extends PaidUpon
  }

  sealed trait TransitionDateOperatorEnum extends scala.Product with scala.Serializable

  object TransitionDateOperatorEnum {
    case object LESS_THAN extends TransitionDateOperatorEnum

    case object LESS_THAN_EQUAL_TO extends TransitionDateOperatorEnum

    case object EQUALS extends TransitionDateOperatorEnum

    case object GREATER_THAN_EQUAL_TO extends TransitionDateOperatorEnum

    case object GREATER_THAN extends TransitionDateOperatorEnum
  }

  sealed trait TransitionReason extends scala.Product with scala.Serializable

  object TransitionReason {
    case object PRICE_CHANGE extends TransitionReason

    case object LEGAL_COMPLIANCE extends TransitionReason

    case object MLB_SEASON_END extends TransitionReason

    case object PROMOTION_EXPIRATION extends TransitionReason
  }

  sealed trait DurationUnit extends scala.Product with scala.Serializable

  object DurationUnit {
    case object MONTH extends DurationUnit

    case object YEAR extends DurationUnit

    case object WEEK extends DurationUnit

    case object DAY extends DurationUnit

    case object ONCE extends DurationUnit
  }

  sealed trait PromoType3PP extends scala.Product with scala.Serializable

  object PromoType3PP {
    case object PROMO_DURATION extends PromoType3PP

    case object FREE_TRIAL extends PromoType3PP

    case object NONE extends PromoType3PP
  }

  sealed trait PartnerSaleTypeEnum extends scala.Product with scala.Serializable

  object PartnerSaleTypeEnum {
    case object A_LA_CARTE extends PartnerSaleTypeEnum

    case object BUNDLE extends PartnerSaleTypeEnum
  }

  sealed trait PriceTypeInput extends scala.Product with scala.Serializable

  object PriceTypeInput {
    case object RETAIL extends PriceTypeInput

    case object PROMO extends PriceTypeInput
  }

  sealed trait PhaseTypeEnum extends scala.Product with scala.Serializable

  object PhaseTypeEnum {
    case object RETAIL extends PhaseTypeEnum

    case object FREE_TRIAL extends PhaseTypeEnum

    case object DISCOUNT extends PhaseTypeEnum

    case object PREPAID extends PhaseTypeEnum
  }

  sealed trait PhaseType extends scala.Product with scala.Serializable

  object PhaseType {
    case object DISCOUNT extends PhaseType

    case object FREE_TRIAL extends PhaseType

    case object PREPAID extends PhaseType

    case object RETAIL extends PhaseType

    case object SPLIT_BILLING extends PhaseType
  }

  sealed trait Eligibility extends scala.Product with scala.Serializable

  object Eligibility {
    case object NEW extends Eligibility

    case object SUBSCRIBED extends Eligibility

    case object RETURNING extends Eligibility

    case object REACQUIRED extends Eligibility
  }

  sealed trait PaymentType extends scala.Product with scala.Serializable

  object PaymentType {
    case object MP_PAID extends PaymentType

    case object DSS_PAID extends PaymentType

    case object USER_PAID extends PaymentType
  }

  sealed trait ProductEntityType extends scala.Product with scala.Serializable

  object ProductEntityType {
    case object LIVE_TV extends ProductEntityType

    case object PAY_PER_VIEW extends ProductEntityType

    case object PREMIUM_NETWORKS extends ProductEntityType

    case object BASE extends ProductEntityType

    case object TEST extends ProductEntityType

    case object PREMIER_ACCESS extends ProductEntityType

    case object SEASONAL extends ProductEntityType

    case object FEATURE extends ProductEntityType

    case object ONE_TIME_PURCHASE extends ProductEntityType
  }

  sealed trait ProductStatusType extends scala.Product with scala.Serializable

  object ProductStatusType {
    case object DRAFT extends ProductStatusType

    case object LIVE extends ProductStatusType

    case object LIVE_WITH_EDITS extends ProductStatusType
  }

  sealed trait EntitlementType extends scala.Product with scala.Serializable

  object EntitlementType {
    case object FEATURE extends EntitlementType

    case object CONTENT extends EntitlementType
  }

  sealed trait Tag extends scala.Product with scala.Serializable

  object Tag {
    case object SELLABLE extends Tag

    case object OVERRIDE extends Tag
  }

  sealed trait EntitlementResolver extends scala.Product with scala.Serializable

  object EntitlementResolver {
    case object SUM extends EntitlementResolver

    case object MAX extends EntitlementResolver

    case object MIN extends EntitlementResolver

    case object ANY extends EntitlementResolver
  }

  sealed trait CampaignStatusEnum extends scala.Product with scala.Serializable

  object CampaignStatusEnum {
    case object DRAFT extends CampaignStatusEnum

    case object NEEDS_REVIEW extends CampaignStatusEnum

    case object FINANCE_REVIEW extends CampaignStatusEnum

    case object FINANCE_APPROVED extends CampaignStatusEnum

    case object LIVE extends CampaignStatusEnum

    case object LIVE_WITH_EDITS extends CampaignStatusEnum

    case object EXPIRED extends CampaignStatusEnum

    case object DEACTIVATED extends CampaignStatusEnum
  }

  sealed trait CampaignBilling extends scala.Product with scala.Serializable

  object CampaignBilling {
    case object BILLING_D2C extends CampaignBilling

    case object BILLING_3PP extends CampaignBilling

    case object BILLING_IAP extends CampaignBilling
  }

  sealed trait PartnerStatus extends scala.Product with scala.Serializable

  object PartnerStatus {
    case object DRAFT extends PartnerStatus

    case object LIVE extends PartnerStatus

    case object EDITED extends PartnerStatus

    case object EXPIRED extends PartnerStatus
  }

  sealed trait PartnerTypes extends scala.Product with scala.Serializable

  object PartnerTypes {
    case object BILLING_3PP extends PartnerTypes

    case object BILLING_IAP extends PartnerTypes

    case object BILLING_DEVICE extends PartnerTypes
  }

  sealed trait SubpartnerTypes extends scala.Product with scala.Serializable

  object SubpartnerTypes {
    case object BILLING_3PP extends SubpartnerTypes

    case object BILLING_IAP extends SubpartnerTypes

    case object BILLING_DEVICE extends SubpartnerTypes

    case object SOURCE_PROVIDER extends SubpartnerTypes
  }

  sealed trait CohortView extends scala.Product with scala.Serializable

  object CohortView {
    case object UI extends CohortView

    case object UPSELL extends CohortView

    case object ALL extends CohortView
  }

  sealed trait ParamType extends scala.Product with scala.Serializable

  object ParamType {
    case object OptionsParam extends ParamType

    case object StringParam extends ParamType

    case object DateTimeStringParam extends ParamType

    case object DateTimeComparator extends ParamType

    case object TimeFrame_ParamsContainer extends ParamType

    case object IntParam extends ParamType

    case object ParamContainer extends ParamType

    case object BooleanParam extends ParamType
  }

  sealed trait BountyPaidUpon extends scala.Product with scala.Serializable

  object BountyPaidUpon {
    case object CHARGE extends BountyPaidUpon

    case object ACTIVATION extends BountyPaidUpon
  }

  sealed trait PartnerBountyRule extends scala.Product with scala.Serializable

  object PartnerBountyRule {
    case object LG_LIFETIME_CAP_RULE extends PartnerBountyRule

    case object LIFETIME_CAP_RULE extends PartnerBountyRule

    case object MICROSOFT_ADD_ON_RULE extends PartnerBountyRule

    case object PERPETUAL_RULE extends PartnerBountyRule

    case object PWA_RULE extends PartnerBountyRule
  }

  sealed trait PriceChangeEventStatus extends scala.Product with scala.Serializable

  object PriceChangeEventStatus {
    case object DRAFT extends PriceChangeEventStatus

    case object VALIDATED extends PriceChangeEventStatus

    case object LIVE extends PriceChangeEventStatus
  }

  sealed trait PriceChangeEventType extends scala.Product with scala.Serializable

  object PriceChangeEventType {
    case object PRICE_CHANGE extends PriceChangeEventType

    case object ONE_TIME_SEND extends PriceChangeEventType
  }

  sealed trait MessageType extends scala.Product with scala.Serializable

  object MessageType {
    case object EMAIL extends MessageType

    case object IN_APP extends MessageType

    case object PUSH extends MessageType
  }

  sealed trait MessageTiming extends scala.Product with scala.Serializable

  object MessageTiming {
    case object MESSAGE_TIMING_FIXED_DATE extends MessageTiming

    case object MESSAGE_TIMING_DYNAMIC_DATE extends MessageTiming

    case object MESSAGE_TIMING_RANGE_DATES extends MessageTiming
  }

  sealed trait RulePattern extends scala.Product with scala.Serializable

  object RulePattern {
    case object Alpha extends RulePattern

    case object Alphanum extends RulePattern

    case object Numeric extends RulePattern
  }

  sealed trait RuleCase extends scala.Product with scala.Serializable

  object RuleCase {
    case object Lower extends RuleCase

    case object Upper extends RuleCase

    case object Mixed extends RuleCase
  }

  sealed trait CodeGenTaskStatus extends scala.Product with scala.Serializable

  object CodeGenTaskStatus {
    case object PENDING extends CodeGenTaskStatus

    case object PROCESSING extends CodeGenTaskStatus

    case object PROCESSED extends CodeGenTaskStatus
  }

}
