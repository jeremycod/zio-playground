package dev.zio.quickstart

import zio._

object ZIODependencies extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = ???
}
