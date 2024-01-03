import _root_.caliban.tools.Codegen
scalaVersion := "2.13.9"
organization := "dev.zio"
name := "zio-playground-hello-world"
val ZioVersion = "2.0.18"
val ZioLoggingVersion = "2.1.12"
val Slf4jVersion = "2.0.5"
val LogbackVersion = "1.4.5"
val LogbackContribVersion = "0.1.5"
val ScalaLoggingVersion = "3.9.2"
val newtypeVersion = "0.4.4"
val squantsVersion = "1.8.3"
val CirceVersion = "0.14.3"
val quillVersion = "4.8.0"
//val Cron4ZioVersion = "1.0.0"
val cronUtilsVersion = "9.2.0"
val circeVersion = "0.14.5"
val CalibanVersion = "2.5.0"
val ZioHttpVersion = "3.0.0-RC1"
val tapirVersion = "1.9.0"
//Compile / run / fork := true

lazy val root = (project in file("."))
  .enablePlugins(CalibanPlugin)
  .settings(
    name := "zio-playground-hello-world",
    Compile / caliban / calibanSettings ++= Seq(
      calibanSetting(file("zio-playground/src/main/graphql/sdl.gql"))(
        // important to set this. otherwise you'll get client code
        _.genType(Codegen.GenType.Schema)
          // you can customize the codegen further with this DSL
          .clientName("NameOfApi.scala")
          .packageName("dev.zio.generated")
      )
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "dev.zio" %% "zio" % ZioVersion,
      "dev.zio" %% "zio-logging-slf4j2" % ZioLoggingVersion,
      "dev.zio" %% "zio-streams" % ZioVersion,
      "dev.zio" %% "zio-macros" % ZioVersion,
      "dev.zio" %% "zio-streams" % ZioVersion,
      "dev.zio" %% "zio-http" % ZioHttpVersion,
      "dev.zio" %% "zio-test" % ZioVersion % Test,
      "dev.zio" %% "zio-json" % "0.3.0-RC8",
      "dev.zio" %% "zio-interop-reactivestreams" % "1.3.5",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-zio" % tapirVersion,
      "com.cronutils" % "cron-utils" % cronUtilsVersion,
      "com.github.ghostdogpr" %% "caliban" % CalibanVersion,
      "com.github.ghostdogpr" %% "caliban-zio-http" % CalibanVersion,
      "io.getquill" %% "quill-zio" % quillVersion,
      "io.getquill" %% "quill-jdbc-zio" % quillVersion,
      "io.getquill" %% "quill-codegen-jdbc" % quillVersion,
      "org.postgresql" % "postgresql" % "42.5.0",
      "com.github.pureconfig" %% "pureconfig" % "0.17.3",
      "com.opencsv" % "opencsv" % "5.5.2",
      //"com.github.tharwaninitin" %% "cron4zio" % Cron4ZioVersion,
      "dev.zio" %% "zio-prelude" % "1.0.0-RC21",
      "io.estatico" %% "newtype" % newtypeVersion,
      "io.circe" %% "circe-core" % CirceVersion,
      "org.typelevel" %% "squants" % squantsVersion,
      "org.slf4j" % "slf4j-api" % Slf4jVersion,
      "org.slf4j" % "slf4j-simple" % Slf4jVersion,
      "ch.qos.logback" % "logback-core" % LogbackVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "ch.qos.logback.contrib" % "logback-jackson" % LogbackContribVersion,
      "ch.qos.logback.contrib" % "logback-json-classic" % LogbackContribVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion,
      "eu.timepit" %% "refined" % "0.9.27",
      "com.beachape" %% "enumeratum" % "1.7.3"
    )
  )

