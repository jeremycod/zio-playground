package dev.zio.quickstart

import zio._

import scala.concurrent.Future
object Example2 extends scala.App {

  private val program =
    for {
      _ <- ZIO.fromFuture { _ =>
        Future.failed(new RuntimeException("test"))
      }
      _ <- ZIO.fromFuture { _ =>
        Future.failed(new RuntimeException("test 2"))
      }
    } yield ()

  val schedule =
    Schedule.spaced(30.second) && Schedule.duration(5.minutes)

  Unsafe.unsafe { implicit unsafe =>
    Runtime.default.unsafe.run {
      program.debug("program").retry(schedule)
    }
  }
}
