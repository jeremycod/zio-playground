package dev.zio.quickstart

import zio.test._
import zio._

import java.io.IOException
object JobSchedulerSpec extends ZIOSpecDefault {

  def succeedAndAppendJobLog(ref: Ref[Array[String]], jobName: String, delaySeconds: Int): IO[String, String] =
    for {
      _ <- ref.update(_ :+ jobName)
      sleepFiber <- ZIO.sleep(delaySeconds.seconds).fork
      _ <- TestClock.adjust(delaySeconds.seconds)
      _ <- sleepFiber.join
      r <- ZIO.succeed(jobName)
    } yield r

  def spec: Spec[Any, String] = suite("Job Scheduler")(
    test("Validate with promise") {
      for {
        ref <- Ref.make(Array.empty[String])
        _ <- succeedAndAppendJobLog(ref, "test-1", 2)
        _ <- succeedAndAppendJobLog(ref, "test-2", 0)
        readRef <- ref.get
      } yield assertTrue(readRef sameElements Array("test-1", "test-2"))
    }
  )
}
