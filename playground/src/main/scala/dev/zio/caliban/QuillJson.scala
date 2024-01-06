package dev.zio.caliban

import io.circe._
import io.circe.syntax._
import io.getquill.MappedEncoding
object QuillJson {
  def jsonEncoder[T: Encoder]: MappedEncoding[T, String] = MappedEncoding[T, String](_.asJson.toString())
  def jsonDecoder[T: Decoder]: MappedEncoding[String, T] = MappedEncoding[String, T](x =>
    parser.decode[T](x).fold(err => throw err, identity)
  ) // NB(bulat): unsafe throw here
}
