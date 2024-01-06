package dev.zio.caliban.resolver

import dev.zio.caliban.persist.OfferServiceDataStore
import dev.zio.caliban.subgraph.{OfferView, ReferenceOfferView}
import dev.zio.caliban.transformer.TiaraOfferTransformer
import zio.ZIO
import zio.query.ZQuery

object GetOffer {

  def fetchAllOffers(profile: String) = {
    val value = ZQuery.fromZIO(OfferServiceDataStore.getOffers)
      .map(_.map { offer =>
        val oView = OfferView.fromTable(offer)
        oView
      })

    value

  }

  protected def combine(a: Map[String, Set[String]], b: Map[String, Set[String]]): Map[String, Set[String]] = {
    val keys = a.keySet ++ b.keySet
    keys.map(k => k -> (a.getOrElse(k, Set.empty) ++ b.getOrElse(k, Set.empty))).toMap
  }

  def getAttributes(offerId: String): ZQuery[OfferServiceDataStore, Throwable, Map[String, String]] = {
    val value = ZQuery.fromZIO(OfferServiceDataStore.getOfferAttributes(offerId))
    val att = value.map(_.map(a => (a.key, a.value)).toMap)
    att
  }

  def getReferenceOffer(
      offerId: String,
      productId: String,
      offerBasePrice: Int,
      currencyCode: String,
      countryCode: String
  ): ZQuery[OfferServiceDataStore, Throwable, Option[ReferenceOfferView]] = {
    val value = ZQuery.fromZIO(OfferServiceDataStore.getReferenceOfferCandidates(offerId, productId, offerBasePrice, currencyCode, countryCode)
      .map(_.map { offer =>
        val oView = ReferenceOfferView.fromTable(offer)
        oView
      }))
    val selectedReferenceOffer = for {
      s <- value
      res = s.headOption
      _ <- ZQuery.fromZIO(ZIO.logInfo(s"GET REFERENCE OFFER OFFER: $offerId product: $productId base price: $offerBasePrice currency: $currencyCode RES is defined ${res.map(r => r.id).getOrElse("NONE")} out of: ${s.map(_.id).mkString(",")}"))
    } yield res
    selectedReferenceOffer
  }

}
