package dev.zio.caliban.subgraph

import java.util.UUID
import dev.zio.caliban.table
final case class Entitlement (id: UUID, name: String)

object Entitlement {
  def fromTable(r: table.Entitlement): Entitlement = {
    Entitlement(
      id = UUID.fromString(r.id),
      name = r.name
    )
  }
}
