package dev.zio.caliban.model

import enumeratum._
import enumeratum.EnumEntry._

import java.util.Locale


sealed trait ProductEntityType extends EnumEntry with Uppercase

object ProductEntityType extends Enum[ProductEntityType] {
  override def values: IndexedSeq[ProductEntityType] = findValues

  case object LIVE_TV extends ProductEntityType
  case object PAY_PER_VIEW extends ProductEntityType
  case object PREMIUM_NETWORKS extends ProductEntityType
  case object BASE extends ProductEntityType
  case object TEST extends ProductEntityType
  case object PREMIER_ACCESS extends ProductEntityType
  case object SEASONAL extends ProductEntityType
  case object FEATURE extends ProductEntityType
  case object ONE_TIME_PURCHASE extends ProductEntityType
  case object STANDALONE extends ProductEntityType
  case object UNKNOWN extends ProductEntityType

  def findByName(name: String): ProductEntityType =
    values.find(_.toString.toLowerCase(Locale.US) == name.toLowerCase(Locale.US)
    ).getOrElse(ProductEntityType.UNKNOWN)
}

