package com.playground.dss.omp.graphql.persist

import com.playground.dss.omp.graphql.Errors
import com.playground.dss.omp.graphql.table.public.ProductAttributeValue
import com.playground.dss.omp.graphql.table.public
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZLayer}

@SuppressWarnings(Array("org.wartremover.warts.All"))
class ProductServiceWriteDataStore(override val quill: Quill.Postgres[SnakeCase.type])
  extends DataStoreService[Product](quill) {

  import quill._

  implicit val productInsertMeta = insertMeta[public.Product](_.version)
  implicit val productEntitlementSchema = schemaMeta[public.ProductEntitlement]("product_entitlements")
  implicit val entitlementInsertMeta = insertMeta[public.Entitlement](_.version)

  override val entityName: String = "product"

  private def createProductId(
                               id: String,
                               profile: String
                             ): ZIO[ProductServiceWriteDataStore, Throwable, public.ProductId] =
    run {
      query[public.ProductId]
        .insertValue(lift(public.ProductId(id, profile)))
        .returning(r => r)
    }

  private def createEntitlementId(id: String, profile: String) =
    run {
      query[public.EntitlementId]
        .insertValue(lift(public.EntitlementId(id, profile)))
        .returning(r => r)
    }

  def createProduct(
                     p: public.Product,
                     attributes: Map[String, String],
                     entitlements: List[String]
                   ): ZIO[ProductServiceWriteDataStore, Throwable, public.Product] = {

    for {

      _ <- run(query[public.ProductId].filter(pid => pid.id == lift(p.id) && pid.profile == lift(p.profile)))
        .map(_.headOption)
        .someOrElseZIO(
          createProductId(p.id, p.profile)
        )

      savedProduct <- run {
        query[public.Product]
          .insertValue(lift(p))
          .returning(r => r)
      }
      _ <- run {
        quote {
          liftQuery(attributes.map {
            case (key, value) =>
              public.ProductAttributeValue(savedProduct.id, key, value, savedProduct.profile, savedProduct.version)
          }.toList).foreach(pe =>
            query[public.ProductAttributeValue].insertValue(pe).returning(r => r))
        }
      }
      _ <- run {
        quote {
          liftQuery(entitlements.map(e =>
            public.ProductEntitlement(savedProduct.id, e, savedProduct.profile, savedProduct.version))).foreach(
            pe =>
              query[public.ProductEntitlement].insertValue(pe).returning(r => r))
        }
      }
    } yield savedProduct
  }

  def createEntitlement(
                         entitlement: public.Entitlement
                       ): ZIO[ProductServiceWriteDataStore, Throwable, public.Entitlement] = {
    for {
      _ <- run(query[public.EntitlementId].filter(eid =>
        eid.id == lift(entitlement.id) && eid.profile == lift(entitlement.profile)))
        .map(_.headOption)
        .someOrElseZIO(
          createEntitlementId(entitlement.id, entitlement.profile)
        )

      insertedEntitlement <- run(query[public.Entitlement]
        .insertValue(lift(entitlement)).returning(e => e))
    } yield insertedEntitlement
  }
}
object ProductServiceWriteDataStore {

  // TODO: We are creating entitlements if it doesn't exist
  // Maybe this is not correct and we should fail if entitlements do not exists
  def createProduct(
                     productToCreate: public.Product,
                     attributes: Map[String, String],
                     existingEntitlements: List[String]
                   ): ZIO[ProductServiceWriteDataStore, Throwable, public.Product] =
    ZIO.serviceWithZIO[ProductServiceWriteDataStore](_.createProduct(
      productToCreate,
      attributes,
      existingEntitlements)
      .catchAllCause(cause =>
        ZIO.logErrorCause(s"Failed to create product, ", cause) *> ZIO.fail(
          Errors.DataAccessErrorMsg(
            Errors.ErrorCode.DATA_WRITE_ERROR,
            s"Failed to create product from DB: ${
              cause.failureOption.map(
                _.getMessage).getOrElse("")
            } "))))

  def createEntitlement(entitlement: public.Entitlement)
  : ZIO[ProductServiceWriteDataStore, Throwable, public.Entitlement] =
    ZIO.serviceWithZIO[ProductServiceWriteDataStore](_.createEntitlement(
      entitlement
    ))

  val layer: ZLayer[Quill.Postgres[SnakeCase.type], Nothing, ProductServiceWriteDataStore] =
    ZLayer.fromFunction(new ProductServiceWriteDataStore(_))

}
