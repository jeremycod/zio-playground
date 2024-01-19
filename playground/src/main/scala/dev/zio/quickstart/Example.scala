package dev.zio.quickstart

import zio._

import scala.concurrent.Future
object Example extends ZIOAppDefault {

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
