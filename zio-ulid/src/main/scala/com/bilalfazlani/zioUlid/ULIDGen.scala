package com.bilalfazlani.zioUlid

import java.util.concurrent.TimeUnit
import zio._
import zio.direct._

trait ULIDGen {
  def nextULID: UIO[ULID]
}

object ULIDGen {
  def live = ZLayer
    .fromZIO(Ref.Synchronized.make(BinaryULID.empty))
    .map(s => ZEnvironment(ULIDGenLive(s.get)))
  def nextULID = ZIO.serviceWithZIO[ULIDGen](_.nextULID)
}

case class ULIDGenLive(state: Ref.Synchronized[BinaryULID]) extends ULIDGen {
  private def generateBinary: UIO[BinaryULID] =
    state.updateAndGetZIO { lastState =>
      for {
        currentMillis <- ZIO.clockWith(_.currentTime(TimeUnit.MILLISECONDS)).run
        lastTimestamp = lastState.timestamp
        differentTime = currentMillis != lastTimestamp
        result <-
          if (differentTime) {
            // No conflict at millisecond level. We can generate a new ULID safely
            val bin: IO[ULIDBytesParsingError.InvalidTimestamp, BinaryULID] =
              BinaryULID(currentMillis)
            bin.orDie
          } else
          // do increment
          if (lastState.low != ~0L)
            BinaryULID(lastState.high, lastState.low + 1L)
          else {
            val nextHi = (lastState.high & ~(~0L << 16)) + 1
            if ((nextHi & (~0L << 16)) != 0)
              // Random number overflow. Wait for one millisecond and retry
              ZIO.sleep(1.millis) *> generateBinary
            else BinaryULID(nextHi | currentMillis << (64 - 48), 0)
          }
      } yield result
    }

  def nextULID = generateBinary.map(ULID.unsafe)
}
