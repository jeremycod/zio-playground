package dev.zio.quickstart

import zio.{Clock, Runtime, Schedule, Unsafe, ZIO}

import java.time.ZoneId
import cron4zio._
import dev.zio.quickstart.ZioSchedulerUnsafeFacade.unsafeZioFork
import zio._
import zio.logging.backend.SLF4J

case class ScheduledJob(jobName: String, action: ZIO[Any, Any, Any], retryPolicy: Schedule[Any, Any, Any])

object JobSynchronizer {
  def make: UIO[JobSynchronizer] = for {
    q <- Queue.bounded[ScheduledJob](20)
    jobSync = new JobSynchronizer(q)
  } yield jobSync

  def handleAction(
      jobName: String,
      action: ZIO[Any, Any, Any],
      retryPolicy: Schedule[Any, Any, Any]
  ): ZIO[Any, Nothing, UIO[Unit]] =
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

  def consumeScheduledJobs(jobSynchronizer: JobSynchronizer): ZIO[Any, Nothing, Unit] = {
    println(s"consume scheduled jobs")
    val j = for {
      _ <- ZIO.log(s"started consuming jobs")
      //jobsSync <- jobSynchronizer
      queue = jobSynchronizer.jobsQueue
      size <- queue.size
      _ <- ZIO.log(s"jobs in queue ${size.toString}")
      job <- queue.take
      _ <- ZIO.log(s"scheduled-job-executing, jobName: ${job.jobName}")
      _ <- handleAction(job.jobName, job.action, job.retryPolicy)
    } yield ()
    j.forever
  }
}

class JobSynchronizer(val jobsQueue: Queue[ScheduledJob]) {
  def offer(scheduledJob: ScheduledJob): UIO[Boolean] = jobsQueue.offer(scheduledJob)

}

object ZioSchedulerUnsafeFacade extends App {

  private def sendJobToQueue(
      jobName: String,
      jobSynchronizer: JobSynchronizer,
      action: ZIO[Any, Any, Any],
      retryPolicy: Schedule[Any, Any, Any]
  ) = {
    for {
      _ <- ZIO.log(s"scheduled-job-in-queue, jobName: $jobName")
      //jobSync <- jobSynchronizer
      _ <- jobSynchronizer.offer(ScheduledJob(jobName, action, retryPolicy))
      size <- jobSynchronizer.jobsQueue.size
      _ <- ZIO.log(s"scheduled-job-placed-in-queue, jobName: $jobName, ${size.toString}")
    } yield ()
  }

  def unsafeZioFork(runtime: Runtime[Any], zio: ZIO[Any, Any, Any]): Unit =
    Unsafe.unsafe { implicit unsafe: Unsafe =>
      val _ = runtime.unsafe.fork(zio)
    }

  private def scheduleWithSchedule(
      jobName: String,
      jobSynchronizer: JobSynchronizer,
      action: ZIO[Any, Any, Any],
      schedule: Schedule[Any, Any, Any],
      timezoneClock: Clock,
      retryPolicy: Schedule[Any, Any, Any]
  ) =
    sendJobToQueue(jobName, jobSynchronizer, action, retryPolicy)
      .schedule(schedule)
      .withClock(timezoneClock)

  def unsafeRunAsyncScheduledZIO(
      jobName: String,
      runtime: Runtime[Any],
      jobSynchronizer: JobSynchronizer,
      action: ZIO[Any, Any, Any],
      schedule: Schedule[Any, Any, Any],
      timezoneClock: Clock,
      retryPolicy: Schedule[Any, Any, Any] = Schedule.stop // no retry by default
  ): Unit =
    unsafeZioFork(
      runtime,
      scheduleWithSchedule(jobName, jobSynchronizer, action, schedule, timezoneClock, retryPolicy))

  private def scheduleWithCron(
      jobName: String,
      action: ZIO[Any, Any, Any],
      cronString: String,
      zoneId: ZoneId,
      retryPolicy: Schedule[Any, Any, Any]
  ) = repeatEffectForCron(
    JobSynchronizer.handleAction(jobName, action, retryPolicy),
    unsafeParse(cronString),
    zoneId = zoneId
  ).unit

  private def unsafeRunAsyncCronZIO(
      jobName: String,
      runtime: Runtime[Any],
      action: ZIO[Any, Any, Any],
      cronString: String,
      zoneId: ZoneId,
      retryPolicy: Schedule[Any, Any, Any] = Schedule.stop // no retry by default
  ): Unit = {
    unsafeZioFork(
      runtime,
      scheduleWithCron(jobName, action, cronString, zoneId, retryPolicy)
    )
  }
}

object JobsScheduler extends App {
  private val schedulerZone = ZoneId.of("America/Los_Angeles")
  private val schedulerTZClock = Clock.ClockJava(java.time.Clock.system(schedulerZone))
  private val loggingLayer: ZLayer[Any, Any, Unit] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j(
    format = zio.logging.LogFormat.colored
  )
  private val jobSynchronizer = JobSynchronizer.make
  val runtime = {
    Unsafe.unsafe { implicit unsafe: Unsafe => Runtime.unsafe.fromLayer(loggingLayer) }
  }
  private def init(jobSynchronizer: JobSynchronizer): Unit = {

    ZioSchedulerUnsafeFacade.unsafeRunAsyncScheduledZIO(
      "FirstJobService",
      runtime,
      jobSynchronizer,
      sampleJobA("First job", delay = 5),
      Schedule.spaced(15.seconds),
      schedulerTZClock
    )
    ZioSchedulerUnsafeFacade.unsafeRunAsyncScheduledZIO(
      "SecondJobService",
      runtime,
      jobSynchronizer,
      sampleJobA("Second job", delay = 20),
      Schedule.spaced(15.seconds),
      schedulerTZClock
    )
    unsafeZioFork(runtime, JobSynchronizer.consumeScheduledJobs(jobSynchronizer))

    Thread.sleep(30000000)
  }

  private def sampleJobA(message: String, delay: Int): ZIO[Any, Object, Unit] = {
    for {
      _ <- ZIO.logInfo(s"JOB - started: ${message}")
      _ <- ZIO.sleep(delay.seconds)
      _ <- ZIO.logInfo(s"JOB - finished: ${message}")
    } yield ()
  }
  def program = for {
    jobSync <- jobSynchronizer
    _ = init(jobSync)
  } yield ()
  unsafeZioFork(runtime, program)

}
