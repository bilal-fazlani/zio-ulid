package com.bilalfazlani.zioUlid
package benchmarks

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.chatwork.scala.ulid.{ULID => cwULID}
import de.huxhorn.sulky.ulid.{ULID => sULID}
import net.petitviolet.ulid4s.{ULID => ULID4S}
import wvlet.airframe.ulid.{ULID => afULID}
import com.bilalfazlani.zioUlid.{ULID => zUlid}
import BenchmarkUtil.unsafeRun

import org.openjdk.jmh.annotations.{
  Benchmark,
  BenchmarkMode,
  Mode,
  OutputTimeUnit,
  Scope,
  State
}
import com.bilalfazlani.zioUlid.ULIDGen
import zio.ZIO
import zio.ZEnvironment
import zio.Ref

val zulid = ULIDGenLive(unsafeRun(Ref.Synchronized.make(BinaryULID.empty)))
val sUlid = new sULID()

@State(Scope.Benchmark)
@BenchmarkMode(Array(Mode.SampleTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class ULIDBenchmark {

  // // UUID
  // @Benchmark
  // def randomUUID_toString(): Unit = {
  //   unsafeRun {
  //     ZIO.succeed(UUID.randomUUID().toString)
  //   }
  // }

  // ZioULID nextULID toString
  @Benchmark
  def zUlid_nextULID_toString(): Unit = {
    unsafeRun {
      zulid.nextULID.map(_.toString)
    }
  }

  // // ULID4S toString
  // @Benchmark
  // def ulid4s_ULID_newULID_toString() = {
  //   unsafeRun {
  //     ZIO.succeed(ULID4S.generate)
  //   }
  // }

  // Airframe newULIDString
  @Benchmark
  def airframe_ULID_newULIDString(): Unit = {
    unsafeRun(ZIO.succeed(afULID.newULIDString))
  }

  // // Airframe newULID toString
  // @Benchmark
  // def airframe_ULID_newULID_toString(): Unit = {
  //   unsafeRun(ZIO.succeed(afULID.newULID.toString()))
  // }

  // // ScalaULID asString
  // @Benchmark
  // def cwULID_generateULID_asString(): Unit = {
  //   unsafeRun(ZIO.succeed(cwULID.generate().asString))
  // }

  // // SulkyULID nextULID toString
  // @Benchmark
  // def sUlid_generateULID_toString(): Unit = {
  //   unsafeRun(ZIO.succeed(sUlid.nextValue().toString))
  // }

}
