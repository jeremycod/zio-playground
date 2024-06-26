package dev.zio.quickstart

import com.cronutils.model.Cron
import com.cronutils.model.definition.{CronConstraintsFactory, CronDefinition, CronDefinitionBuilder}
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import zio.{Duration, IO, Schedule, ZIO}

import java.time.temporal.ChronoUnit
import java.time.{OffsetDateTime, ZoneId, ZoneOffset}
import java.util.TimeZone
import scala.util.Try

// Taken from https://github.com/tharwaninitin/cron4zio and slightly modified
object Cron4ZIO {
  // format: off
  /* Our cron definition uses below cron expressions that go from seconds to day of week in the following order:
   Seconds	0-59	                    - * /
   Minutes	0-59	                    - * /
   Hours	0-23	                      - * /
   Day (of month)	1-31	            * ? / L W
   Month	1-12 or JAN-DEC	            - * /
   Day (of week)	1-7 or SUN-SAT	    - * ? / L #
   Year (optional)	empty, 1970-2099	- * /
 */
  private val cronDefinition: CronDefinition = CronDefinitionBuilder.defineCron()
    .withSeconds().withValidRange(0, 59).and()
    .withMinutes().withValidRange(0, 59).and()
    .withHours().withValidRange(0, 23).and()
    .withDayOfMonth().withValidRange(1, 31).supportsL().supportsW().supportsLW().supportsQuestionMark().and()
    .withMonth().withValidRange(1, 12).and()
    .withDayOfWeek().withValidRange(1, 7).withMondayDoWValue(1).supportsHash().supportsL().supportsQuestionMark().and()
    .withYear().withValidRange(1970, 2099).withStrictRange().optional().and()
    .withCronValidation(CronConstraintsFactory.ensureEitherDayOfWeekOrDayOfMonth())
    .instance()
  // format: on

  private val cronParser: CronParser = new CronParser(cronDefinition)

  /** @param cron
    * @return
    *   Cron instance, which corresponds to cron expression received as String or throws IllegalArgumentException if expression
    *   does not match cron definition
    */
  def unsafeParse(cron: String): Cron = cronParser.parse(cron)

  /** @param cron
    * @return
    *   Cron instance, which corresponds to cron expression received as String or throws IllegalArgumentException if expression
    *   does not match cron definition
    */
  def parse(cron: String): Try[Cron] = Try(cronParser.parse(cron))

  /** @param cron
    * @param zoneId
    * @return
    *   Duration between current time and next run based on cron,zoneId provided or throws IllegalArgumentException if for a valid
    *   cron next run cannot be generated
    */
  private def getNextRunDuration(
      cron: Cron,
      zoneId: ZoneId = ZoneOffset.UTC
  ): IO[IllegalArgumentException, Duration] =
    for {
      timeNow <- ZIO.succeed(OffsetDateTime.now(zoneId).toZonedDateTime)
      timeNext <-
        ZIO
          .attempt(
            ExecutionTime
              .forCron(cron)
              .nextExecution(timeNow)
              .orElseThrow(() =>
                new IllegalArgumentException(s"Cannot generate next run from provided cron => ${cron.asString()}"))
          )
          .refineToOrDie[IllegalArgumentException]
      durationInNanos = timeNow.until(timeNext, ChronoUnit.NANOS)
      duration = Duration.fromNanos(durationInNanos)
    } yield duration

  /** @param cron
    * @param zoneId
    * @return
    *   Effect which will sleep in non-blocking way for duration between current time and next run
    */
  def sleepForCron(
      cron: Cron,
      zoneId: ZoneId
  ): IO[IllegalArgumentException, Unit] =
    getNextRunDuration(cron, zoneId).flatMap(duration => ZIO.sleep(duration))

  /** @param effect
    * @param cron
    * @param maxRecurs
    * @param zoneId
    * @return
    *   This will either return number of times effect ran for cron or will never return anything and keep running forever based
    *   on value of param maxRecurs
    */
  def repeatEffectForCron[R, E >: Throwable, A](
      effect: ZIO[R, E, A],
      cron: Cron,
      maxRecurs: Int = 0,
      zoneId: ZoneId = ZoneOffset.UTC
  ): ZIO[R, E, Long] =
    if (maxRecurs != 0)
      (sleepForCron(cron, zoneId) *> effect).repeat(Schedule.recurs(maxRecurs))
    else
      (sleepForCron(cron, zoneId) *> effect).repeat(Schedule.forever)

  /** @param tasks
    * @param maxRecurs
    * @param zoneId
    * @return
    *   This will either return number of times effects ran for cron or will never return anything and keep running all effects
    *   forever based on value of param maxRecurs
    */
  def repeatEffectsForCron[R, E >: Throwable, A](
      tasks: List[(ZIO[R, E, A], Cron)],
      maxRecurs: Int = 0,
      zoneId: ZoneId = TimeZone.getDefault.toZoneId
  ): ZIO[R, E, List[Long]] =
    ZIO.foreachPar(tasks)(input => repeatEffectForCron(input._1, input._2, maxRecurs, zoneId))
}
