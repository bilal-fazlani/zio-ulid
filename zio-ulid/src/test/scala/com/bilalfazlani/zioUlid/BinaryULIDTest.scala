package com.bilalfazlani.zioUlid

import zio.test._
import zio.test.Assertion._
import zio.Chunk

object BinaryULIDTest extends ZIOSpecDefault {
  private val timestampGen = Gen.long(0L, 281474976710655L)
  val spec = suite("BinaryULIDTest")(
    test("binary to string") {
      val encoded = BinaryULID(1231231231223534L, 6786796773453452L).encode
      assert(encoded)(equalTo("000HFWRQ1CCVQ0060WHS1DGBMC"))
    },
    test("string to binary") {
      val decoded: Either[ULIDStringParsingError, BinaryULID] =
        BinaryULID.decode("000HFWRQ1CCVQ0060WHS1DGBMC")
      assert(decoded)(
        isRight(equalTo(BinaryULID(1231231231223534L, 6786796773453452L)))
      )
    },
    test("back and forth") {
      check(Gen.long, Gen.long) { (high, low) =>
        val binaryULID = BinaryULID(high, low)
        val encoded: String = binaryULID.encode
        val decoded: Either[ULIDStringParsingError, BinaryULID] =
          BinaryULID.decode(encoded)
        assert(decoded)(isRight(equalTo(binaryULID)))
      }
    },
    test("bytes to BinaryULID") {
      val bytes = Chunk.fill(16)(0xff.toByte)
      val binaryUlid = BinaryULID.fromBytes(bytes)
      assert(binaryUlid)(isRight(equalTo(BinaryULID(-1L, -1L))))
    },
    test(
      "isValidBase32 should validate all characters without throwing exception"
    ) {
      check(Gen.char) { char =>
        val isValid = BinaryULID.isValidBase32(char.toString())
        assertCompletes
      }
    }
  )
}
