package com.bilalfazlani

import zio._
import com.bilalfazlani.zioUlid.ULIDBytesParsingError._
import com.bilalfazlani.zioUlid.ULIDStringParsingError._
import java.util.concurrent.TimeUnit

package object zioUlid {
  type BinaryULID = (Long, Long)

  implicit class BinaryULIDExtensions(c: BinaryULID) {
    def encode: String = BinaryULID.encode128bits(high, low)
    def tuple: (Long, Long) = c
    def high: Long = c._1
    def low: Long = c._2
    def timestamp: Long = (high >>> 16) & 0xffffffffffffL
    def bytes: Chunk[Byte] = {
      val b = new Array[Byte](16)
      for (i <- 0 until 8) {
        b(i) = ((high >>> (64 - (i + 1) * 8)) & 0xffL).toByte
      }
      for (i <- 0 until 8) {
        b(i + 8) = ((low >>> (64 - (i + 1) * 8)) & 0xffL).toByte
      }
      Chunk.fromArray(b)
    }
  }

  object BinaryULID {

    private[zioUlid] val minTimestamp: Long = 0L
    private[zioUlid] val maxTimestamp: Long = (~0L) >>> (64 - 48)

    private[zioUlid] def apply(high: Long, low: Long): BinaryULID = (high, low)

    val empty: BinaryULID = (0L, 0L)

    def apply(
        timestamp: Long,
        rand: Chunk[Byte]
    ): Either[
      ULIDBytesParsingError,
      BinaryULID
    ] =
      withValidation(timestamp, rand) {
        unsafe(timestamp, rand)
      }

    private[zioUlid] def apply(
        timestamp: Long
    ): IO[InvalidTimestamp, BinaryULID] =
      for {
        randBytes <- ZIO.randomWith(_.nextBytes(10))
        _ <- ZIO.fromEither(validateTimestamp(timestamp))
      } yield unsafe(timestamp, randBytes)

    // todo:remove var
    private[zioUlid] def fromBytes(
        bytes: Chunk[Byte]
    ): Either[InvalidBytesLength, BinaryULID] =
      if (bytes.length != 16) Left(InvalidBytesLength(16, bytes.length))
      else {
        var i = 0
        var hi = 0L
        while (i < 8) {
          hi <<= 8
          hi |= bytes(i) & 0xffL
          i += 1
        }
        var low = 0L
        while (i < 16) {
          low <<= 8
          low |= bytes(i) & 0xffL
          i += 1
        }
        Right(BinaryULID(hi, low))
      }

    private def unsafe[zioUlid](
        timestamp: Long,
        rand: Chunk[Byte]
    ): BinaryULID = {
      val hi: Long = ((timestamp & 0xffffffffffffL) << (64 - 48)) |
        (rand(0) & 0xffL) << 8 | (rand(1) & 0xffL)
      val low: Long =
        ((rand(2) & 0xffL) << 56) |
          ((rand(3) & 0xffL) << 48) |
          ((rand(4) & 0xffL) << 40) |
          ((rand(5) & 0xffL) << 32) |
          ((rand(6) & 0xffL) << 24) |
          ((rand(7) & 0xffL) << 16) |
          ((rand(8) & 0xffL) << 8) |
          (rand(9) & 0xffL)
      BinaryULID(hi, low)
    }

    private[zioUlid] def decode(
        s: String
    ): Either[ULIDStringParsingError, BinaryULID] =
      withValidation(s) { decodeUnsafe(s) }

    private[zioUlid] def decodeUnsafe(s: String) = {
      val carryMask = ~(~0L >>> 5)
      s.zipWithIndex.foldLeft[BinaryULID]((0L, 0L)) { case (acc, (c, i)) =>
        val (hi, low) = acc
        val carry = (low & carryMask) >>> (64 - 5)
        val newL = low << 5 | decode(s.charAt(i))
        val newH = hi << 5 | carry
        (newH, newL)
      }
    }

    private[zioUlid] def decodeTimestamp(s: String): Long =
      Range(1, 10).foldLeft[Long](decode(s.charAt(0))) { case (acc, i) =>
        acc << 5 | decode(s.charAt(i))
      }

    // https://stackoverflow.com/questions/10813154/how-do-i-convert-a-number-to-a-letter-in-java
    private val decodingChars = Array[Byte](
      -1, -1, -1, -1, -1, -1, -1, -1, // 0
      -1, -1, -1, -1, -1, -1, -1, -1, // 8
      -1, -1, -1, -1, -1, -1, -1, -1, // 16
      -1, -1, -1, -1, -1, -1, -1, -1, // 24
      -1, -1, -1, -1, -1, -1, -1, -1, // 32
      -1, -1, -1, -1, -1, -1, -1, -1, // 40
      0, 1, 2, 3, 4, 5, 6, 7, // 48
      8, 9, -1, -1, -1, -1, -1, -1, // 56
      -1, 10, 11, 12, 13, 14, 15, 16, // 64
      17, 1, 18, 19, 1, 20, 21, 0, // 72
      22, 23, 24, 25, 26, -1, 27, 28, // 80
      29, 30, 31, -1, -1, -1, -1, -1, // 88
      -1, 10, 11, 12, 13, 14, 15, 16, // 96
      17, 1, 18, 19, 1, 20, 21, 0, // 104
      22, 23, 24, 25, 26, -1, 27, 28, // 112
      29, 30, 31 // 120
    )

    private def decode(c: Char): Byte = {
      val index = c & 0x7f
      if (index > -1 && index < 123) decodingChars(index)
      else -1
    }

    private val encodingChars = Chunk(
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
      'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X',
      'Y', 'Z'
    )

    private[zioUlid] def encode(i: Int): Char = encodingChars(i & 0x1f)

    private[zioUlid] def encode128bits(hi: Long, low: Long): String = {
      val s = new StringBuilder(26)
      Range(0, 26).foldLeft((hi, low)) { (acc, _) =>
        val (h, l) = acc
        s += encode((l & 0x1fL).toInt)
        val carry = (h & 0x1fL) << (64 - 5)
        val newL = l >>> 5 | carry
        val newH = h >>> 5
        (newH, newL)
      }
      s.reverseInPlace().toString()
    }

    private[zioUlid] def isValidBase32(s: String): Boolean = s.forall { c =>
      decode(c) != -1
    }

    private def validateInput(
        s: String
    ): Either[ULIDStringParsingError, Unit] = {
      if (s.length != 26) Left(InvalidLength(s))
      else if (!isValidBase32(s)) Left(InvalidCharacters(s))
      else if (s.toUpperCase > "7ZZZZZZZZZZZZZZZZZZZZZZZZZ")
        Left(OverflowValue(s))
      else Right(())
    }

    private[zioUlid] def withValidation[A](s: String)(
        f: => A
    ): Either[ULIDStringParsingError, A] = validateInput(s).map(_ => f)

    private def withValidation[A](timestamp: Long, rand: Chunk[Byte])(
        f: => A
    ): Either[ULIDBytesParsingError, A] =
      for {
        _ <- validateTimestamp(timestamp) // need to limit timestamp to 48 bits
        _ <- validateRandomBytes(rand) // need to limit random bytes to 10 bytes
      } yield f

    private def validateRandomBytes(
        rand: Chunk[Byte]
    ): Either[InvalidBytesLength, Chunk[Byte]] =
      if (rand.length != 10) Left(InvalidBytesLength(10, rand.size))
      else Right(rand)

    private def validateTimestamp(
        timestamp: Long
    ): Either[InvalidTimestamp, Long] =
      if (timestamp < minTimestamp || timestamp > maxTimestamp)
        Left(InvalidTimestamp(timestamp))
      else Right(timestamp)
  }

}
