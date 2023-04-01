package com.bilalfazlani.zioUlid

import zio.Chunk

sealed trait ULIDStringParsingError extends Throwable
object ULIDStringParsingError {
  case class InvalidLength(string: String) extends ULIDStringParsingError {
    override def getMessage: String =
      s"String length must be 26: \"${string}\" (length: ${string.length})"
  }

  case class InvalidCharacters(string: String) extends ULIDStringParsingError {
    override def getMessage: String =
      s"String (${string}) must contain only Crockford base 32 characters"
  }

  case class OverflowValue(string: String) extends ULIDStringParsingError {
    override def getMessage: String =
      s"ULID value (${string}) should be less than ULID.Max (7ZZZZZZZZZZZZZZZZZZZZZZZZZ)"
  }
}

sealed trait ULIDBytesParsingError extends Throwable

object ULIDBytesParsingError {

  case class InvalidBytesLength(size: Int, expectedSize: Int)
      extends ULIDBytesParsingError {
    override def getMessage: String =
      s"Chunk[Byte] size (${size}) must be of size ${expectedSize}"
  }

  case class InvalidTimestamp(value: Long) extends ULIDBytesParsingError {
    override def getMessage: String =
      s"Timestamp value (${value}) must be between ${BinaryULID.minTimestamp} and ${BinaryULID.maxTimestamp}"
  }
}

case class UnsupportedSystemDateTime(timestamp: Long)
    extends Throwable(
      s"ULID supports system datetime between ${BinaryULID.minTimestamp} and ${BinaryULID.maxTimestamp}. Unsupported value: ${timestamp}"
    )
