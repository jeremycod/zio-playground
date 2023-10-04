scalaVersion := "2.13.9"
organization := "dev.zio"
name := "zio-playground-hello-world"
val ZioVersion    = "2.0.18"
val ZioLoggingVersion = "2.1.12"
val Slf4jVersion = "2.0.5"
val LogbackVersion = "1.4.5"
val LogbackContribVersion = "0.1.5"
val ScalaLoggingVersion = "3.9.2"
val newtypeVersion          = "0.4.4"
val squantsVersion          = "1.8.3"
//val Cron4ZioVersion = "1.0.0"
val cronUtilsVersion = "9.2.0"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % ZioVersion,
  "dev.zio" %% "zio-logging-slf4j2" % ZioLoggingVersion,
  "dev.zio" %% "zio-streams" % ZioVersion,
  "dev.zio" %% "zio-macros" % ZioVersion,
  "dev.zio" %% "zio-streams" % ZioVersion,
  "dev.zio" %% "zio-test" % ZioVersion % Test,
  "dev.zio" %% "zio-json" % "0.3.0-RC8",
  "dev.zio" %% "zio-interop-reactivestreams" % "1.3.5",
  "com.cronutils" % "cron-utils" % cronUtilsVersion,
 //"com.github.tharwaninitin" %% "cron4zio" % Cron4ZioVersion,
  "dev.zio" %% "zio-prelude" % "1.0.0-RC21",
  "io.estatico"           %% "newtype"     % newtypeVersion,
  "org.typelevel"         %% "squants"     % squantsVersion,
  "org.slf4j" % "slf4j-api" % Slf4jVersion,
  "org.slf4j" % "slf4j-simple" % Slf4jVersion,
  "ch.qos.logback" % "logback-core" % LogbackVersion,
  "ch.qos.logback" % "logback-classic" % LogbackVersion,
  "ch.qos.logback.contrib" % "logback-jackson" % LogbackContribVersion,
  "ch.qos.logback.contrib" % "logback-json-classic" % LogbackContribVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion,
  "eu.timepit" %% "refined" % "0.9.27",
)

