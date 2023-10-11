package dev.zio.caliban

import zio.ZIO
import zio.{ZIO, _}
import ZIO.{attempt, fromOption, logInfo}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

object QuillDataSource {
  val databaseURLOpt = Some("jdbc:postgresql://localhost:5432/")
  private def mkDataSource: Task[HikariDataSource] =
    for {
     // databaseURLOpt <- System.env("DATABASE_URL").orDie
      databaseURL <- fromOption(databaseURLOpt)
        .orElseFail(new RuntimeException("Missing the \"DATABASE_URL\" environment variable."))
      pgDataSource <- attempt {
        val dataSource = new org.postgresql.ds.PGSimpleDataSource()
        dataSource.setURL(databaseURL)
        //dataSource.setDatabaseName("genie")
        dataSource.setDatabaseName("genieplus")
        //dataSource.setUser("postgres")
        dataSource.setUser("alice4")
        //dataSource.setPassword("genieplus")
        dataSource.setPassword("securePass1")
        dataSource
      }
      hikariConfig <- attempt {
        val config = new HikariConfig()
        config.setDataSource(pgDataSource)
        config
      }
      dataSource <- attempt(new HikariDataSource(hikariConfig))
    }

  yield (dataSource)

  val layer: ZLayer[Any, Throwable, HikariDataSource] =
    ZLayer.fromZIO(mkDataSource)
}
