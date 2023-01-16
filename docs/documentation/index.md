---
description: "Example of using zio-ulid"
---

# Getting started

## Generating new ULIDs

Here is an example of generating a ULID. Generating ULIDs require a `ULIDGen` which is a `ZLayer`. You can create a `ULIDGen` using `ULIDGen.live`.

<!--codeinclude-->
[Generating ULIDs](../../examples/src/main/scala/Examples.scala) inside_block:BasicExample
<!--/codeinclude-->

`.toString` gives us the string representation of ULID

!!! warning
    If system datetime is set to more than August 10,889, then fiber will die with `UnsupportedSystemDateTime` exception

!!! important 
    Since `ULIDGen` [is a stateful layer](generation.md), you should have only one instance of `ULIDGen` in your application. You can ensure that by using `ULIDGen.live` only once in your application, ideally at at the start/root of your application.


## Timestamp

To know when a ULID was generated, we can get the timestamp from a ULID. Its a `Long` value representing Unix epoch milliseconds. 

<!--codeinclude-->
[timestammp](../../examples/src/main/scala/Examples.scala) inside_block:timestamp_code
<!--/codeinclude-->

## Comparing and sorting ULIDs

Since ULIDs are lexicographically sortable, we can compare them with each other and sort them.

<!--codeinclude-->
[Comparing and sorting](../../examples/src/main/scala/Examples.scala) inside_block:CompareAndSort 
<!--/codeinclude-->

## Binary encoding

There are two binary representations of ULIDs. One is a `Chunk[Byte]` of size 16 and the other a tuple (pair) of two 64 bit `Long` values.

<!--codeinclude-->
[Encoding ULIDs](../../examples/src/main/scala/Examples.scala) inside_block:Encoding
<!--/codeinclude-->

## Converting a `Tuple[Long, Long]` to ULID

You can easily convert a tuple of two `Long`s into a ULID

<!--codeinclude-->
[tuple convert](../../examples/src/main/scala/Examples.scala) inside_block:DecodingFromTuple
<!--/codeinclude-->

This is a direct operation and does not need any validations as any two Longs are a valid ULID

## Parsing a String

Parsing a string into a ULID returns `Either[ULIDStringParsingError, ULID]`. It can fail with following errors

- `ULIDStringParsingError.InvalidLength` when string length is not 26
- `ULIDStringParsingError.InvalidCharacters` when string contains characters which are not supported by [encoding](structure-encoding.md)
- `ULIDStringParsingError.OverflowValue` when string contains a value which is greater than 128 bits

<!--codeinclude-->
[parse from string](../../examples/src/main/scala/Examples.scala) inside_block:parse_from_string
<!--/codeinclude--> 

It is also possible to validate a string using pattern matching

<!--codeinclude-->
[validate string](../../examples/src/main/scala/Examples.scala) inside_block:parse_string_from_pattern_matching 
<!--/codeinclude-->

## Parsing chunk of bytes

When parsing bytes into a ULID, validation of chunk size is performed. If size is not 16, then `Left[ULIDBytesParsingError.InvalidBytesLength]` is returned.

 <!--codeinclude-->
[parsing byte chunk](../../examples/src/main/scala/Examples.scala) inside_block:parse_from_bytes
<!--/codeinclude-->

Similar to validation of Strings, its also possible to validate bytes using pattern matching

<!--codeinclude-->
[validate bytes](../../examples/src/main/scala/Examples.scala) inside_block:parse_bytes_from_pattern_matching
<!--/codeinclude-->  

## Parsing `Chunk[Byte]` with separate timestamp

You can create a ULID from a timestamp (`Long`) and a `Chunk[Byte]`. Chunk should be of size 10. Timestamp should be <= 48 bits.

<!--codeinclude-->
[create from timestamp and random bytes](../../examples/src/main/scala/Examples.scala) inside_block:from_timestamp_and_bytes
<!--/codeinclude-->

This will return an `Either[ULIDBytesParsingError, ULID]` with following errors

- `ULIDBytesParsingError.InvalidBytesLength` when chunk size is not 10
- `ULIDBytesParsingError.InvalidTimestamp` when timestamp is greater than 48 bits
