package com.bilalfazlani.zioUlid

sealed trait ULIDStringParsingError extends Throwable

sealed trait ULIDBytesParsingError extends Throwable

sealed trait ULIDGenerationError extends Throwable

object ULIDError {
  case class InvalidStringLength(string: String) extends ULIDStringParsingError {
    override def getMessage: String =
      s"String length must be 26: \"$string\" (length: ${string.length})"
  }

  case class InvalidCharacters(string: String) extends ULIDStringParsingError {
    override def getMessage: String =
      s"String ($string) must contain only Crockford base 32 characters"
  }

  case class ULIDOverflow(string: String) extends ULIDStringParsingError {
    override def getMessage: String =
      s"ULID value ($string) should be less than ULID.Max (7ZZZZZZZZZZZZZZZZZZZZZZZZZ)"
  }

  case class InvalidBytesLength(size: Int, expectedSize: Int)
    extends ULIDBytesParsingError {
    override def getMessage: String =
      s"Chunk[Byte] size ($size) must be of size $expectedSize"
  }

  case class RandomOverflow(timestamp: Long) extends ULIDGenerationError {
    override def getMessage: String =
      s"Random number overflow at timestamp $timestamp. Wait for one millisecond and retry"
  }

  case class InvalidTimestamp(value: Long) extends ULIDGenerationError with ULIDBytesParsingError {
    override def getMessage: String =
      s"Timestamp value ($value) must be between ${BinaryULID.minTimestamp} and ${BinaryULID.maxTimestamp}"
  }

}

