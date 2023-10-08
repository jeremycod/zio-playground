package dev.zio.caliban

import caliban.CalibanError.ValidationError
import zio.{IO, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object PocMain extends ZIOAppDefault {

  val program: IO[ValidationError, Unit] = ??? //for {
 //   offers <- offerService.list("main")

//  } yield ()
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = program
}
