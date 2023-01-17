package com.bilalfazlani.zioUlid
package benchmarks

import scala.concurrent.ExecutionContext
import zio.*

object BenchmarkUtil extends Runtime[Any] { self =>
  val environment = Runtime.default.environment

  val fiberRefs = Runtime.default.fiberRefs

  val runtimeFlags = Runtime.default.runtimeFlags

  def unsafeRun[E, A](zio: ZIO[Any, E, A]): A =
    Unsafe.unsafe(implicit unsafe =>
      self.unsafe.run(zio).getOrThrowFiberFailure()
    )
}
