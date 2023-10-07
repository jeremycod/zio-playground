package dev.zio.caliban

import zio._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}

import java.sql.SQLException


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
  implicit val offerProductEncoder: zio.json.JsonEncoder[OfferProduct] = DeriveJsonEncoder.gen[OfferProduct]
  implicit val offerProductDecoder: zio.json.JsonDecoder[OfferProduct] = DeriveJsonDecoder.gen[OfferProduct]
  implicit val offerMapProductEncoder: zio.json.JsonEncoder[Map[String, OfferProduct]] = DeriveJsonEncoder.gen[Map[String, OfferProduct]]
  implicit val offerMapProductDecoder: zio.json.JsonDecoder[Map[String, OfferProduct]] = DeriveJsonDecoder.gen[Map[String, OfferProduct]]
  implicit val offerEncoder: zio.json.JsonEncoder[Offer] = DeriveJsonEncoder.gen[Offer]
  implicit val offerDecoder: zio.json.JsonDecoder[Offer] = DeriveJsonDecoder.gen[Offer]
}



class OfferService2(quill: Quill.Postgres[SnakeCase]) {
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


