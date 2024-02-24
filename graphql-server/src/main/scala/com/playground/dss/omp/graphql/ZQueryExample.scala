package com.playground.dss.omp.graphql

import zio._
import zio.query._

object ZQueryExample extends ZIOAppDefault {
  case class GetUserName(id: Int) extends Request[Throwable, String]

  lazy val UserDataSource: DataSource.Batched[Any, GetUserName] =
    new DataSource.Batched[Any, GetUserName] {
      val identifier: String = "UserDataSource"

      def run(requests: Chunk[GetUserName])(implicit trace: Trace): ZIO[Any, Nothing, CompletedRequestMap] = {
        val resultMap = CompletedRequestMap.empty
        requests.toList match {
          case request :: Nil =>
            val result: Task[String] = {
              // get user by ID e.g. SELECT name FROM users WHERE id = $id
              ZIO.succeed("RES")
            }

            result.exit.map(resultMap.insert(request, _))

          case batch: Seq[GetUserName] =>
            val result: Task[List[(Int, String)]] = {
              // get multiple users at once e.g. SELECT id, name FROM users WHERE id IN ($ids)
              ZIO.succeed(List.empty)
            }

            result.fold(
              err =>
                requests.foldLeft(resultMap) { case (map, req) =>
                  map.insert(req, Exit.fail(err))
                },
              _.foldLeft(resultMap) { case (map, (id, name)) =>
                map.insert(GetUserName(id), Exit.succeed(name))
              }
            )
        }
      }

    }

  def getUserNameById(id: Int): ZQuery[Any, Throwable, String] =
    ZQuery.fromRequest(GetUserName(id))(UserDataSource)

  val query: ZQuery[Any, Throwable, List[String]] =
    for {
      ids <- ZQuery.succeed(1 to 10)
      names <- ZQuery.foreachPar(ids)(id => getUserNameById(id)).map(_.toList)
    } yield (names)

  def run = query.run.tap(usernames => Console.printLine(s"Usernames: $usernames"))
}
