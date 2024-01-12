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

  def createProduct(
      p: public.Product,
      attributes: Map[String, String],
      entitlements: List[String],
      newEntitlements: List[public.Entitlement]
  ): ZIO[ProductServiceWriteDataStore, Throwable, public.Product] = {

    for {
      _ <- run(quote {
             liftQuery(newEntitlements.map(e => public.EntitlementId(e.id, e.profile))).foreach(c => {
               query[public.EntitlementId].insertValue(c).returning(r => r)
             })
           })
      insertedEntitlements <- run(quote {
                                liftQuery(newEntitlements)
                                  .foreach(e =>
                                    query[public.Entitlement].insertValue(e)
                                      .returning(r => r))
                              })

      entitlementsIds <- ZIO.succeed(entitlements ++ insertedEntitlements.map(_.id))

      _ <- run(query[public.ProductId].insertValue(lift(public.ProductId(p.id, p.profile))))
      savedProduct <- run {
                        query[public.Product]
                          .insertValue(lift(p))
                          .returning(r => r)
                      }
      _ <- run {
             quote {
               liftQuery(attributes.map {
                 case (key, value) =>
                   ProductAttributeValue(savedProduct.id, key, value, savedProduct.profile, savedProduct.version)
               }.toList).foreach(pe =>
                 query[public.ProductAttributeValue].insertValue(pe).returning(r => r))
             }
           }
      _ <- run {
             quote {
               liftQuery(entitlementsIds.map(e =>
                 public.ProductEntitlement(savedProduct.id, e, savedProduct.profile, savedProduct.version))).foreach(
                 pe =>
                   query[public.ProductEntitlement].insertValue(pe).returning(r => r))
             }
           }
    } yield savedProduct
  }
}
object ProductServiceWriteDataStore {

  // TODO: We are creating entitlements if it doesn't exist
  // Maybe this is not correct and we should fail if entitlements do not exists
  def createProduct(
      productToCreate: public.Product,
      attributes: Map[String, String],
      existingEntitlements: List[String],
      newEntitlements: List[public.Entitlement]
  ): ZIO[ProductServiceWriteDataStore, Throwable, public.Product] =
    ZIO.serviceWithZIO[ProductServiceWriteDataStore](_.createProduct(
      productToCreate,
      attributes,
      existingEntitlements,
      newEntitlements)
      .catchAllCause(cause =>
        ZIO.logErrorCause(s"Failed to create product, ", cause) *> ZIO.fail(
          Errors.DataAccessErrorMsg(
            s"Failed to create product from DB: ${
                cause.failureOption.map(
                  _.getMessage).getOrElse("")
              } "))))

  val layer: ZLayer[Quill.Postgres[SnakeCase.type], Nothing, ProductServiceWriteDataStore] =
    ZLayer.fromFunction(new ProductServiceWriteDataStore(_))

}
