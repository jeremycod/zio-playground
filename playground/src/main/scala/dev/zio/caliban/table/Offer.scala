package dev.zio.caliban.table

case class Attribute(key: String, value: String)
case class Offer(
    id: String,
    name: String,
    description: Option[String],
    discountId: Option[String]
)
