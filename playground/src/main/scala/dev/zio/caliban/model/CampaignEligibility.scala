package dev.zio.caliban.model

import dev.zio.caliban.model.Offer.Cohort

// eligibleCohorts and ineligibleCohorts are mutually exclusive and there's no third state..
final case class CampaignEligibility(
    eligibleCohorts: Set[Cohort],
    ineligibleCohorts: Set[Cohort],
    setNumber: Int
)
