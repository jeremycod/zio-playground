package com.playground.dss.omp.graphql.table

import com.playground.dss.omp.graphql.table.public.{
  Campaign,
  CampaignAttributeKey,
  CampaignAttributeValue,
  CampaignEligibility,
  CampaignId,
  CampaignMessage,
  CampaignOffer,
  Discount,
  DiscountAmount,
  DiscountAttributeKey,
  DiscountAttributeValue,
  DiscountId,
  DiscountMessage,
  Entitlement,
  EntitlementAttributeKey,
  EntitlementAttributeValue,
  EntitlementId,
  MessageType,
  MetadataType,
  Offer,
  OfferAttributeKey,
  OfferAttributeValue,
  OfferId,
  OfferMessage,
  OfferMetadata,
  OfferPhase,
  OfferProduct,
  OfferTransition,
  Package,
  PackageId,
  PackageProduct,
  Partner,
  PartnerAttributeKey,
  PartnerAttributeValue,
  PartnerId,
  Price,
  Product,
  ProductAttributeKey,
  ProductAttributeValue,
  ProductDependency,
  ProductEntitlement,
  ProductId,
  ProductMessage,
  ProductMetadata,
  Sku,
  SkuAttributeValue,
  SkuCampaign,
  SkuId,
  SkuOffer,
  SkuProduct,
  SubPartner,
  SubPartnerAttributeKey,
  SubPartnerAttributeValue,
  SubPartnerId
}

trait PublicExtensions[+Idiom <: io.getquill.idiom.Idiom, +Naming <: io.getquill.NamingStrategy] {
  this: io.getquill.context.Context[Idiom, Naming] =>

  object PublicSchema {
    object OfferTransitionDao {
      def query = quote {
        querySchema[OfferTransition](
          "public.offer_transitions",
          _.offerId -> "offer_id",
          _.toOfferId -> "to_offer_id",
          _.cohort -> "cohort",
          _.startdate -> "startDate",
          _.profile -> "profile",
          _.version -> "version",
          _.eligibility -> "eligibility",
          _.reason -> "reason"
        )

      }

    }

    object CampaignIdDao {
      def query = quote {
        querySchema[CampaignId](
          "public.campaign_ids",
          _.id -> "id",
          _.profile -> "profile"
        )

      }

    }

    object ProductAttributeValueDao {
      def query = quote {
        querySchema[ProductAttributeValue](
          "public.product_attribute_values",
          _.productId -> "product_id",
          _.key -> "key",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object SkuCampaignDao {
      def query = quote {
        querySchema[SkuCampaign](
          "public.sku_campaigns",
          _.skuId -> "sku_id",
          _.campaignId -> "campaign_id",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object SubPartnerAttributeValueDao {
      def query = quote {
        querySchema[SubPartnerAttributeValue](
          "public.sub_partner_attribute_values",
          _.subPartnerId -> "sub_partner_id",
          _.key -> "key",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object MessageTypeDao {
      def query = quote {
        querySchema[MessageType](
          "public.message_types",
          _.name -> "name",
          _.`type` -> "type"
        )

      }

    }

    object PartnerAttributeValueDao {
      def query = quote {
        querySchema[PartnerAttributeValue](
          "public.partner_attribute_values",
          _.partnerId -> "partner_id",
          _.key -> "key",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object DiscountMessageDao {
      def query = quote {
        querySchema[DiscountMessage](
          "public.discount_messages",
          _.discountId -> "discount_id",
          _.name -> "name",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object DiscountAttributeKeyDao {
      def query = quote {
        querySchema[DiscountAttributeKey](
          "public.discount_attribute_keys",
          _.key -> "key"
        )

      }

    }

    object SubPartnerAttributeKeyDao {
      def query = quote {
        querySchema[SubPartnerAttributeKey](
          "public.sub_partner_attribute_keys",
          _.key -> "key"
        )

      }

    }

    object CampaignAttributeValueDao {
      def query = quote {
        querySchema[CampaignAttributeValue](
          "public.campaign_attribute_values",
          _.campaignId -> "campaign_id",
          _.key -> "key",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object PackageDao {
      def query = quote {
        querySchema[Package](
          "public.packages",
          _.id -> "id",
          _.name -> "name",
          _.description -> "description",
          _.legacy -> "legacy",
          _.author -> "author",
          _.datetime -> "datetime",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object SubPartnerDao {
      def query = quote {
        querySchema[SubPartner](
          "public.sub_partners",
          _.id -> "id",
          _.name -> "name",
          _.displayname -> "displayName",
          _.partnerId -> "partner_id",
          _.legacy -> "legacy",
          _.author -> "author",
          _.datetime -> "datetime",
          _.profile -> "profile",
          _.version -> "version",
          _.deleted -> "deleted"
        )

      }

    }

    object PriceDao {
      def query = quote {
        querySchema[Price](
          "public.prices",
          _.id -> "id",
          _.amount -> "amount",
          _.currency -> "currency",
          _.productId -> "product_id",
          _.active -> "active",
          _.profile -> "profile"
        )

      }

    }

    object DiscountAmountDao {
      def query = quote {
        querySchema[DiscountAmount](
          "public.discount_amounts",
          _.discountId -> "discount_id",
          _.productId -> "product_id",
          _.amount -> "amount",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object OfferDao {
      def query = quote {
        querySchema[Offer](
          "public.offers",
          _.id -> "id",
          _.name -> "name",
          _.description -> "description",
          _.discountId -> "discount_id",
          _.legacy -> "legacy",
          _.author -> "author",
          _.datetime -> "datetime",
          _.profile -> "profile",
          _.version -> "version",
          _.deleted -> "deleted"
        )

      }

    }

    object ProductEntitlementDao {
      def query = quote {
        querySchema[ProductEntitlement](
          "public.product_entitlements",
          _.productId -> "product_id",
          _.entitlementId -> "entitlement_id",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object OfferMetadataDao {
      def query = quote {
        querySchema[OfferMetadata](
          "public.offer_metadata",
          _.offerId -> "offer_id",
          _.name -> "name",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object PartnerIdDao {
      def query = quote {
        querySchema[PartnerId](
          "public.partner_ids",
          _.id -> "id",
          _.profile -> "profile"
        )

      }

    }

    object SkuIdDao {
      def query = quote {
        querySchema[SkuId](
          "public.sku_ids",
          _.id -> "id",
          _.profile -> "profile"
        )

      }

    }

    object ProductAttributeKeyDao {
      def query = quote {
        querySchema[ProductAttributeKey](
          "public.product_attribute_keys",
          _.key -> "key"
        )

      }

    }

    object PackageIdDao {
      def query = quote {
        querySchema[PackageId](
          "public.package_ids",
          _.id -> "id",
          _.profile -> "profile"
        )

      }

    }

    object SkuAttributeValueDao {
      def query = quote {
        querySchema[SkuAttributeValue](
          "public.sku_attribute_values",
          _.skuId -> "sku_id",
          _.key -> "key",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object OfferPhaseDao {
      def query = quote {
        querySchema[OfferPhase](
          "public.offer_phases",
          _.offerId -> "offer_id",
          _.id -> "id",
          _.durationLength -> "duration_length",
          _.durationUnit -> "duration_unit",
          _.repeatCount -> "repeat_count",
          _.endDate -> "end_date",
          _.paymentType -> "payment_type",
          _.discountId -> "discount_id",
          _.nextPhaseId -> "next_phase_id",
          _.profile -> "profile",
          _.version -> "version",
          _.legacy -> "legacy"
        )

      }

    }

    object SkuProductDao {
      def query = quote {
        querySchema[SkuProduct](
          "public.sku_products",
          _.skuId -> "sku_id",
          _.productId -> "product_id",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object SubPartnerIdDao {
      def query = quote {
        querySchema[SubPartnerId](
          "public.sub_partner_ids",
          _.id -> "id",
          _.profile -> "profile"
        )

      }

    }

    object ProductDependencyDao {
      def query = quote {
        querySchema[ProductDependency](
          "public.product_dependencies",
          _.productId -> "product_id",
          _.parentProductId -> "parent_product_id",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object DiscountIdDao {
      def query = quote {
        querySchema[DiscountId](
          "public.discount_ids",
          _.id -> "id",
          _.profile -> "profile"
        )

      }

    }

    object OfferIdDao {
      def query = quote {
        querySchema[OfferId](
          "public.offer_ids",
          _.id -> "id",
          _.profile -> "profile"
        )

      }

    }

    object CampaignOfferDao {
      def query = quote {
        querySchema[CampaignOffer](
          "public.campaign_offers",
          _.campaignId -> "campaign_id",
          _.offerId -> "offer_id",
          _.legacy -> "legacy",
          _.profile -> "profile",
          _.version -> "version",
          _.setNumber -> "set_number"
        )

      }

    }

    object PackageProductDao {
      def query = quote {
        querySchema[PackageProduct](
          "public.package_products",
          _.packageId -> "package_id",
          _.productId -> "product_id",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object OfferAttributeKeyDao {
      def query = quote {
        querySchema[OfferAttributeKey](
          "public.offer_attribute_keys",
          _.key -> "key"
        )

      }

    }

    object EntitlementAttributeValueDao {
      def query = quote {
        querySchema[EntitlementAttributeValue](
          "public.entitlement_attribute_values",
          _.entitlementId -> "entitlement_id",
          _.key -> "key",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object EntitlementDao {
      def query = quote {
        querySchema[Entitlement](
          "public.entitlements",
          _.id -> "id",
          _.name -> "name",
          _.description -> "description",
          _.legacy -> "legacy",
          _.author -> "author",
          _.datetime -> "datetime",
          _.profile -> "profile",
          _.version -> "version",
          _.deleted -> "deleted"
        )

      }

    }

    object ProductMetadataDao {
      def query = quote {
        querySchema[ProductMetadata](
          "public.product_metadata",
          _.productId -> "product_id",
          _.name -> "name",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object MetadataTypeDao {
      def query = quote {
        querySchema[MetadataType](
          "public.metadata_types",
          _.name -> "name",
          _.`type` -> "type"
        )

      }

    }

    object OfferMessageDao {
      def query = quote {
        querySchema[OfferMessage](
          "public.offer_messages",
          _.offerId -> "offer_id",
          _.name -> "name",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object PartnerAttributeKeyDao {
      def query = quote {
        querySchema[PartnerAttributeKey](
          "public.partner_attribute_keys",
          _.key -> "key"
        )

      }

    }

    object EntitlementAttributeKeyDao {
      def query = quote {
        querySchema[EntitlementAttributeKey](
          "public.entitlement_attribute_keys",
          _.key -> "key"
        )

      }

    }

    object SkuDao {
      def query = quote {
        querySchema[Sku](
          "public.skus",
          _.id -> "id",
          _.name -> "name",
          _.description -> "description",
          _.platform -> "platform",
          _.countries -> "countries",
          _.author -> "author",
          _.datetime -> "datetime",
          _.profile -> "profile",
          _.version -> "version",
          _.deleted -> "deleted",
          _.legacy -> "legacy"
        )

      }

    }

    object DiscountAttributeValueDao {
      def query = quote {
        querySchema[DiscountAttributeValue](
          "public.discount_attribute_values",
          _.discountId -> "discount_id",
          _.key -> "key",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object CampaignMessageDao {
      def query = quote {
        querySchema[CampaignMessage](
          "public.campaign_messages",
          _.campaignId -> "campaign_id",
          _.name -> "name",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object CampaignAttributeKeyDao {
      def query = quote {
        querySchema[CampaignAttributeKey](
          "public.campaign_attribute_keys",
          _.key -> "key"
        )

      }

    }

    object ProductIdDao {
      def query = quote {
        querySchema[ProductId](
          "public.product_ids",
          _.id -> "id",
          _.profile -> "profile"
        )

      }

    }

    object EntitlementIdDao {
      def query = quote {
        querySchema[EntitlementId](
          "public.entitlement_ids",
          _.id -> "id",
          _.profile -> "profile"
        )

      }

    }

    object CampaignDao {
      def query = quote {
        querySchema[Campaign](
          "public.campaigns",
          _.id -> "id",
          _.name -> "name",
          _.description -> "description",
          _.partnerId -> "partner_id",
          _.legacy -> "legacy",
          _.author -> "author",
          _.datetime -> "datetime",
          _.profile -> "profile",
          _.version -> "version",
          _.deleted -> "deleted"
        )

      }

    }

    object ProductDao {
      def query = quote {
        querySchema[Product](
          "public.products",
          _.id -> "id",
          _.name -> "name",
          _.description -> "description",
          _.legacy -> "legacy",
          _.author -> "author",
          _.datetime -> "datetime",
          _.profile -> "profile",
          _.version -> "version",
          _.deleted -> "deleted"
        )

      }

    }

    object PartnerDao {
      def query = quote {
        querySchema[Partner](
          "public.partners",
          _.id -> "id",
          _.name -> "name",
          _.description -> "description",
          _.legacy -> "legacy",
          _.author -> "author",
          _.datetime -> "datetime",
          _.profile -> "profile",
          _.version -> "version",
          _.deleted -> "deleted"
        )

      }

    }

    object ProductMessageDao {
      def query = quote {
        querySchema[ProductMessage](
          "public.product_messages",
          _.productId -> "product_id",
          _.name -> "name",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object DiscountDao {
      def query = quote {
        querySchema[Discount](
          "public.discounts",
          _.id -> "id",
          _.currency -> "currency",
          _.legacy -> "legacy",
          _.author -> "author",
          _.datetime -> "datetime",
          _.profile -> "profile",
          _.version -> "version",
          _.deleted -> "deleted"
        )

      }

    }

    object OfferProductDao {
      def query = quote {
        querySchema[OfferProduct](
          "public.offer_products",
          _.offerId -> "offer_id",
          _.productId -> "product_id",
          _.priceId -> "price_id",
          _.referenceOffer -> "reference_offer",
          _.phaseId -> "phase_id",
          _.legacy -> "legacy",
          _.skus -> "skus",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object OfferAttributeValueDao {
      def query = quote {
        querySchema[OfferAttributeValue](
          "public.offer_attribute_values",
          _.offerId -> "offer_id",
          _.key -> "key",
          _.value -> "value",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }

    object CampaignEligibilityDao {
      def query = quote {
        querySchema[CampaignEligibility](
          "public.campaign_eligibility",
          _.campaignId -> "campaign_id",
          _.eligibleCohorts -> "eligible_cohorts",
          _.ineligibleCohorts -> "ineligible_cohorts",
          _.profile -> "profile",
          _.setNumber -> "set_number",
          _.version -> "version"
        )

      }

    }

    object SkuOfferDao {
      def query = quote {
        querySchema[SkuOffer](
          "public.sku_offers",
          _.skuId -> "sku_id",
          _.offerId -> "offer_id",
          _.profile -> "profile",
          _.version -> "version"
        )

      }

    }
  }
}
