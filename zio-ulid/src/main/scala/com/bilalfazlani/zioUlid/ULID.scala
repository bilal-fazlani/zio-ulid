package com.bilalfazlani.zioUlid

import zio.{URIO, Chunk, ZIO}
import com.bilalfazlani.zioUlid.ULIDError.InvalidBytesLength
import math.Ordered.orderingToOrdered

final class ULID private[zioUlid] (private val binary: BinaryULID)
    extends Ordered[ULID] {

  override lazy val toString: String = binary.encode

  lazy val timestamp: Long = binary.timestamp

  lazy val bytes: Chunk[Byte] = binary.bytes

  lazy val tuple: (Long, Long) = binary.tuple

  override def equals(other: Any): Boolean = other match {
    case u: ULID => (u.tuple compare binary.tuple) == 0
    case _       => false
  }

  override lazy val hashCode: Int = binary.hashCode

  override def compare(that: ULID): Int =
    tuple compare that.tuple
}
object ULID {

  /** Creates a ULID from a string. Performs string length validation, character
    * validation, and overflow validation.
    * @param ulidString
    * @return
    *   If string is valid, returns Right(ULID), otherwise returns
    *   Left(ULIDStringParsingError)
    */
  def apply(ulidString: String): Either[ULIDStringParsingError, ULID] =
    BinaryULID.decode(ulidString).map(ULID.unsafe)

  /** Creates a ULID from a timestamp and random bytes. Performs timestamp
    * validation and random bytes size validation
    * @param timestamp
    *   Timestamp in unix (epoch) milliseconds. Should be less than
    *   281474976710655L
    * @param randomBytes
    *   Chunk (of size 10) of random bytes
    * @return
    *   If timestamp and random bytes are valid, returns Right(ULID), otherwise
    *   returns Left(ULIDBytesParsingError)
    */
  def apply(
      timestamp: Long,
      randomBytes: Chunk[Byte]
  ): Either[ULIDBytesParsingError, ULID] =
    BinaryULID(timestamp, randomBytes).map(ULID.unsafe)

  def apply(
      allBytes: Chunk[Byte]
  ): Either[InvalidBytesLength, ULID] =
    BinaryULID.fromBytes(allBytes).map(ULID.unsafe)

  def apply(high: Long, low: Long) = ULID.unsafe(BinaryULID(high, low))

  /** Encodes the given BinaryULID to a string and returns a ULID. Performs no
    * validation.
    * @param binaryULID
    * @return
    *   ULID
    */
  private[zioUlid] def unsafe(binaryULID: BinaryULID): ULID =
    new ULID(binaryULID)

  /** generates a new random ULID
    *
    * @return
    *   a new random ULID
    */
  def nextULID: URIO[ULIDGen, ULID] = ZIO.serviceWithZIO[ULIDGen](_.nextULID)

  def unapply(ulid: String): Option[String] =
    ULID(ulid).toOption.map(_.toString)

  def unapply(ulid: Chunk[Byte]): Option[String] =
    ULID(ulid).toOption.map(_.toString)
}
