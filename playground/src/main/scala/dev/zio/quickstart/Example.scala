package dev.zio.quickstart

import zio._

import scala.concurrent.Future
object Example extends ZIOAppDefault {

  // function to calculate average price from list of Products
  def averagePrice(products: Seq[Product]): Double = {
    products.map(_.price).sum / products.length
  }
  // average price of offer
  def averagePrice(offer: Offer): Double = {
    averagePrice(offer.products)
  }

  // validate that offer product  have reference offers

// get a list of all offers from repository  having author as "transform"


  private val program =
    for {
      _ <- ZIO.fromFuture { _ => Future.failed(new RuntimeException("test")) }
      _ <- ZIO.fromFuture { _ => Future.failed(new RuntimeException("test 2")) }
    } yield ()

  val schedule =
    Schedule.recurs(3) && Schedule.spaced(1.second)

  val run =
    program.debug("program").retry(schedule)
}
