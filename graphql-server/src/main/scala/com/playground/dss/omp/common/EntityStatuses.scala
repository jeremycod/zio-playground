package com.playground.dss.omp.common

// TODO incorporate these statuses into all the models in this service rather than just using it for the status update endpoints
object EntityStatuses {

  sealed trait Status {
    def stringRep: String
  }

  val DRAFT = "DRAFT"
  val NEEDS_REVIEW = "NEEDS_REVIEW"
  val FINANCE_APPROVED = "FINANCE_APPROVED"
  val LIVE = "LIVE"
  val LIVE_WITH_EDITS = "LIVE_WITH_EDITS"
  val EXPIRED = "EXPIRED"
  val DEACTIVATED = "DEACTIVATED"
  val ENABLED = "ENABLED"
  val DISABLED = "DISABLED"

  val allSkuStatuses: Set[SkuStatuses] = Set(Draft, Live, LiveWithEdits, Deactivated)
  val allOfferStatuses: Set[OfferStatuses] = Set(Draft, NeedsReview, FinanceApproved, Live, LiveWithEdits, Deactivated)
  val allOffer3PPStatuses: Set[Offer3ppStatuses] = Set(Draft, Live, Deactivated)
  val allPromotionStatuses: Set[PromotionStatuses] = Set(Enabled, Disabled)
  val allProductStatuses: Set[ProductStatuses] = Set(Live, LiveWithEdits)
  val allPartnerStatuses: Set[PartnerStatuses] = Set(Live, NeedsReview, FinanceApproved)

  sealed trait SkuStatuses extends Status
  sealed trait OfferStatuses extends Status
  sealed trait Offer3ppStatuses extends Status
  sealed trait PromotionStatuses extends Status
  sealed trait ProductStatuses extends Status
  sealed trait PartnerStatuses extends Status

  case object Draft extends OfferStatuses with Offer3ppStatuses with SkuStatuses {
    override val stringRep: String = DRAFT
  }
  case object NeedsReview extends OfferStatuses with PartnerStatuses {
    override val stringRep: String = NEEDS_REVIEW
  }
  case object FinanceApproved extends OfferStatuses with PartnerStatuses {
    override val stringRep: String = FINANCE_APPROVED
  }
  case object Live
      extends OfferStatuses with Offer3ppStatuses with SkuStatuses with ProductStatuses with PartnerStatuses {
    override val stringRep: String = LIVE
  }
  case object LiveWithEdits extends OfferStatuses with SkuStatuses with ProductStatuses {
    override val stringRep: String = LIVE_WITH_EDITS
  }
  // Currently only a 'Visual' status in the UI, imo this should eventually transition to a concrete status in the back-end
  case object Expired extends OfferStatuses with Offer3ppStatuses with PromotionStatuses {
    override val stringRep: String = EXPIRED
  }
  case object Deactivated extends OfferStatuses with Offer3ppStatuses with SkuStatuses {
    override val stringRep: String = DEACTIVATED
  }
  case object Enabled extends PromotionStatuses {
    override val stringRep: String = ENABLED
  }
  case object Disabled extends PromotionStatuses {
    override val stringRep: String = DISABLED
  }
}
