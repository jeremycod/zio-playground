package com.playground.dss.omp.graphql

import com.typesafe.config.Config
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.getquill.SnakeCase
import org.postgresql.ds.PGSimpleDataSource
import zio.{Task, ZIO, ZLayer}

/*final case class DatabaseConfig(
    serverName: String,
    portNumber: String,
    databaseName: String,
    username: String,
    password: String
)*/

object FixedSnakeCase extends SnakeCase {

  override def table(name: String): String =
    if (name.endsWith("_metadata")) s"""\"${super.table(name)}\""""
    else s"""\"${super.table(s"${name}s")}\""""

}
object QuillDataSource {

  private def mkDataSource(config: ApplicationConfiguration): Task[HikariDataSource] = {
    for {
      pgDataSource <- ZIO.attempt {
                        val dbConfig: DatabaseConfig = config.databaseConfig
                        mkSimpleDataSource(dbConfig: DatabaseConfig)

                      }
      hikariConfig <- ZIO.attempt {
                        val config = new HikariConfig()
                        config.setDataSource(pgDataSource)
                        config
                      }
      dataSource <- ZIO.attempt(new HikariDataSource(hikariConfig))
    } yield dataSource
  }

  def getDbConfig(conf: Config): DatabaseConfig =
    DatabaseConfig(
      url = conf.getString("url"),
      username = conf.getString("user"),
      password = conf.getString("password")
    )
  def mkSimpleDataSource(dbConfig: DatabaseConfig): PGSimpleDataSource = {
    val dataSource = new org.postgresql.ds.PGSimpleDataSource()
    dataSource.setURL(dbConfig.url)
    dataSource.setUser(dbConfig.username)
    dataSource.setPassword(dbConfig.password)
    dataSource
  }

  val layer: ZLayer[ApplicationConfiguration, Throwable, HikariDataSource] =
    ZLayer.fromZIO(ZIO.serviceWithZIO[ApplicationConfiguration](config => mkDataSource(config)))
}
