package dev.zio.caliban

import dev.zio.caliban.model._
import io.getquill.{Query, SnakeCase}
import io.getquill.jdbczio.Quill
import zio._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}

import java.sql.SQLException
import Implicits._

import dev.zio.caliban.Model._

object Implicits {
  // implicit val mapSetEncoder: zio.json.JsonEncoder[Map[String, Set[String]]] = zio.json.JsonEncoder.map[String, Set[String]]
  //implicit val mapSetDecoder: zio.json.JsonDecoder[Map[String, Set[String]]] = zio.json.JsonDecoder.map[String, Set[String]]

/*  implicit val offerCountryCohortEligibilityEncoder: zio.json.JsonEncoder[CountryCohortEligibility] = DeriveJsonEncoder.gen[CountryCohortEligibility]
  implicit val offerCountryCohortEligibilityDecoder: zio.json.JsonDecoder[CountryCohortEligibility] = DeriveJsonDecoder.gen[CountryCohortEligibility]
  implicit val offerEligibilityEncoder: zio.json.JsonEncoder[OfferEligibility] = DeriveJsonEncoder.gen[OfferEligibility]
  implicit val offerEligibilityDecoder: zio.json.JsonDecoder[OfferEligibility] = DeriveJsonDecoder.gen[OfferEligibility]
  implicit val offerTransitionEncoder: zio.json.JsonEncoder[OfferTransition] = DeriveJsonEncoder.gen[OfferTransition]
  implicit val offerTransitionDecoder: zio.json.JsonDecoder[OfferTransition] = DeriveJsonDecoder.gen[OfferTransition]
  implicit val phaseEncoder: zio.json.JsonEncoder[Phase] = DeriveJsonEncoder.gen[Phase]
  implicit val phaseDecoder: zio.json.JsonDecoder[Phase] = DeriveJsonDecoder.gen[Phase]
  implicit val priceEncoder: zio.json.JsonEncoder[Price] = DeriveJsonEncoder.gen[Price]
  implicit val priceDecoder: zio.json.JsonDecoder[Price] = DeriveJsonDecoder.gen[Price]
  implicit val offerProductEncoder =  DeriveJsonEncoder.gen[OfferProduct]
  implicit val offerProductDecoder = DeriveJsonDecoder.gen[OfferProduct]*/
  /**/

  //implicit val mapSetEncoder: zio.json.JsonEncoder[Map[String, String]] = DeriveJsonEncoder.gen[Map[String, String]]
  //implicit val mapSetDecoder: zio.json.JsonDecoder[Map[String, String]] = DeriveJsonDecoder.gen[Map[String, String]]
  //implicit val mapStringEncoder: zio.json.JsonEncoder[Map[String, String]] = DeriveJsonEncoder.gen[Map[String, String]]
  //implicit val mapStringDecoder: zio.json.JsonDecoder[Map[String, String]] = DeriveJsonDecoder.gen[Map[String, String]]
  implicit val mapDecoder = QuillJson.jsonDecoder[Map[String, String]]
  implicit val mapEncoder = QuillJson.jsonEncoder[Map[String, String]]
/* implicit val mapEncoder: zio.json.JsonEncoder[Map[String, String]] =
  zio.json.JsonEncoder.map[String, String]
  implicit val mapDecoder: zio.json.JsonDecoder[Map[String, String]] =
    zio.json.JsonDecoder.map[String, String]*/
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



  private def getOffers(entityName: String, versionSelector: String) = {
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
   run(getOffers("offer", version))
 }

}
object OfferServiceDataStore {
  def getOffers: ZIO[OfferServiceDataStore, SQLException, Seq[Offer]] =
    ZIO.serviceWithZIO[OfferServiceDataStore](_.fetchOffers)

  val layer = ZLayer.fromFunction(new OfferServiceDataStore(_))
}
