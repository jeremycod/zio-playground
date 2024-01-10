package com.playground.dss.omp.graphql.services


import com.playground.dss.omp.graphql.security.SecurityHelpers
import com.playground.dss.omp.graphql.subgraph.Types.CreateOrEditBaseProductInput

trait ProductService {
  def createBaseProduct(product: CreateOrEditBaseProductInput) = for {
    profile <- SecurityHelpers.getProfile
    
  } yield Product.fromTableWithAttributes(productWithAttributes)
}
object ProductService {

}
