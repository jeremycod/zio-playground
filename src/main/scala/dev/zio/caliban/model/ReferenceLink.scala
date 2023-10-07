package dev.zio.caliban.model

final case class ReferenceLink(key: String, url: String) {
  override def toString: String = s"$key:$url"
}

object ReferenceLink {
  def fromReferenceLinkString(s: String): Either[String, ReferenceLink] = s.split(":", 2) match {
    case Array(key, url) => Right(ReferenceLink(key, url))
    case a =>
      Left(s"Unable to create a referenceLink from the supplied arguments. Args: ${a.mkString(", ")}")
  }
}
