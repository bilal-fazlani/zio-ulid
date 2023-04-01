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
      defer {
        val currentMillis =
          ZIO.clockWith(_.currentTime(TimeUnit.MILLISECONDS)).run
        val lastTimestamp = lastState.timestamp
        val differentTime = currentMillis != lastTimestamp
        if (differentTime) {
          // No conflict at millisecond level. We can generate a new ULID safely
          val bin: IO[ULIDBytesParsingError.InvalidTimestamp, BinaryULID] =
            BinaryULID(currentMillis)
          bin.orDie.run
        } else
        // do increment
        if (lastState.low != ~0L) BinaryULID(lastState.high, lastState.low + 1L)
        else {
          val nextHi = (lastState.high & ~(~0L << 16)) + 1
          if ((nextHi & (~0L << 16)) != 0)
            // Random number overflow. Wait for one millisecond and retry
            (ZIO.sleep(1.millis) *> generateBinary).run
          else BinaryULID(nextHi | currentMillis << (64 - 48), 0)
        }
      }
    }

  def nextULID = generateBinary.map(ULID.unsafe)
}
