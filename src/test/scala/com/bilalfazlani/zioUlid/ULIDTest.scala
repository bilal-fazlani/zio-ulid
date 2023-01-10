package com.bilalfazlani.zioUlid

import zio.test.*
import zio.test.Assertion.*
import zio.test.TestAspect.*
import zio.Chunk

object ULIDTest extends ZIOSpecDefault {

  private val validChars =
    "0123456789ABCDEFGHJKMNPQRSTVWXYZabcdefghjkmnpqrstvwxyz"

  val validStringGen = {
    val firstValidChars = "01234567"
    val headGen = Gen.elements(firstValidChars.toCharArray()*)
    val tailGen = Gen.stringN(25)(Gen.elements(validChars.toCharArray()*))
    (headGen zip tailGen).map(x => x._1 + x._2)
  }
  val timestampGen = Gen.long(0L, 281474976710655L)

  val spec = suite("ULIDTest")(
    test("parse ULID strings") {
      check(validStringGen) { str =>
        assert(ULID(str))(isRight)
      }
    },
    test("report overflow ULID") {
      val ulid = ULID("Z0000000000000000000000000")
      assert(ulid)(
        isLeft(
          equalTo(
            ULIDStringParsingError.OverflowValue(
              "Z0000000000000000000000000"
            )
          )
        )
      )
    },
    test("report smaller ULID length") {
      val ulid = ULID("123AA")
      assert(ulid)(
        isLeft(equalTo(ULIDStringParsingError.InvalidLength("123AA")))
      )
    },
    test("report larger ULID length") {
      val ulid = ULID("123AA123AA123AA123AA123AA123AA123AA")
      assert(ulid)(
        isLeft(
          equalTo(
            ULIDStringParsingError.InvalidLength(
              "123AA123AA123AA123AA123AA123AA123AA"
            )
          )
        )
      )
    },
    test("report invalid ULID characters") {
      val stringGen = Gen.stringN(1)(
        Gen.elements("OoIiUuLl".toCharArray()*)
      ) zip Gen.stringN(25)(Gen.elements(validChars.toCharArray()*))
      check(stringGen) { case (a, b) =>
        val str = a + b
        val ulid = ULID(str)
        assert(ulid)(
          isLeft(equalTo(ULIDStringParsingError.InvalidCharacters(str)))
        )
      }
    },
    test("access timestamp") {
      val ulid = ULID("01GNMX0KYJ7P2EXXFFGAPZWSH0")
      assert(ulid)(
        isRight(hasField("timestamp", _.timestamp, equalTo(1672517537746L)))
      )
    },
    test("toString") {
      val ulid = ULID("01GNMM5NTDC6P2MF6KASPJWTXF")
      assert(ulid)(
        isRight(
          hasField(
            "toString",
            _.toString,
            equalTo("01GNMM5NTDC6P2MF6KASPJWTXF")
          )
        )
      )
    },
    test("equals ignoring case") {
      val ulid1 = ULID("01GNMM5NTDC6P2MF6KASPJWTXF")
      val ulid2 = ULID("01GNMM5NTDC6P2MF6KASPJWTXf")
      assertTrue(ulid1 == ulid2)
    },
    test("not equals") {
      val ulid1 = ULID("01GNMM5NTDC6P2MF6KASPJWTXF")
      val ulid2 = ULID("01GNMM5NTDC6P2MF6KASPJWTXG")
      assertTrue(ulid1 != ulid2)
    },
    test("ordering") {
      val ulid1 = ULID("01GNMM5NTDC6P2MF6KASPJWTXF")
      val ulid2 = ULID("01GNMM5NTDC6P2MF6KASPJWTXG")
      val comparision =
        ulid1.flatMap(one => ulid2.map(two => one < two && two > one))
      assert(comparision)(isRight(equalTo(true)))
    },
    test("create ULID from timestamp and random bytes") {
      check(timestampGen zip Gen.chunkOfN(10)(Gen.byte)) {
        case (timestamp, randomBytes) =>
          val ulid = ULID(timestamp, randomBytes)
          assert(ulid)(isRight)
      }
    },
    test("report error when random bytes are not 10 bytes") {
      val encoded = ULID(
        timestamp = 123123L,
        randomBytes = Chunk(49, 65, 66, 52, 53, 67, 55, 68).map(_.toByte)
      )
      assert(encoded)(
        isLeft(
          equalTo(
            ULIDBytesParsingError.InvalidBytesLength(
              10,
              8
            )
          )
        )
      )
    },
    test("report error when timestamp is > 48 bits") {
      val encoded = ULID(
        281474976710656L, // one more than 48 bits
        Chunk.fill[Byte](10)(0xff.toByte)
      )
      assert(encoded)(
        isLeft(
          equalTo(
            ULIDBytesParsingError.InvalidTimestamp(281474976710656L)
          )
        )
      )
    },
    test(
      "parity between ULIDs created using string and timestamp & random bytes"
    ) {
      val ulid1 = ULID("7ZZZZZZZZZZZZZZZZZZZZZZZZZ")
      val ulid2 = ULID(281474976710655L, Chunk.fill[Byte](10)(0xff.toByte))
      assert(ulid1)(isRight && equalTo(ulid2))
    },
    test("create ULID from bytes") {
      check(validStringGen) { str =>
        val test = for
          stringEncodedULID <- ULID(str)
          bytesDecodedULID <- ULID(stringEncodedULID.bytes)
        yield stringEncodedULID == bytesDecodedULID
        assert(test)(isRight(isTrue))
      }
    }
  )
}
