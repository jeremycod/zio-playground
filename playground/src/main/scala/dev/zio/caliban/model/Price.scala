package dev.zio.caliban.model

final case class Price(
    id: String,
    amount: Int,
    grossAmount: Option[Int],
    currency: String,
    productId: String
)
