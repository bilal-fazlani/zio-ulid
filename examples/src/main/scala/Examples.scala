package examples

import com.bilalfazlani.zioUlid._

import zio._
import java.time.ZonedDateTime
import java.time.Instant
import java.time.ZoneId
import com.bilalfazlani.zioUlid.ULIDStringParsingError._

object BasicExample extends ZIOAppDefault {
  import com.bilalfazlani.zioUlid._

  val program = for {
    // make a new ULID
    ulid <- ULID.nextULID

    // .toString gives a string representation of the ULID
    _ <- Console.printLine(ulid.toString)
  } yield ()

  val run = program.provide(ULIDGen.live)
}

object Timestamp {

  val ulid: ULID = ???

  // timestamp_code{
  // get the timestamp of a ULID
  val timestampMillis: Long = ulid.timestamp

  // convert the millis to a isntant or datetime
  val datetime = ZonedDateTime.ofInstant(
    Instant.ofEpochMilli(timestampMillis),
    ZoneId.of("Z")
  )
  // }
}

object CompareAndSort {
  import com.bilalfazlani.zioUlid.ULID

  val program =
    for {
      // make new ULIDs
      ulid1 <- ULID.nextULID
      ulid2 <- ULID.nextULID

      // compare two ULIDs
      equals = ulid1 == ulid2
      lessThan = ulid1 < ulid2
      greaterThan = ulid1 > ulid2

      // sort ULIDs
      sortedUlids = List(ulid2, ulid1).sorted
    } yield ()
}

object Encoding {
  import com.bilalfazlani.zioUlid.ULID

  val program = for {
    // make a new ULID
    ulid <- ULID.nextULID

    // get a bytes representation of a ULID
    bytes: Chunk[Byte] = ulid.bytes

    // get a tuple (high, low) representation of a ULID
    tuple: (Long, Long) = ulid.tuple
  } yield ()
}

object DecodingFromString {

  // parse_from_string {
  import com.bilalfazlani.zioUlid.{ULID, ULIDStringParsingError}
  import ULIDStringParsingError._

  private def getInputString: String = ???

  // decode a ULID from a string
  val ulid: Either[ULIDStringParsingError, ULID] = ULID(getInputString)

  ulid.fold(
    {
      case InvalidCharacters(_) =>
        println("Invalid characters in the string")
      case InvalidLength(_) =>
        println(s"Invalid length of string")
      case OverflowValue(string) =>
        println(s"Overflow value $string")
    },
    ulid => println(ulid.toString)
  )
  // }

  // parse_string_from_pattern_matching {
  // validate a string as a ULID using pattern matching
  getInputString match {
    case ULID(string)  => println(s"Valid ULID: $string")
    case invalidString => println(s"Invalid ULID: $invalidString")
  }
  // }
}

object DecodingFromBytes {
  // parse_from_bytes {
  import com.bilalfazlani.zioUlid._

  private def getInputBytes: Chunk[Byte] = ???

  // decode a ULID from bytes
  val ulid: Either[ULIDBytesParsingError.InvalidBytesLength, ULID] = ULID(
    getInputBytes
  )
  // }

  // parse_bytes_from_pattern_matching {
  // validate bytes as a ULID using pattern matching
  getInputBytes match {
    case validBytes @ ULID(string) => println("Valid ULID: " + string)
    case invalidBytes              => println("bytes are invalid")
  }
  // }
}

object FromTimestampAndBytes {
  private def getTimestamp: Long = ???
  private def getRandomBytes: Chunk[Byte] = ???

  // from_timestamp_and_bytes {
  import com.bilalfazlani.zioUlid._

  // create a ULID from a timestamp and bytes
  val ulid: Either[ULIDBytesParsingError, ULID] =
    ULID(getTimestamp, getRandomBytes)
  // }
}

object DecodingFromTuple {
  // decode a ULID from a tuple
  val ulid: ULID = ULID(123412312L, 2134234423L)
}
