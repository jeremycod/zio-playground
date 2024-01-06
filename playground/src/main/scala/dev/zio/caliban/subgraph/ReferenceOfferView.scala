package dev.zio.caliban.subgraph

import dev.zio.caliban.table


case class ReferenceOfferView(
    id: String
)

object ReferenceOfferView {
  def fromTable(o: table.Offer): ReferenceOfferView = {
    ReferenceOfferView(o.id)
  }
}

