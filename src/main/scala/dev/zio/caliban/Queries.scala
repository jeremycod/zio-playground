package dev.zio.caliban

object Queries {
  val offers: String =
    """{
      |offers(profile : "main") {
      | id
      | offer {
      |   id
      |   name
      |   description
      |   discountId
      |   author
      |   profile
      |   version
      | }
      | products {
      |   offerProduct{
      |     referenceOffer
      |   }
      | }
      |}
      |}
      |""".stripMargin

}
