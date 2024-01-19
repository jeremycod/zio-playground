package com.playground.dss.omp.graphql.table.public

final case class OfferTransition(
    offerId: String,
    toOfferId: String,
    cohort: Option[String],
    startdate: Option[String],
    profile: String,
    version: Long,
    eligibility: Option[String],
    reason: Option[String]
)

case class CampaignId(id: String, profile: String)

case class ProductAttributeValue(productId: String, key: String, value: String, profile: String, version: Long)

case class SkuCampaign(skuId: String, campaignId: String, profile: String, version: Long)

case class SubPartnerAttributeValue(subPartnerId: String, key: String, value: String, profile: String, version: Long)

case class MessageType(name: String, `type`: String)

case class PartnerAttributeValue(partnerId: String, key: String, value: String, profile: String, version: Long)

case class DiscountMessage(discountId: String, name: String, value: String, profile: String, version: Long)

case class DiscountAttributeKey(key: String)

case class SubPartnerAttributeKey(key: String)

case class CampaignAttributeValue(campaignId: String, key: String, value: String, profile: String, version: Long)

case class Package(
    id: String,
    name: String,
    description: String,
    legacy: Option[String],
    author: String,
    datetime: String,
    profile: String,
    version: Long
)

case class SubPartner(
    id: String,
    name: String,
    displayname: String,
    partnerId: String,
    legacy: Option[String],
    author: String,
    datetime: String,
    profile: String,
    version: Long,
    deleted: Boolean
)

case class Price(id: String, amount: Int, currency: String, productId: String, active: Boolean, profile: String)

case class DiscountAmount(discountId: String, productId: String, amount: Int, profile: String, version: Long)

case class Offer(
    id: String,
    name: String,
    description: Option[String],
    discountId: Option[String],
    legacy: Option[String],
    author: String,
    datetime: String,
    profile: String,
    version: Long,
    deleted: Boolean
)

case class ProductEntitlement(productId: String, entitlementId: String, profile: String, version: Long)

case class OfferMetadata(offerId: String, name: String, value: String, profile: String, version: Long)

case class PartnerId(id: String, profile: String)

case class SkuId(id: String, profile: String)

case class ProductAttributeKey(key: String)

case class PackageId(id: String, profile: String)

case class SkuAttributeValue(skuId: String, key: String, value: String, profile: String, version: Long)

case class OfferPhase(
    offerId: String,
    id: String,
    durationLength: Option[Int],
    durationUnit: Option[String],
    repeatCount: Option[Int],
    endDate: Option[String],
    paymentType: String,
    discountId: Option[String],
    nextPhaseId: Option[String],
    profile: String,
    version: Long,
    legacy: Option[String]
)

case class SkuProduct(skuId: String, productId: String, profile: String, version: Long)

case class SubPartnerId(id: String, profile: String)

case class ProductDependency(productId: String, parentProductId: String, profile: String, version: Long)

case class DiscountId(id: String, profile: String)

case class OfferId(id: String, profile: String)

case class CampaignOffer(
    campaignId: String,
    offerId: String,
    legacy: Option[String],
    profile: String,
    version: Long,
    setNumber: Int
)

case class PackageProduct(packageId: String, productId: String, profile: String, version: Long)

case class OfferAttributeKey(key: String)

case class EntitlementAttributeValue(entitlementId: String, key: String, value: String, profile: String, version: Long)

case class Entitlement(
    id: String,
    name: String,
    description: Option[String],
    legacy: Option[String],
    author: String,
    datetime: String,
    profile: String,
    version: Long,
    deleted: Boolean
)

case class ProductMetadata(productId: String, name: String, value: String, profile: String, version: Long)

case class MetadataType(name: String, `type`: String)

case class OfferMessage(offerId: String, name: String, value: String, profile: String, version: Long)

case class PartnerAttributeKey(key: String)

case class EntitlementAttributeKey(key: String)

case class Sku(
    id: String,
    name: String,
    description: Option[String],
    platform: Option[String],
    countries: java.sql.Array,
    author: String,
    datetime: String,
    profile: String,
    version: Long,
    deleted: Boolean,
    legacy: Option[String]
)

case class DiscountAttributeValue(discountId: String, key: String, value: String, profile: String, version: Long)

case class CampaignMessage(campaignId: String, name: String, value: String, profile: String, version: Long)

case class CampaignAttributeKey(key: String)

case class ProductId(id: String, profile: String)

case class EntitlementId(id: String, profile: String)

case class Campaign(
    id: String,
    name: String,
    description: Option[String],
    partnerId: Option[String],
    legacy: Option[String],
    author: String,
    datetime: String,
    profile: String,
    version: Long,
    deleted: Boolean
)

case class Product(
    id: String,
    name: String,
    description: Option[String],
    legacy: Option[String],
    author: String,
    datetime: String,
    profile: String,
    version: Long,
    deleted: Boolean
)

case class ProductWithAttributes(
    id: String,
    name: String,
    description: Option[String],
    legacy: Option[String],
    author: String,
    datetime: String,
    profile: String,
    version: Long,
    deleted: Boolean,
    eventDate: Option[String],
    catalogDate: Option[String],
    status: Option[String]
)

case class Partner(
    id: String,
    name: String,
    description: Option[String],
    legacy: Option[String],
    author: String,
    datetime: String,
    profile: String,
    version: Long,
    deleted: Boolean
)

case class ProductMessage(productId: String, name: String, value: String, profile: String, version: Long)

case class Discount(
    id: String,
    currency: String,
    legacy: Option[String],
    author: String,
    datetime: String,
    profile: String,
    version: Long,
    deleted: Boolean
)

case class OfferProduct(
    offerId: String,
    productId: String,
    priceId: Option[String],
    referenceOffer: Option[String],
    phaseId: Option[String],
    legacy: Option[String],
    skus: Option[String],
    profile: String,
    version: Long
)

case class OfferAttributeValue(offerId: String, key: String, value: String, profile: String, version: Long)

case class CampaignEligibility(
    campaignId: String,
    eligibleCohorts: java.sql.Array,
    ineligibleCohorts: java.sql.Array,
    profile: String,
    setNumber: Int,
    version: Long
)

case class SkuOffer(skuId: String, offerId: String, profile: String, version: Long)
