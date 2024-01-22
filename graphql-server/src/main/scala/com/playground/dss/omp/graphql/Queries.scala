package com.playground.dss.omp.graphql

import com.playground.dss.omp.graphql.Queries.Env
import com.playground.dss.omp.graphql.persist.{ProductServiceDataStore, ProductServiceWriteDataStore}
import com.playground.dss.omp.graphql.resolver.GetProduct
import com.playground.dss.omp.graphql.security.Auth
import com.playground.dss.omp.graphql.services.ProductService
import com.playground.dss.omp.graphql.subgraph.Types._
import zio.query.ZQuery
import zio._

final case class Queries(
    // TODO: Temporary commenting out all queries that are not implemented yet
    /* offer: QueryOfferArgs => zio.UIO[scala.Option[Offer]],
    offers: QueryOffersArgs => zio.UIO[List[Offer]],
    referenceOffers: QueryReferenceOffersArgs => zio.UIO[List[Offer]],*/
    product: QueryProductArgs => ZQuery[Env, Throwable, scala.Option[Product]],
    products: QueryProductsArgs => ZQuery[Env, Throwable, Seq[Product]]
    /* entitlements: zio.UIO[List[Entitlement]],
    entitlementsFromEDC: QueryEntitlementsFromEDCArgs => zio.UIO[List[EntitlementFromEDC]],
    entitlementFromEDC: QueryEntitlementFromEDCArgs => zio.UIO[scala.Option[EntitlementFromEDC]],
    campaign: QueryCampaignArgs => zio.UIO[scala.Option[Campaign]],
    campaigns: zio.UIO[List[Campaign]],
    partner: QueryPartnerArgs => zio.UIO[scala.Option[Partner]],
    partners: zio.UIO[List[Partner]],
    subpartner: QuerySubpartnerArgs => zio.UIO[scala.Option[Subpartner]],
    subpartners: zio.UIO[List[Subpartner]],
    cohortResolvers: QueryCohortResolversArgs => zio.UIO[List[Resolver]],
    cohorts: QueryCohortsArgs => zio.UIO[List[Cohort]],
    cohort: QueryCohortArgs => zio.UIO[Cohort],
    `package`: QueryPackageArgs => zio.UIO[scala.Option[Package]],
    packages: zio.UIO[List[Package]],
    countries: zio.UIO[List[Country]],
    currencies: zio.UIO[List[Currency]],
    priceChangeEvent: QueryPriceChangeEventArgs => zio.UIO[scala.Option[PriceChangeEvent]],
    priceChangeEvents: zio.UIO[List[PriceChangeEvent]],
    eventCohort: QueryEventCohortArgs => zio.UIO[scala.Option[EventCohort]],
    eventCohorts: QueryEventCohortsArgs => zio.UIO[List[EventCohort]],
    messagingConfigs: zio.UIO[List[MessagingConfig]],
    vanellopeGatewayPayload: QueryVanellopeGatewayPayloadArgs => zio.UIO[String],
    configServicePayload: QueryConfigServicePayloadArgs => zio.UIO[String],
    sku: QuerySkuArgs => zio.UIO[scala.Option[SKU]],
    skus: zio.UIO[List[SKU]],
    redemptionCodes: QueryRedemptionCodesArgs => zio.UIO[scala.Option[String]],
    redemptionCodeTasks: QueryRedemptionCodeTasksArgs => zio.UIO[List[CodeGenTask]],
    redemptionCodeInfo: QueryRedemptionCodeInfoArgs => zio.UIO[RedemptionCodeResponse],
    tierDefinitions: zio.UIO[List[TierDefinition]],
    tierDefinition: QueryTierDefinitionArgs => zio.UIO[scala.Option[TierDefinition]]*/
)
object Queries {
  type Env = Auth with ProductServiceDataStore with ProductServiceWriteDataStore with ProductService
  val live: Queries = Queries(
    args => GetProduct.getProduct(args),
    args => GetProduct.getAllProducts(args.types.getOrElse(List.empty), args.productIds.getOrElse(List.empty))
  )

}
