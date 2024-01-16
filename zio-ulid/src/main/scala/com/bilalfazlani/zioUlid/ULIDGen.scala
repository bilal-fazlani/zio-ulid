package com.bilalfazlani.zioUlid

import java.util.concurrent.TimeUnit
import zio._
import ULIDError._

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
        currentMillis <- ZIO.clockWith(_.currentTime(TimeUnit.MILLISECONDS))
        lastTimestamp = lastState.timestamp
        differentTime = currentMillis != lastTimestamp
        result <-
          (if (differentTime) {
            // No conflict at millisecond level. We can generate a new ULID safely
            val bin: IO[InvalidTimestamp, BinaryULID] =
              BinaryULID(currentMillis)
            bin.orDie
          } else
          // do increment
            if (lastState.low != ~0L)
              ZIO.succeed(BinaryULID(lastState.high, lastState.low + 1L))
            else {
              val nextHi = (lastState.high & ~(~0L << 16)) + 1
              if ((nextHi & (~0L << 16)) != 0)
              // Random number overflow. Wait for one millisecond and retry
                ZIO.sleep(1.millis) *> generateBinary
              else
                ZIO.succeed(BinaryULID(nextHi | currentMillis << (64 - 48), 0))
            })
      } yield result
    }

  def nextULID: UIO[ULID] = generateBinary.map(ULID.unsafe)

  def nextULIDFromEpoch(epochMillis: Long): IO[ULIDGenerationError, ULID] =
    state.updateAndGetZIO { lastUlid =>
      if (epochMillis != lastUlid.timestamp) {
        // No conflict at millisecond level. We can generate a new ULID safely
        BinaryULID(epochMillis)
      } else
      // do increment
        if (lastUlid.low != ~0L)
          ZIO.succeed(BinaryULID(lastUlid.high, lastUlid.low + 1L))
        else {
          val nextHi = (lastUlid.high & ~(~0L << 16)) + 1
          if ((nextHi & (~0L << 16)) != 0)
            ZIO.fail(RandomOverflow(epochMillis))
          else
            ZIO.succeed(BinaryULID(nextHi | epochMillis << (64 - 48), 0))
        }
    }.map(ULID.unsafe)
}
