package dev.zio.quickstart

import zio._

import java.io.IOException

object LayersExample extends ZIOAppDefault {

  lazy val makeWithConstructors = {
    ZIO.succeed {
      ControllerImpl.make(BoundaryImpl.make(GoogleImpl.make), Console.ConsoleLive)
    }
  }
  lazy val make =
    ZIO
      .service[Controller]
      .provideLayer(
        ZLayer.succeed(ControllerImpl.make(
          BoundaryImpl.make(GoogleImpl.make),
          Console.ConsoleLive
        ))

        // ZLayer.Debug.mermaid,
      )

  def runController(): ZIO[Controller, IOException, Unit] =
    for {
      cont <- ZIO.service[Controller]
      _ <- cont.run
    } yield ()

  val consoleLayer: ZLayer[Any, Nothing, Console] = ZLayer.succeed(Console.ConsoleLive)
  val googleLayer: ZLayer[Any, Nothing, Google] = ZLayer.succeed(GoogleImpl.make)
  val boundaryLayer: ZLayer[Google, Nothing, Boundary] = ZLayer.fromFunction(BoundaryImpl.make(_))
  val controllerLayer: ZLayer[Boundary with Console, Nothing, Controller] = ZLayer.fromFunction(ControllerImpl.make _)

  // composing layers
  // vertical composition
  val boundaryLayerWithConsole: ZLayer[Any, Any, Boundary] = googleLayer >>> boundaryLayer

  // horizontal composition
  val c: ZLayer[Any with Google, Nothing, Console with Boundary] = consoleLayer ++ boundaryLayer

  //val mix: ZLayer[Any, Any, Controller with Boundary with Console] = controllerLayer ++ boundaryLayer ++ consoleLayer

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    runController()
      .provide(ZLayer.succeed(
        ControllerImpl.make(
          BoundaryImpl.make(GoogleImpl.make),
          Console.ConsoleLive
        )
      ))
      .exitCode
}
