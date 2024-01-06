package com.playground.dss.omp.graphql

import io.getquill.codegen.model._
import io.getquill.codegen.jdbc.ComposeableTraitsJdbcCodegen

import zio._

object QuillCodeGenerator extends ZIOAppDefault {
  import io.getquill.codegen.util.StringUtil._
  val program: ZIO[ApplicationConfiguration, Throwable, Unit] = for {
    _ <- ZIO.logInfo("Generating Quill tables")
    _ <- ZIO.serviceWithZIO[ApplicationConfiguration](config => generateTables(config))
    _ <- ZIO.logInfo("Quill tables generation completed")

  } yield ()

  private def generateTables(config: ApplicationConfiguration): Task[Unit] = {
    for {
      pgDataSource <- ZIO.attempt {
                        val dbConfig = config.databaseConfig
                        //val dbConfig: DatabaseConfig = QuillDataSource.getDbConfig(conf)
                        QuillDataSource.mkSimpleDataSource(dbConfig: DatabaseConfig)

                      }
      gen = new ComposeableTraitsJdbcCodegen(
              pgDataSource,
              packagePrefix = "com.playground.dss.omp.graphql.table",
              nestedTrait = true) {
              override def nameParser = CustomNames(
                tableParser = tm => {
                  val tn = tm.tableName.snakeToUpperCamel
                  if (tn.endsWith("ies")) tn.replace("ies", "y")
                  else tn.stripSuffix("s")
                }
              )
              override def packagingStrategy: PackagingStrategy = PackagingStrategy(
                GroupByPackage,
                PackageHeaderByNamespace(packagePrefix, _.table.namespace),
                PackageHeaderByNamespace(packagePrefix, _.table.namespace),
                ByPackageObjectStandardName
              )
            }
      _ = gen.writeFiles("graphql-server/src/main/scala/com/playground/dss/omp/graphql/table")
    } yield ()
  }
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = program
    .provide(
      AppConfig.layer,
      AppUtils.layer)

}
