package com.playground.dss.omp.graphql.subgraph

import com.playground.dss.omp.graphql.table.{Entitlement => EntitlementTbl}

object Entitlement {
  def fromTable(r: EntitlementTbl): Types.Entitlement =
    Types.Entitlement(
      id = r.id,
      name = r.name
    )
}
