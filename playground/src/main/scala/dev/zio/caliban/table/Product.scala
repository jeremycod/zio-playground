package dev.zio.caliban.table

final case class Product(
    id: String,
    name: String,
    description: Option[String],
    legacy: String,
    author: String,
    datetime: String,
    profile: String,
    version: Long
)

