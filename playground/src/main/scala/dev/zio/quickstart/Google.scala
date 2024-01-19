package dev.zio.quickstart

import zio._

trait Google {
  def countPicturesOf(topic: String): UIO[Int]

}

object GoogleImpl {
  def make: Google = new Google {
    override def countPicturesOf(topic: String): UIO[RuntimeFlags] =
      ZIO.succeed(if (topic == "cats") 1337 else 1338)
  }
}
