package dev.zio.caliban

import dev.zio.caliban.model._
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.sql.SQLException
import Implicits._

object Implicits {
  implicit val offerCountryCohortEligibilityEncoder: zio.json.JsonEncoder[CountryCohortEligibility] = DeriveJsonEncoder.gen[CountryCohortEligibility]
  implicit val offerCountryCohortEligibilityDecoder: zio.json.JsonDecoder[CountryCohortEligibility] = DeriveJsonDecoder.gen[CountryCohortEligibility]
  implicit val offerEligibilityEncoder: zio.json.JsonEncoder[OfferEligibility] = DeriveJsonEncoder.gen[OfferEligibility]
  implicit val offerEligibilityDecoder: zio.json.JsonDecoder[OfferEligibility] = DeriveJsonDecoder.gen[OfferEligibility]
  implicit val offerTransitionEncoder: zio.json.JsonEncoder[OfferTransition] = DeriveJsonEncoder.gen[OfferTransition]
  implicit val offerTransitionDecoder: zio.json.JsonDecoder[OfferTransition] = DeriveJsonDecoder.gen[OfferTransition]
  implicit val phaseEncoder: zio.json.JsonEncoder[Phase] = DeriveJsonEncoder.gen[Phase]
  implicit val phaseDecoder: zio.json.JsonDecoder[Phase] = DeriveJsonDecoder.gen[Phase]
  implicit val priceEncoder: zio.json.JsonEncoder[Price] = DeriveJsonEncoder.gen[Price]
  implicit val priceDecoder: zio.json.JsonDecoder[Price] = DeriveJsonDecoder.gen[Price]
  //implicit val offerMapProductEncoder: zio.json.JsonEncoder[Map[String, OfferProduct]] = DeriveJsonEncoder.gen[Map[String, OfferProduct]]
  //implicit val offerMapProductDecoder: zio.json.JsonDecoder[Map[String, OfferProduct]] = DeriveJsonDecoder.gen[Map[String, OfferProduct]]
  implicit val offerProductEncoder: zio.json.JsonEncoder[OfferProduct] = DeriveJsonEncoder.gen[OfferProduct]
  implicit val offerProductDecoder: zio.json.JsonDecoder[OfferProduct] = DeriveJsonDecoder.gen[OfferProduct]
  //implicit val mapSetEncoder: zio.json.JsonEncoder[Map[String, String]] = DeriveJsonEncoder.gen[Map[String, String]]
  //implicit val mapSetDecoder: zio.json.JsonDecoder[Map[String, String]] = DeriveJsonDecoder.gen[Map[String, String]]
 //implicit val mapStringEncoder: zio.json.JsonEncoder[Map[String, String]] = DeriveJsonEncoder.gen[Map[String, String]]
  //implicit val mapStringDecoder: zio.json.JsonDecoder[Map[String, String]] = DeriveJsonDecoder.gen[Map[String, String]]
  //implicit val mapDecoder = QuillJson.jsonDecoder[Map[String, String]]
  //implicit val mapEncoder = QuillJson.jsonEncoder[Map[String, String]]
  implicit val mapSetEncoder: zio.json.JsonEncoder[Map[String, Set[String]]] = zio.json.Encoder.map[String, Set[String]]
  implicit val mapSetDecoder: zio.json.JsonDecoder[Map[String, Set[String]]] = zio.json.JsonDecoder.map[String, Set[String]]
  implicit val mapEncoder: zio.json.JsonEncoder[Map[String, String]] = zio.json.JsonEncoder.map[String, String]
  implicit val mapDecoder: zio.json.JsonDecoder[Map[String, String]] = zio.json.JsonDecoder.map[String, String]
  implicit val offerEncoder: zio.json.JsonEncoder[Offer] = DeriveJsonEncoder.gen[Offer]
  implicit val offerDecoder: zio.json.JsonDecoder[Offer] = DeriveJsonDecoder.gen[Offer]
}



class OfferService2(quill: Quill.Postgres[SnakeCase]) {
  import quill._

  //implicit val mapEncoder: zio.json.JsonEncoder[Map[String, String]] = zio.json.JsonEncoder.map[String, String]
  //implicit val mapDecoder: zio.json.JsonDecoder[Map[String, String]] = zio.json.JsonDecoder.map[String, String]
  def list: ZIO[Any, SQLException, Seq[Offer]] = run(query[Offer])
}
object OfferService2 {
  def getOffers: ZIO[OfferService2, SQLException, Seq[Offer]] =
    ZIO.serviceWithZIO[OfferService2](_.list)

  val live = ZLayer.fromFunction(new OfferService2(_))
}

/*trait DataService[T] {

  def list(profile: String):ZIO[Any, SQLException, Seq[Offer]]

}
class OfferService(quill: Quill.Postgres[SnakeCase]) {
  import quill._

  def list: ZIO[Any, SQLException, Seq[Offer]] = run(query[Offer])
}
object OfferService {

  def getOffers: ZIO[OfferService, SQLException, Seq[Offer]] = ZIO.serviceWith[OfferService](_.list)

  val live: ZLayer[Quill.Postgres[SnakeCase], Nothing, OfferService] = ZLayer.fromFunction(new OfferService(_))
}*/


