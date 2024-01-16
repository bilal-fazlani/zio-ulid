package com.bilalfazlani.zioUlid

import zio.test._
import zio.test.Assertion._
import zio.test.TestAspect._
import zio._

object ULIDGenTest extends ZIOSpecDefault {
  val spec = suite("ULIDGenTest")(
    test("generate ULID") {
      (
        for {
          ulid <- ULID.nextULID.map(_.toString)
        } yield assertTrue(ulid == "00000000000000000000000001")
        ).provide(ULIDGen.live)
    },
    test("ULID in same millisecond increments random part") {
      (for {
        ulid1 <- ULID.nextULID.map(_.toString)
        ulid2 <- ULID.nextULID.map(_.toString)
      } yield assertTrue(ulid1 == "00000000000000000000000001") && assertTrue(
        ulid2 == "00000000000000000000000002"
      ))
        .provide(ULIDGen.live)
    },
    test("ULID in different millisecond creates new random part") {
      val randomBytes: Chunk[Byte] = Chunk.fill(10)(0xff.toByte)
      (for {
        _ <- TestClock.adjust(1.millisecond)
        _ <- TestRandom.clearBytes
        _ <- TestRandom.feedBytes(randomBytes)
        ulid <- ULID.nextULID.map(_.toString)
      } yield assertTrue(ulid.toString == "0000000001ZZZZZZZZZZZZZZZZ"))
        .provide(ULIDGen.live)
    }
  )
}
