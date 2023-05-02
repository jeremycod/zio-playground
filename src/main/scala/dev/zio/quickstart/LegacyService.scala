package dev.zio.quickstart

import zio._
import zio.logging.backend._

import scala.concurrent.{ExecutionContext, Future}

object LegacyService {
  case class FetchResult(offers: Seq[String])

  sealed trait FinalPriceErrors {
    def message: String
  }

  final case class UnableToFindReferenceOffer(refOfferId: String) extends FinalPriceErrors {
    val message =
      s"Unable to find a reference offer in all the offers with an id of $refOfferId, cannot continue the finalPrice chain"
  }

  trait SdpPublisherFailure {
    def msg: String
  }

  final case class ReferenceOfferChainsBroken(errors: Set[FinalPriceErrors]) extends SdpPublisherFailure {
    val msg =
      s"Unable to find products on reference offers that should have them in the set ${errors.map(_.message).mkString(",\n")}"
  }

  object DataFetcher {
    def fetchAll(profile: String, success: Boolean): Future[FetchResult] = {
      if (success) Future.successful {
        println("FETCH ALL")
        FetchResult(Seq("A", "B", "C"))
      } else {
        println("SHOULD FAIL")
        Future.failed(new RuntimeException("Test exception"))
      }
    }

    def enrichWithPricingInformation(data: FetchResult, success: Boolean): Either[Set[SdpPublisherFailure], FetchResult] = {
      println(s"ENRICH DATA: $data")
      if (success)
      Right(FetchResult(data.offers ++ Seq("E", "F")))
      else Left(Set(ReferenceOfferChainsBroken(Set(UnableToFindReferenceOffer("REF_OFF_ID_1")))))
    }

    def marshal(fetchResult: FetchResult): (
        Seq[String],
        Seq[String],
        Seq[String],
        Seq[String],
        Seq[String],
        Seq[String]
    ) = {
      println(s"MARSHALL DATA: $fetchResult")
      (
        fetchResult.offers ++ Seq("ENT"),
        fetchResult.offers ++ Seq("PRO"),
        fetchResult.offers ++ Seq("OFF"),
        fetchResult.offers ++ Seq("CAMP"),
        fetchResult.offers ++ Seq("DISC"),
        fetchResult.offers ++ Seq("SKU"))
    }

    def produce(
        ent: Seq[String],
        ent2: Seq[String],
        ent3: Seq[String],
        ent4: Seq[String],
        ent5: Seq[String],
        ent6: Seq[String],
        success: Boolean
    ): Future[List[Option[String]]] = {
      if (success)
      Future.successful {
        (ent.map(Some(_)) ++ ent2.map(Some(_)) ++ ent3.map(Some(_)) ++ ent4.map(Some(_)) ++ ent5.map(
          Some(_)) ++ ent6.map(Some(_))).toList
      } else Future.failed(new RuntimeException("Failed Produce"))
    }
  }
  private val retry = Schedule.recurs(5) && Schedule.spaced(5.seconds)
  private val program: ZIO[Any, Object, Unit] = for {
    _ <- ZIO.logInfo("scheduled-job-start: genie-plus-sdp-scheduler")
    data <- ZIO.fromFuture { implicit ec: ExecutionContext => DataFetcher.fetchAll("main", success = false) }
      .retry(retry)
    priceEnrichedData <- ZIO.fromEither(DataFetcher.enrichWithPricingInformation(data, success = true))
    marshalledData <- ZIO.succeed(DataFetcher.marshal(priceEnrichedData))
    publishResult <- ZIO.fromFuture { implicit ec: ExecutionContext =>
                       DataFetcher.produce(
                         marshalledData._1,
                         marshalledData._2,
                         marshalledData._3,
                         marshalledData._4,
                         marshalledData._5,
                         marshalledData._6,
                         success = true)
                     }
    _ <- ZIO.foreachDiscard(publishResult.flatten)(m => ZIO.logInfo(m))
    _ <- ZIO.logInfo(s"Completed! $publishResult")
  } yield ()

  def main(args: Array[String]): Unit = {
    val runtime = {
      val logger = Runtime.removeDefaultLoggers >>> SLF4J.slf4j(
        format = zio.logging.LogFormat.colored
      )
      Unsafe.unsafe { implicit unsafe: Unsafe => Runtime.unsafe.fromLayer(logger) }
    }
    Unsafe.unsafe { implicit unsafe: Unsafe =>
      val _ = runtime.unsafe.fork(
        program
          .retry(Schedule.recurs(3) && Schedule.spaced(10.seconds))
      )
    }
  }

}
