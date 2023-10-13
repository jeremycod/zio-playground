package dev.zio.caliban

import dev.zio.caliban.model._
import io.getquill.{Query, SnakeCase}
import io.getquill.jdbczio.Quill
import zio._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}

import java.sql.SQLException
import Implicits._
import caliban.{GraphQL, RootResolver}
import caliban.GraphQL.graphQL

object Implicits {
  implicit val mEcnoder = QuillJson.jsonEncoder[Map[String, String]]
  implicit val mDecoder = QuillJson.jsonDecoder[Map[String, String]]
  implicit val offerEncoder: zio.json.JsonEncoder[Offer] = DeriveJsonEncoder.gen[Offer]
  implicit val offerDecoder: zio.json.JsonDecoder[Offer] = DeriveJsonDecoder.gen[Offer]
}


class OfferServiceDataStore(val quill: Quill.Postgres[SnakeCase]) {
  import quill._

  protected def profileVersionSelector(entityName: String, profile: String) =
    s"""
       SELECT id, profile, version
       FROM (
         SELECT id, profile, deleted, version, row_number() OVER (PARTITION BY id, profile ORDER BY version DESC) as rn
         FROM ${entityName}s
       ) as t WHERE rn = 1 and deleted = false and profile = '$profile'"""



  private def getMainEntities(entityName: String, versionSelector: String) = {
    quote {
      sql"""SELECT e.* FROM #${entityName}s as e
           JOIN (#$versionSelector) as vs ON e.id = vs.id and e.profile = vs.profile and e.version = vs.version"""
        .as[Query[Offer]]
    }
  }
 //val queryOffers: Quoted[EntityQuery[Offer]] = quote(querySchema[Offer](entity = "offers"))
 // val profileVersionSql = quote { querySchema[Offer](entity = "offers")}
 /* val queryOffersSql: Quoted[Query[(String, String, Long)]] =
    quote { querySchema[Offer](entity = "offers").map(p => profileVersionSelector("offer", "main"))}
*/  def fetchOffers: ZIO[Any, SQLException, Seq[Offer]] = {
   val version = profileVersionSelector("offer", "main")
   run(getMainEntities("offer", version))
 }

}
object OfferServiceDataStore {
  def getOffers: ZIO[OfferServiceDataStore, SQLException, Seq[Offer]] =
    ZIO.serviceWithZIO[OfferServiceDataStore](_.fetchOffers)

  val layer = ZLayer.fromFunction(new OfferServiceDataStore(_))
}
