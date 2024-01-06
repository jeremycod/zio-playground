package dev.zio.quickstart

import zio.{Random, Schedule, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object SimpleSchedules extends ZIOAppDefault {

  val aZIO = Random.nextBoolean.flatMap { flag =>
    if (flag) ZIO.succeed("fetched value").debugThread
    else ZIO.succeed("failure...").debugThread *> ZIO.fail("error")
  }
  val successZIO = ZIO.succeed("TEST")
  val testSchedule = Schedule.minuteOfHour(12) && Schedule.minuteOfHour(13)
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = successZIO.schedule(testSchedule)

}
