package dev.zio.caliban.subgraph

//import com.disney.dss.omp.genie.models.enums.ProductEntityTypes.ProductEntityType
//import com.disney.dss.omp.genie.models.enums.ProductEntityTypes
//import com.disney.dss.omp.graphql.Queries.Env
//import com.disney.dss.omp.graphql.resolver.GetProduct
import dev.zio.caliban.Queries.Env
import dev.zio.caliban.model.ProductEntityType
import dev.zio.caliban.resolver.GetProduct
import io.circe.parser.parse
import zio.query.ZQuery
//import zio.query.ZQuery

import java.util.UUID
import dev.zio.caliban.table

final case class Product(
                          id: UUID,
                          legacyId: Option[String],
                          name: String,
                          description: Option[String],
                          `type`: ProductEntityType,
                          createdBy: String,
                          createdDate: String,
                          updatedBy: Option[String],
                          updatedDate: Option[String],
                          entitlements: ZQuery[Env, Throwable, Set[Entitlement]]
                        )

object Product {

  def fromLegacyOptionString(legacy: Option[String]): Map[String, String] =
    legacy.map(fromLegacyString).getOrElse(Map.empty)

  def fromLegacyString(legacy: String): Map[String, String] =
    parse(legacy).flatMap(_.as[Map[String, String]]).toOption
      .getOrElse(Map.empty)
  def fromTable(r: table.Product): Product = {
    val legacyMap = fromLegacyString(r.legacy)
    val productId = UUID.fromString(r.id)
    Product(
      id = productId,
      legacyId = legacyMap.get("disney_legacy_id"),
      name = r.name,
      description = r.description,
      `type` = ProductEntityType.findByName(legacyMap.getOrElse("disney_legacy_id", "UNKNOWN")),
      createdBy = r.author,
      createdDate = r.datetime,
      updatedBy = Some(r.author),
      updatedDate = Some(r.datetime),
      entitlements = GetProduct.getProductEntitlements(productId, r.profile)
    )
  }
}