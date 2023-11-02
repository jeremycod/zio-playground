package dev.zio.caliban.persist

import dev.zio.caliban.QuillJson
import dev.zio.caliban.table._
import io.getquill.jdbczio.Quill
import io.getquill.{Query, SnakeCase}
import zio._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}

object Implicits {
  implicit val mapDecoder = QuillJson.jsonDecoder[Map[String, String]]
  implicit val mapEncoder = QuillJson.jsonEncoder[Map[String, String]]
  implicit val mEcnoder = QuillJson.jsonEncoder[Map[String, String]]
  implicit val mDecoder = QuillJson.jsonDecoder[Map[String, String]]
  implicit val attributeEncoder: zio.json.JsonEncoder[Attribute] = DeriveJsonEncoder.gen[Attribute]
  implicit val attributeDecoder: zio.json.JsonDecoder[Attribute] = DeriveJsonDecoder.gen[Attribute]
  implicit val offerEncoder: zio.json.JsonEncoder[Offer] = DeriveJsonEncoder.gen[Offer]
  implicit val offerDecoder: zio.json.JsonDecoder[Offer] = DeriveJsonDecoder.gen[Offer]

}


class OfferServiceDataStore(val quill: Quill.Postgres[SnakeCase]) extends DataStoreService {
  import quill._



  private def getOffers(entityName: String, versionSelector: String) = {
    quote {
      sql"""SELECT e.* FROM #${entityName}s as e
           JOIN (#$versionSelector) as vs ON e.id = vs.id and e.profile = vs.profile and e.version = vs.version"""
        .as[Query[Offer]]
    }
  }

  private def getAttributes(entityName: String, versionSelector: String) = {
    quote {
      sql"""
           SELECT v.key, v.value
                 FROM (#$versionSelector) as vs
                 JOIN #${entityName}_attribute_values as v on vs.id = v.#${entityName}_id and vs.profile = v.profile and vs.version = v.version"""
        .as[Query[Attribute]]

    }
  }

  private def getReferenceOffers(versionSelector: String, offerBasePrice: Int, currencyCode: String, countryCode: String) = {
    quote {
      sql"""SELECT o.*, oav.value as currencyCode, oav2.value as countries from offers as o
           JOIN(#$versionSelector) as vs ON o.id = vs.id and o.profile = vs.profile and o.version = vs.version
           JOIN offer_attribute_values oav on o.id = oav.offer_id and oav.version = vs.version
           JOIN offer_attribute_values oav2 on o.id = oav2.offer_id and oav2.version = vs.version
           JOIN offer_attribute_values oav3 on o.id = oav3.offer_id and oav3.version = vs.version
           WHERE oav.key = 'currencyCode' AND oav.value='#${currencyCode}'
             AND oav2.key = 'offer_base_price'  AND oav2.value='#${offerBasePrice.toString}'
             AND oav3.key = 'countries' and oav3.value = '#${countryCode}'"""
        .as[Query[Offer]]
    }
  }

 def fetchOffers: ZIO[Any, Throwable, Seq[Offer]] = {
   //val version = profileVersionSelector("offer", "main")
   val version = versionSelector("offer", "where profile = 'main' and author='transform'")
   run(getOffers("offer", version))
 }

  def fetchAttributes(offerId: String): ZIO[Any, Throwable, Seq[Attribute]] = {
    //val version = profileVersionSelector("offer", "main")
    val version = versionSelector("offer", s"where profile = 'main' and author='transform' and id='${offerId}'")
    run(getAttributes("offer", version))
  }

  def fetchReferenceOffersCandidates(offerBasePrice: Int, currencyCode: String, countryCode: String): ZIO[Any, Throwable, Seq[Offer]] = {
    val version = versionSelector("offer", "where profile = 'main' and author='transform'")
    run(getReferenceOffers( version, offerBasePrice, currencyCode, countryCode))
  }

}
object OfferServiceDataStore {
  def getOffers: ZIO[OfferServiceDataStore, Throwable, Seq[Offer]] =
    ZIO.serviceWithZIO[OfferServiceDataStore](_.fetchOffers)

  def getReferenceOfferCandidates(offerId: String, productId: String, offerBasePrice: Int, currencyCode: String, countryCode: String): ZIO[OfferServiceDataStore, Throwable, Seq[Offer]] =
    ZIO.serviceWithZIO[OfferServiceDataStore](_.fetchReferenceOffersCandidates(offerBasePrice, currencyCode, countryCode))

  def getOfferAttributes(offerId: String): ZIO[OfferServiceDataStore, Throwable, Seq[Attribute]] =
    ZIO.serviceWithZIO[OfferServiceDataStore](_.fetchAttributes(offerId))

  val layer = ZLayer.fromFunction(new OfferServiceDataStore(_))
}
