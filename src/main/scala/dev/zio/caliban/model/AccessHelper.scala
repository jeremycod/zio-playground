package dev.zio.caliban.model

import java.time.OffsetDateTime

trait AccessHelper {
  def attributes: Map[String, Set[String]]

  def one(name: String): Option[String] = attributes.get(name).flatMap(_.headOption)
  def many(name: String): Set[String] = attributes.getOrElse(name, Set.empty)

  def strToOffsetDateTime(value: String): OffsetDateTime = OffsetDateTime.parse(value)

  // Logging if this fails?
  def strToBoolean(value: String): Boolean = value.toBooleanOption.getOrElse(false)

}
