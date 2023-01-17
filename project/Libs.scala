import sbt._

object Libs {
  private lazy val zioVersion = "2.0.6"

  lazy val zio = "dev.zio" %% "zio" % zioVersion
  lazy val zioDirect = "dev.zio" %% "zio-direct" % "1.0.0-RC4"
  lazy val zioTest = "dev.zio" %% "zio-test" % zioVersion
  lazy val zioTestSbt = "dev.zio" %% "zio-test-sbt" % zioVersion
}
