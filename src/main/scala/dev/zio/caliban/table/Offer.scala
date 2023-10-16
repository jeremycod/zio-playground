package dev.zio.caliban.table

case class Offer(
    id: String,
    name: String,
    description: Option[String],
    discountId: Option[String]
)
