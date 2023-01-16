import sbt._

object Libs {
  private lazy val zioVersion = "2.0.5"

  lazy val zio = "dev.zio" %% "zio" % zioVersion
  lazy val zioDirect = "dev.zio" %% "zio-direct" % "1.0.0-RC3"
  lazy val zioTest = "dev.zio" %% "zio-test" % zioVersion
  lazy val zioTestSbt = "dev.zio" %% "zio-test-sbt" % zioVersion
}

object BenchmarkLibs {
  lazy val ScalaUlid = ("com.chatwork" %% "scala-ulid" % "1.0.24")
    .withCrossVersion(CrossVersion.for3Use2_13)
  lazy val SulkyUlid = "de.huxhorn.sulky" % "de.huxhorn.sulky.ulid" % "8.2.0"
  lazy val ULID4S = ("net.petitviolet" %% "ulid4s" % "0.4.1").withCrossVersion(
    CrossVersion.for3Use2_13
  )
  lazy val AirframeULID = "org.wvlet.airframe" %% "airframe-ulid" % "23.1.1"
  lazy val ULIDCreator = "com.github.f4b6a3" % "ulid-creator" % "5.1.0"
}
