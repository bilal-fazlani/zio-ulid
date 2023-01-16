package jmh

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.chatwork.scala.ulid.{ULID => cwULID}
import de.huxhorn.sulky.ulid.{ULID => sULID}
import net.petitviolet.ulid4s.{ULID => ULID4S}
import wvlet.airframe.ulid.{ULID => afULID}
import com.bilalfazlani.zioUlid.{ULID => zUlid}

import org.openjdk.jmh.annotations.{
  Benchmark,
  BenchmarkMode,
  Mode,
  OutputTimeUnit,
  Scope,
  State
}

@State(Scope.Benchmark)
@BenchmarkMode(Array(Mode.SampleTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
class ULIDBenchmark {

  // UUID
  @Benchmark
  def randomUUID_toString(): Unit = {
    UUID.randomUUID().toString
  }

  // ULID4S toString
  @Benchmark
  def ulid4s_ULID_newULID_toString() = {
    ULID4S.generate
  }

  // Airframe newULIDString
  @Benchmark
  def airframe_ULID_newULIDString(): Unit = {
    afULID.newULIDString
  }

  // Airframe newULID toString
  @Benchmark
  def airframe_ULID_newULID_toString(): Unit = {
    afULID.newULID.toString()
  }

  // ScalaULID asString
  @Benchmark
  def cwULID_generateULID_asString(): Unit = {
    cwULID.generate().asString
  }

  val sUlid = new sULID()

  // SulkyULID nextULID toString
  @Benchmark
  def sUlid_generateULID_toString(): Unit = {
    sUlid.nextValue().toString
  }

  // ZioULID nextULID toString
  @Benchmark
  def zUlid_generateULID_toString(): Unit = {
    zUlid.nextULID.map(_.toString)
  }

}
