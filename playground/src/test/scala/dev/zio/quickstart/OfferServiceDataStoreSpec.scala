package dev.zio.quickstart

import dev.zio.caliban.persist.OfferServiceDataStore
import dev.zio.caliban.persist.OfferServiceDataStore._
import dev.zio.caliban.table._
import zio.test.Assertion._
import zio.test.ZIOSpecDefault
import io.getquill.jdbczio.Quill
import io.getquill.{Query, SnakeCase}
import zio._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}
import zio.test.Assertion.equalTo
import zio.test.TestEnvironment

/*object OfferServiceDataStoreSpec extends ZIOSpecDefault {
  def spec = suite("OfferServiceDataStoreSpec") {
    test("fetchOffers") {
      val quill = Quill.Postgres[SnakeCase]
      val dataStore = new OfferServiceDataStore(quill)
      val offers = dataStore.fetchOffers.run
      assert(offers)(equalTo(Seq.empty[Offer]))
    }

    test("fetchAttributes") {
      val quill = Quill.Postgres[SnakeCase]
      val dataStore = new OfferServiceDataStore(quill)
      val attributes = dataStore.fetchAttributes("1").run
      assert(attributes)(equalTo(Seq.empty[Attribute]))
    }

    test("fetchReferenceOffersCandidates") {
      val quill = Quill.Postgres[SnakeCase]
      val dataStore = new OfferServiceDataStore(quill)
      val offers = dataStore.fetchReferenceOffersCandidates(1, "USD", "US").run
      assert(offers)(equalTo(Seq.empty[Offer]))
    }
  }
}*/
