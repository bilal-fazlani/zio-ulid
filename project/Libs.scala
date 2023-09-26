import sbt._

object Libs {
  val zioVersion = "2.0.17"
}

object BenchmarkLibs {
  lazy val ScalaUlid = ("com.chatwork" %% "scala-ulid" % "1.0.24")
    .withCrossVersion(CrossVersion.for3Use2_13)
  lazy val SulkyUlid = "de.huxhorn.sulky" % "de.huxhorn.sulky.ulid" % "8.3.0"
  lazy val ULID4S = ("net.petitviolet" %% "ulid4s" % "0.5.0").withCrossVersion(
    CrossVersion.for3Use2_13
  )
  lazy val AirframeULID = "org.wvlet.airframe" %% "airframe-ulid" % "23.9.2"
  lazy val ULIDCreator = "com.github.f4b6a3" % "ulid-creator" % "5.2.2"
}
