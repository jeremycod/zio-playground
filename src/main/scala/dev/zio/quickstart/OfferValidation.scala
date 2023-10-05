package dev.zio.quickstart

import dev.zio.quickstart.OfferValidation.validateOffer3
import zio.{NonEmptyChunk, Unsafe, ZIO}
import zio.prelude.Validation

case class Product(name: String, id: String, price: Int)
case class Offer(name: String, id: String, products: Seq[Product])

object OfferValidation extends App {
  def getProducts(start: Int) = (start to start + 3).map(ind => Product(s"name $ind", s"id $ind", ind * 10))

  def getOffers(start: Int) =
    (start to start + 5).map(ind => Offer(s"offer name $ind", s"offer id $ind", getProducts(ind)))

  private val incorrectOffer_1 = Offer(
    "test",
    "id 1",
    products = Seq(Product("a", "a1", 30))
  )
  private val incorrectOffer_2 = Offer(
    "test2",
    "id 2",
    products = Seq(Product("a", "a1", -330))
  )
  private val incorrectOffers = Seq(incorrectOffer_1, incorrectOffer_2)
  val offers = getOffers(1) ++ incorrectOffers

  offers.foreach { o =>
    val result = validateOffer3(o)
    result.fold(
      failures => {
        val f = failures.mkString(", ")
        println(s"Offer failed: $f")
      },
      _ => {})
  }

  val program = ZIO.foreachParDiscard(offers) { offer =>
    val result = validateOffer2(offer)
    result.fold(
      failures => {
        val f = failures.mkString(", ")
        ZIO.logError(s"ZIO Offer ${offer.id} failed: $f")
      },
      _ => ZIO.unit)
  }
  Unsafe.unsafe { implicit u: Unsafe => zio.Runtime.default.unsafe.run(program) }

  private def validateOffer(offer: Offer): Validation[String, Offer] =
    Validation.validateWith(validateOfferName(offer), validateOfferPrice(offer))((o, _) => o)
  private def validateOffer2(offer: Offer): Validation[String, List[Offer]] =
    Validation.validateAll(List(validateOfferName(offer), validateOfferPrice(offer)))
  private def validateOffer3(offer: Offer): Validation[String, Offer] = for {
    a <- validateOfferName(offer)
    r <- validateOfferPrice(offer)
  } yield r
  private def validateOfferName(offer: Offer): Validation[String, Offer] =
    if (offer.name.length > 5)
      Validation.succeed(offer)
    else Validation.fail(s"Incorrect offer name ${offer.name} in offer ${offer.id}")

  private def validateOfferPrice(offer: Offer): Validation[String, Offer] = {
    val offerPrice = offer.products.map(_.price).sum

    if (offerPrice > 0)
      Validation.succeed(offer)
    else Validation.fail(s"Incorrect offer price ${offer.name} price: ${offerPrice}")
  }

}
