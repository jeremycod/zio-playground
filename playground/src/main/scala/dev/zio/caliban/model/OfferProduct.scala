package dev.zio.caliban.model

// TODO we should consider a dual pattern here (union) of two kinds of OfferProduct, one with referenceOffer and the
//  other with standardRetailPrice as having both together makes no sense and makes coding around it more complex
final case class OfferProduct(
    //standardRetailPrice: Option[Price],
    referenceOffer: Option[String],
    // phases: Seq[Phase],
    // legacy: Map[String, String],
    // Map(SKU code -> Map("country" -> Set(countries), "platforms" -> Set(platforms)))
   // skus: Map[String, Map[String, Set[String]]]
)

object OfferProduct {

  // OfferProduct.skus Map keys
  object LegacySku {
    val name = "name"
    val description = "description"
    val legacyProductId = "legacyProductId"
    val legacySkuId = "legacySkuId"
    val isIAPSku = "isIAPSku"
    val is3PPSku = "is3PPSku"
    val countries = "countries"
    val groups = "groups"
    val platforms = "platforms"
  }

  // OfferProduct.legacy Map keys
  val discountTypeKey = "discount_type"
  val recurringKey = "recurring"

}
