package dev.zio.caliban.model

final case class Channel(
    id: String,
    name: String,
    partner: Option[String],
    acquisitionChannel: Option[String]
)
