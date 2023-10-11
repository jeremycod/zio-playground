package dev.zio.caliban

object Queries {
  val offers: String =
    """{
      |offers {
      | id
      | name
      | description
      | discountId
      | author
      | profile
      | version
      |}
      |}
      |""".stripMargin

}
