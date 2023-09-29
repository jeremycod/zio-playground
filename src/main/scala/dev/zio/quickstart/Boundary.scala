package dev.zio.quickstart
import zio._

trait Boundary {
  def doesGoogleHaveEvenAmountOfPicturesOf(topic: String): UIO[Boolean]

}

object BoundaryImpl {
  def make(google: Google): Boundary =
    new Boundary {
      override def doesGoogleHaveEvenAmountOfPicturesOf(topic: String): UIO[Boolean] =
        google.countPicturesOf(topic).map(_ % 2 == 0)
    }
}
