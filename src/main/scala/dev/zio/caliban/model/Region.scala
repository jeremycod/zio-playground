package dev.zio.caliban.model

trait Region
object Region {
  case object EMEA extends Region
  case object LATAM extends Region
  case object APAC extends Region
  case object NA extends Region
}
