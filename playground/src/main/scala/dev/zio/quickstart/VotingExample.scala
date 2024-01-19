package dev.zio.quickstart

import zio.prelude._

object VotingExample extends scala.App {

  object Votes extends Subtype[Int] {
    implicit val associativeVotes: Associative[Votes] =
      new Associative[Votes] {
        override def combine(l: =>Votes, r: =>Votes): Votes =
          Votes(l + r)
      }
  }
  type Votes = Votes.Type

  object Topic extends Subtype[String]
  type Topic = Topic.Type

  final case class VoteState(map: Map[Topic, Votes]) { self =>
    /* def combine(that: VoteState): VoteState =
      VoteState(self.map combine that.map)*/
  }

  val zioHttp = Topic("zio-http")
  val uziHttp = Topic("uzi-http")
  val zioTlsHttp = Topic("zio-tls-http")

  val leftVotes = VoteState(Map(zioHttp -> Votes(4), uziHttp -> Votes(2)))
  val rightVotes = VoteState(Map(zioHttp -> Votes(2), zioTlsHttp -> Votes(2)))

  // println(leftVotes combine rightVotes)
  // Output: VoteState(Map(zio-http -> 6, uzi-http -> 2, zio-tls-http -> 2))
}
