val scala3Version = "3.2.2"

ThisBuild / organization := "com.bilal-fazlani"
ThisBuild / organizationName := "Bilal Fazlani"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/bilal-fazlani/zio-ulid"),
    "https://github.com/bilal-fazlani/zio-ulid.git"
  )
)
ThisBuild / developers := List(
  Developer(
    "bilal-fazlani",
    "Bilal Fazlani",
    "bilal.m.fazlani@gmail.com",
    url("https://bilal-fazlani.com")
  )
)
ThisBuild / licenses := List(
  "MIT License" -> url(
    "https://github.com/bilal-fazlani/zio-ulid/blob/main/license.md"
  )
)
ThisBuild / homepage := Some(url("https://github.com/bilal-fazlani/zio-ulid"))

ThisBuild / scalaVersion := scala3Version

lazy val root = project
  .in(file("."))
  .aggregate(`zio-ulid`, benchmarks, examples)
  .settings(
    name := "zio-ulid-root",
    publish / skip := true
  )

lazy val `zio-ulid` = project
  .in(file("./zio-ulid"))
  .settings(
    name := "zio-ulid",
    libraryDependencies ++= Seq(
      Libs.zio,
      Libs.zioDirect,
      Libs.zioTest,
      Libs.zioTestSbt
    )
  )

lazy val benchmarks = project
  .in(file("./benchmarks"))
  .enablePlugins(JmhPlugin)
  .settings(
    name := "zio-ulid-benchmarks",
    publish / skip := true,
    libraryDependencies ++= Seq(
      BenchmarkLibs.AirframeULID,
      BenchmarkLibs.ULID4S,
      BenchmarkLibs.ULIDCreator,
      BenchmarkLibs.ScalaUlid,
      BenchmarkLibs.SulkyUlid
    )
  )
  .dependsOn(`zio-ulid`)

lazy val examples = project
  .in(file("./examples"))
  .settings(
    name := "zio-ulid-examples",
    publish / skip := true
  )
  .dependsOn(`zio-ulid`)
