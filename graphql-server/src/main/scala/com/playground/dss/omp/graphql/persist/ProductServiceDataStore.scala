package com.playground.dss.omp.graphql.persist

import com.playground.dss.omp.graphql.{Errors, FixedSnakeCase}
import com.playground.dss.omp.graphql.subgraph.Types.{ID, QueryProductArgs, QueryProductsArgs}
import io.getquill.{Query, Quoted, SnakeCase}
import zio._

import java.sql.SQLException
import com.playground.dss.omp.graphql.table.public
import com.playground.dss.omp.graphql.subgraph.Types.{QueryProductArgs, QueryProductsArgs}
import com.playground.dss.omp.graphql.table.public.{Entitlement, ProductAttributeValue, ProductWithAttributes}
import com.playground.dss.omp.graphql.{table, Errors, FixedSnakeCase}
import io.getquill.jdbczio.Quill

import java.util.UUID
import scala.collection.immutable.List

@SuppressWarnings(Array("org.wartremover.warts.All"))
class ProductServiceDataStore(override val quill: Quill.Postgres[SnakeCase.type])
    extends DataStoreService[Product](quill) {
  import quill._
  override val entityName: String = "product"

  def fetchEntitlementsByNames(
      entNames: List[String],
      profile: String
  ): ZIO[ProductServiceDataStore, Throwable, Seq[public.Entitlement]] = {
    run {
      for {
        entVersion <-
          query[public.Entitlement].filter(e => liftQuery(entNames).contains(e.name) && e.profile == lift(profile))
            .groupByMap(p => (p.id, p.profile))(p => (p.id, p.profile, max(p.version)))
        ent <- query[public.Entitlement].join(e =>
                 e.id == entVersion._1 && e.profile == entVersion._2 && e.version == entVersion._3)
      } yield ent
    }
  }

  def fetchProduct(
      profile: String,
      id: ID
  ): ZIO[Any, SQLException, Option[ProductWithAttributes]] = {

    run {
      for {
        prodVersion <-
          query[public.Product].filter(p => p.id == lift(id.toString) && p.profile == lift(profile))
            .groupByMap(p => (p.id, p.profile))(p => (p.id, p.profile, max(p.version)))
        product <-
          query[public.Product].join(p =>
            p.id == prodVersion._1 && p.profile == prodVersion._2 && p.version == prodVersion._3)

        evDate <-
          query[public.ProductAttributeValue].leftJoin(pav =>
            pav.productId == prodVersion._1 && pav.profile == prodVersion._2 && pav.version == prodVersion._3 && pav.key == "eventDate")
        cDate <-
          query[public.ProductAttributeValue].leftJoin(pav =>
            pav.productId == prodVersion._1 && pav.profile == prodVersion._2 && pav.version == prodVersion._3 && pav.key == "catalogDate")
        st <-
          query[public.ProductAttributeValue].leftJoin(pav =>
            pav.productId == prodVersion._1 && pav.profile == prodVersion._2 && pav.version == prodVersion._3 && pav.key == "status")

      } yield ProductWithAttributes(
        id = product.id,
        name = product.name,
        description = product.description,
        legacy = product.legacy,
        author = product.author,
        datetime = product.datetime,
        profile = product.profile,
        version = product.version,
        deleted = product.deleted,
        eventDate = evDate.map(_.value),
        catalogDate = cDate.map(_.value),
        status = st.map(_.value)
      )

    }.map(_.headOption)
  }

  def fetchProducts(
      profile: String,
      productIds: List[String],
      allProducts: Boolean
  ): ZIO[Any, SQLException, Seq[ProductWithAttributes]] = {
    run {
      for {
        prodVersion <- query[public.Product].filter(p =>
                         (lift(allProducts) || liftQuery(productIds).contains(p.id)) && p.profile == lift(profile))
                         .groupByMap(p => (p.id, p.profile))(p => (p.id, p.profile, max(p.version)))
        product <-
          query[public.Product].join(p =>
            p.id == prodVersion._1 && p.profile == prodVersion._2 && p.version == prodVersion._3)
        evDate <-
          query[ProductAttributeValue].leftJoin(pav =>
            pav.productId == prodVersion._1 && pav.profile == prodVersion._2 && pav.version == prodVersion._3 && pav.key == "eventDate")
        cDate <-
          query[ProductAttributeValue].leftJoin(pav =>
            pav.productId == prodVersion._1 && pav.profile == prodVersion._2 && pav.version == prodVersion._3 && pav.key == "catalogDate")
        st <-
          query[ProductAttributeValue].leftJoin(pav =>
            pav.productId == prodVersion._1 && pav.profile == prodVersion._2 && pav.version == prodVersion._3 && pav.key == "status")
      } yield ProductWithAttributes(
        id = product.id,
        name = product.name,
        description = product.description,
        legacy = product.legacy,
        author = product.author,
        datetime = product.datetime,
        profile = product.profile,
        version = product.version,
        deleted = product.deleted,
        eventDate = evDate.map(_.value),
        catalogDate = cDate.map(_.value),
        status = st.map(_.value)
      )
    }
  }

  def getProductEntitlements(
      productId: UUID,
      profile: String,
      prodVersion: Long
  ): ZIO[Any, SQLException, Seq[Entitlement]] = {
    run {
      for {
        entVersion <-
          query[Entitlement].filter(e => e.profile == lift(profile))
            .groupByMap(p => (p.id, p.profile))(p => (p.id, p.profile, max(p.version)))
        entitlement <-
          query[Entitlement].join(e =>
            e.id == entVersion._1 && e.profile == entVersion._2 && e.version == entVersion._3)
        _ <- query[public.ProductEntitlement].join(ent =>
               ent.productId == lift(productId.toString) && ent.profile == lift(profile) && ent.version == lift(
                 prodVersion) && entitlement.id == ent.entitlementId)

      } yield entitlement
    }
  }
}

object ProductServiceDataStore {

  def fetchProduct(
      profile: String,
      productId: ID
  ): ZIO[ProductServiceDataStore, Throwable, Option[ProductWithAttributes]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.fetchProduct(profile, productId)
      .catchAllCause(cause =>
        ZIO.logErrorCause(s"Failed to retrieve product ${productId.toString}, ", cause) *> ZIO.fail(
          Errors.DataAccessErrorMsg(
            Errors.ErrorCode.DATA_ACCESS_ERROR,
            s"Failed to retrieve product ${productId.toString} from DB: ${cause.failureOption.map(
              _.getMessage).getOrElse("")} "
          ))))

  def fetchProducts(
      profile: String,
      productIds: List[ID]
  ): ZIO[ProductServiceDataStore, Throwable, Seq[ProductWithAttributes]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.fetchProducts(
      profile,
      productIds.map(_.toString),
      productIds.isEmpty))
      .catchAllCause(cause =>
        ZIO.logErrorCause(s"Failed to fetch products, ", cause) *> ZIO.fail(
          Errors.DataAccessErrorMsg(
            Errors.ErrorCode.DATA_ACCESS_ERROR,
            s"Failed to retrieve products with arguments: ${productIds.mkString(",")} from DB: ${cause.failureOption.map(_.getMessage).getOrElse(
              "")} "
          )))

  def getProductEntitlements(
      productId: UUID,
      profile: String,
      productVersion: Long
  ): ZIO[ProductServiceDataStore, Throwable, Seq[public.Entitlement]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.getProductEntitlements(productId, profile, productVersion))

  // TODO: Could be in entitlements data store
  def fetchEntitlementsByNames(
      entNames: List[String],
      profile: String
  ): ZIO[ProductServiceDataStore, Throwable, Seq[Entitlement]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.fetchEntitlementsByNames(entNames, profile))
      .catchAllCause(cause =>
        ZIO.logErrorCause(s"Failed to retrieve entitlements, ", cause) *> ZIO.fail(
          Errors.DataAccessErrorMsg(
            Errors.ErrorCode.DATA_ACCESS_ERROR,
            s"Failed to retrieve entitlements by names: ${entNames.mkString(",")} from DB: ${cause.failureOption.map(_.getMessage).getOrElse(
              "")} "
          )))

  val layer: ZLayer[Quill.Postgres[SnakeCase.type], Nothing, ProductServiceDataStore] =
    ZLayer.fromFunction(new ProductServiceDataStore(_))
}
