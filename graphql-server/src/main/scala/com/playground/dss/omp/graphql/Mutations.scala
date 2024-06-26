package com.playground.dss.omp.graphql

import com.playground.dss.omp.graphql.Queries.Env
import com.playground.dss.omp.graphql.persist.ProductServiceDataStore
import com.playground.dss.omp.graphql.security.Auth
import com.playground.dss.omp.graphql.services.ProductService
import com.playground.dss.omp.graphql.subgraph.Types._
import zio._
final case class Mutations(
                            /* createOfferD2C: MutationCreateOfferD2CArgs => zio.UIO[scala.Option[Offer]],
                            updateOfferD2C: MutationUpdateOfferD2CArgs => zio.UIO[scala.Option[Offer]],
                            createOfferIAP: MutationCreateOfferIAPArgs => zio.UIO[scala.Option[Offer]],
                            updateOfferIAP: MutationUpdateOfferIAPArgs => zio.UIO[scala.Option[Offer]],
                            updateOfferStatus: MutationUpdateOfferStatusArgs => zio.UIO[scala.Option[Offer]],
                            updateOfferAccounting: MutationUpdateOfferAccountingArgs => zio.UIO[scala.Option[Offer]],*/
                            createBaseProduct: MutationCreateBaseProductArgs => ZIO[Env, Throwable, scala.Option[Product]],
                            updateBaseProduct: MutationUpdateBaseProductArgs => ZIO[Env, Throwable, scala.Option[Product]],
                            createOneTimePurchaseProduct: MutationCreateOneTimePurchaseProductArgs => ZIO[
                              Env,
                              Throwable,
                              scala.Option[Product]],
                            updateOneTimePurchaseProduct: MutationUpdateOneTimePurchaseProductArgs => ZIO[
                              Env,
                              Throwable,
                              scala.Option[Product]],
                            updateProductStatus: MutationUpdateProductStatusArgs => ZIO[Env, Throwable, scala.Option[Product]],
                            createEntitlement: MutationCreateEntitlementArgs => ZIO[Env, Throwable, scala.Option[Entitlement]]
                            /*createCampaignD2C: MutationCreateCampaignD2CArgs => zio.UIO[scala.Option[CampaignD2C]],
                            updateCampaignD2C: MutationUpdateCampaignD2CArgs => zio.UIO[scala.Option[CampaignD2C]],
                            createCampaignIAP: MutationCreateCampaignIAPArgs => zio.UIO[scala.Option[CampaignIAP]],
                            updateCampaignIAP: MutationUpdateCampaignIAPArgs => zio.UIO[scala.Option[CampaignIAP]],
                            updateCampaignStatus: MutationUpdateCampaignStatusArgs => zio.UIO[scala.Option[Campaign]],
                            updateCampaignReportAsPaid: MutationUpdateCampaignReportAsPaidArgs => zio.UIO[scala.Option[CampaignD2C]],
                            updatePartner: MutationUpdatePartnerArgs => zio.UIO[Partner],
                            createPartner: MutationCreatePartnerArgs => zio.UIO[Partner],
                            createCohort: MutationCreateCohortArgs => zio.UIO[Cohort],
                            updateCohort: MutationUpdateCohortArgs => zio.UIO[Cohort],
                            createPriceChangeEvent: MutationCreatePriceChangeEventArgs => zio.UIO[scala.Option[PriceChangeEvent]],
                            createPriceChangeCohorts: MutationCreatePriceChangeCohortsArgs => zio.UIO[scala.Option[PriceChangeEvent]],
                            createAndUpdatePriceChangeEventCohort: MutationCreateAndUpdatePriceChangeEventCohortArgs => zio.UIO[EventCohort],
                            analyzePriceChangeEvent: MutationAnalyzePriceChangeEventArgs => zio.UIO[scala.Option[PriceChangeEvent]],
                            validatePriceChangeEvent: MutationValidatePriceChangeEventArgs => zio.UIO[scala.Option[PriceChangeEvent]],
                            submitEventApproval: MutationSubmitEventApprovalArgs => zio.UIO[scala.Option[PriceChangeEvent]],
                            triggerPricingScheduleMessagingSync: zio.UIO[scala.Option[SyncEvent]],
                            createPriceChangeOffers: MutationCreatePriceChangeOffersArgs => zio.UIO[scala.Option[PriceChangeEvent]],
                            createSku: MutationCreateSkuArgs => zio.UIO[SKU],
                            updateSku: MutationUpdateSkuArgs => zio.UIO[SKU],
                            createRedemptionCodes: MutationCreateRedemptionCodesArgs => zio.UIO[CodeGenTask]*/
)
object Mutations {
  val live: Mutations = Mutations(
    args => ProductService.createBaseProduct(args.product),
    args => ProductService.updateBaseProduct(args.id, args.product),
    args => ProductService.createOneTimePurchaseProduct(args.product),
    args => ProductService.updateOneTimePurchaseProduct(args.id, args.product),
    args => ProductService.updateProductStatus(args.id, args.status),
    args => ProductService.createEntitlement(args.entitlement)
  )
}
