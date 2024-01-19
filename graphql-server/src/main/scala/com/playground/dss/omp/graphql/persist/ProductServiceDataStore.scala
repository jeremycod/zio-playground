package com.playground.dss.omp.graphql.persist

import com.playground.dss.omp.graphql.{Errors, FixedSnakeCase}
import com.playground.dss.omp.graphql.subgraph.Types.{ID, QueryProductArgs, QueryProductsArgs}
import io.getquill.{Query, Quoted, SnakeCase}
import zio._

import java.sql.SQLException
import com.playground.dss.omp.graphql.table.public
import com.playground.dss.omp.graphql.subgraph.Types.{QueryProductArgs, QueryProductsArgs}
import com.playground.dss.omp.graphql.table.public.ProductWithAttributes
import com.playground.dss.omp.graphql.{table, Errors, FixedSnakeCase}
import io.getquill.jdbczio.Quill

import java.util.UUID

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
      queryProductArgs: QueryProductsArgs
  ): ZIO[Any, SQLException, Seq[ProductWithAttributes]] = {
    val whereClause =
      if (queryProductArgs.productIds.isDefined) {
        val ids = queryProductArgs.productIds.getOrElse(Seq.empty).map(id => s"'$id'").mkString(",")
        s"WHERE profile = '$profile' and id in ($ids)"
      } else
        s"WHERE profile = '$profile'"
    val prodVersionSelector = versionSelector(whereClause)
    run(
      getProducts(prodVersionSelector)
    )
  }

  protected def getProducts(
      versionSelector: String
  ): Quoted[Query[ProductWithAttributes]] = {
    quote {
      sql"""SELECT e.* , pev1.value as event_date, pev2.value as catalog_date, pev3.value as status FROM products as e
         JOIN (#$versionSelector) as vs ON e.id = vs.id and e.profile = vs.profile and e.version = vs.version
         LEFT JOIN product_attribute_values as pev1 ON pev1.product_id = vs.id and pev1.profile = vs.profile and pev1.version = vs.version and pev1.key = 'eventDate'
         LEFT JOIN product_attribute_values as pev2 ON pev2.product_id = vs.id and pev2.profile = vs.profile and pev2.version = vs.version and pev2.key='catalogDate'
         LEFT JOIN product_attribute_values as pev3 ON pev3.product_id = vs.id and pev3.profile = vs.profile and pev3.version = vs.version and pev3.key='status'"""
        .as[Query[ProductWithAttributes]]
    }
  }

  def getProductEntitlements(productId: UUID, profile: String): ZIO[Any, SQLException, Seq[public.Entitlement]] = {
    val entVersionSelector = versionSelectorByEntity("entitlement", s"WHERE profile = '$profile'")
    val prodVersionSelector = versionSelector(s"WHERE profile = '$profile' AND id = '$productId'")
    run(
      quote {
        sql"""SELECT e.* from entitlements as e JOIN(#$entVersionSelector)  as vs ON e.id = vs.id and e.profile = vs.profile and e.version = vs.version
                JOIN product_entitlements pe on e.id = pe.entitlement_id
                  JOIN (#$prodVersionSelector) as pvs ON pe.product_id = pvs.id and pe.profile = pvs.profile and pe.version = pvs.version"""
          .as[Query[public.Entitlement]]
      }
    )
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
            s"Failed to retrieve product ${productId.toString} from DB: ${cause.failureOption.map(
              _.getMessage).getOrElse("")} "))))

  def fetchProducts(
      profile: String,
      queryProductArgs: QueryProductsArgs
  ): ZIO[ProductServiceDataStore, Throwable, Seq[ProductWithAttributes]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.fetchProducts(profile, queryProductArgs))
      .catchAllCause(cause =>
        ZIO.logErrorCause(s"Failed to fetch products, ", cause) *> ZIO.fail(
          Errors.DataAccessErrorMsg(
            s"Failed to retrieve products with arguments: ${queryProductArgs.toString} from DB: ${cause.failureOption.map(_.getMessage).getOrElse(
              "")} ")))

  def getProductEntitlements(
      productId: UUID,
      profile: String
  ): ZIO[ProductServiceDataStore, Throwable, Seq[public.Entitlement]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.getProductEntitlements(productId, profile))

  // TODO: Could be in entitlements data store
  def fetchEntitlementsByNames(
      entNames: List[String],
      profile: String
  ): ZIO[ProductServiceDataStore, Throwable, Seq[public.Entitlement]] =
    ZIO.serviceWithZIO[ProductServiceDataStore](_.fetchEntitlementsByNames(entNames, profile))
      .catchAllCause(cause =>
        ZIO.logErrorCause(s"Failed to retrieve entitlements, ", cause) *> ZIO.fail(
          Errors.DataAccessErrorMsg(
            s"Failed to retrieve entitlements by names: ${entNames.mkString(",")} from DB: ${cause.failureOption.map(_.getMessage).getOrElse(
              "")} ")))

  val layer: ZLayer[Quill.Postgres[SnakeCase.type], Nothing, ProductServiceDataStore] =
    ZLayer.fromFunction(new ProductServiceDataStore(_))
}
