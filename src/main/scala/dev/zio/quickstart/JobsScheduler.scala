package dev.zio.quickstart

import zio.{Clock, Runtime, Schedule, Unsafe, ZIO}

import java.time.ZoneId
import cron4zio._
import zio._
import zio.logging.backend.SLF4J

trait JobSynchronizer {}
object ZioSchedulerUnsafeFacade {
  private def handleAction[R, E, A](
      jobName: String,
      action: ZIO[R, E, A],
      retryPolicy: Schedule[Any, Any, Any]
  ) =
    ZIO.log(s"scheduled-job-start, jobName: $jobName") *>
      action
        .retry(retryPolicy)
        .zipLeft(ZIO.log(s"scheduled-job-success, jobName: $jobName"))
        .catchAllCause {
          cause =>
            ZIO.foreach(cause.defects) { e =>
              val errorMessage = e.getMessage
              ZIO.logErrorCause(s"scheduled-job-failure, jobName: $jobName, errorMessage: $errorMessage", cause)
            }
        }
        .as(ZIO.unit)

  private def unsafeZioFork[R, E, A](runtime: Runtime[R], zio: ZIO[R, E, A]): Unit =
    Unsafe.unsafe { implicit unsafe: Unsafe =>
      val _ = runtime.unsafe.fork(zio)
    }

  private def scheduleWithSchedule[R, E, A](
      jobName: String,
      action: ZIO[R, E, A],
      schedule: Schedule[Any, Any, Any],
      timezoneClock: Clock,
      retryPolicy: Schedule[Any, Any, Any]
  ) =
    handleAction(jobName, action, retryPolicy)
      .schedule(schedule)
      .withClock(timezoneClock)

  def unsafeRunAsyncScheduledZIO[R, E, A](
      jobName: String,
      runtime: Runtime[R],
      action: ZIO[R, E, A],
      schedule: Schedule[Any, Any, Any],
      timezoneClock: Clock,
      retryPolicy: Schedule[Any, Any, Any] = Schedule.stop // no retry by default
  ): Unit =
    unsafeZioFork(
      runtime,
      scheduleWithSchedule(jobName, action, schedule, timezoneClock, retryPolicy))

  private def scheduleWithCron[R, E, A](
      jobName: String,
      action: ZIO[R, E, A],
      cronString: String,
      zoneId: ZoneId,
      retryPolicy: Schedule[Any, Any, Any]
  ) = repeatEffectForCron(
    handleAction(jobName, action, retryPolicy),
    unsafeParse(cronString),
    zoneId = zoneId
  ).unit

  private def unsafeRunAsyncCronZIO[R, E, A](
      jobName: String,
      runtime: Runtime[R],
      action: ZIO[R, E, A],
      cronString: String,
      zoneId: ZoneId,
      retryPolicy: Schedule[Any, Any, Any] = Schedule.stop // no retry by default
  ): Unit =
    unsafeZioFork(
      runtime,
      scheduleWithCron(jobName, action, cronString, zoneId, retryPolicy)
    )
}
object JobsScheduler extends App {
  private val schedulerZone = ZoneId.of("America/Los_Angeles")
  private val schedulerTZClock = Clock.ClockJava(java.time.Clock.system(schedulerZone))
  private val loggingLayer: ZLayer[Any, Any, Unit] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j(
    format = zio.logging.LogFormat.colored
  )

  private def init(): Unit = {
    val runtime = {
      Unsafe.unsafe { implicit unsafe: Unsafe => Runtime.unsafe.fromLayer(loggingLayer) }
    }
    ZioSchedulerUnsafeFacade.unsafeRunAsyncScheduledZIO(
      "FirstJobService",
      runtime,
      sampleJobA("First job", delay = 5),
      Schedule.spaced(15.seconds),
      schedulerTZClock
    )
    ZioSchedulerUnsafeFacade.unsafeRunAsyncScheduledZIO(
      "SecondJobService",
      runtime,
      sampleJobA("Second job", delay = 20),
      Schedule.spaced(15.seconds),
      schedulerTZClock
    )
    Thread.sleep(30000000)
  }

  private def sampleJobA(message: String, delay: Int): ZIO[Any, Object, Unit] = {
    for {
      _ <- ZIO.logInfo(s"JOB - started: ${message}")
      _ <- ZIO.sleep(delay.seconds)
      _ <- ZIO.logInfo(s"JOB - finished: ${message}")
    } yield ()
  }

  init()
}
