package dev.zio.caliban

import io.getquill.codegen.jdbc.SimpleJdbcCodegen
import io.getquill.codegen.model.SnakeCaseNames
import io.getquill.util.LoadConfig
import io.getquill.codegen.jdbc.SimpleJdbcCodegen
import org.postgresql.ds.PGSimpleDataSource
object CodeGenMain extends App {

  val pgDataSource = new PGSimpleDataSource()
  pgDataSource.setURL(
    "jdbc:postgresql://127.0.0.1:5432/reviewboard?ssl=false"
  )
  pgDataSource.setUser("docker")
  pgDataSource.setPassword("docker")
  val gen = new SimpleJdbcCodegen(pgDataSource, "dev.zio.caliban.table") {
    override def nameParser = SnakeCaseNames
  }
  gen.writeFiles("src/main/scala/dev/zio/caliban/table")
}
