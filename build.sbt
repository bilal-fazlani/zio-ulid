import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val scala3Version = "3.3.0"
val scala2Version = "2.13.12"

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
ThisBuild / homepage := Some(url("https://zio-ulid.bilal-fazlani.com/"))

lazy val root = project
  .in(file("."))
  .aggregate(
    `zio-ulid`.jvm,
    `zio-ulid`.js,
    benchmarks,
    examples
  )
  .settings(
    name := "zio-ulid-root",
    scalaVersion := scala2Version,
    publish / skip := true
  )

lazy val `zio-ulid` = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("./zio-ulid"))
  .settings(
    name := "zio-ulid",
    scalaVersion := scala2Version,
    crossScalaVersions := Seq(scala2Version, scala3Version),
    libraryDependencies ++= Seq(
      "dev.zio" %%% "zio" % Libs.zioVersion,
      "dev.zio" %%% "zio-test" % Libs.zioVersion,
      "dev.zio" %%% "zio-test-sbt" % Libs.zioVersion
    )
  )
  .jsSettings(
    scalaJSUseMainModuleInitializer := true
  )

lazy val benchmarks = project
  .in(file("./benchmarks"))
  .enablePlugins(JmhPlugin)
  .settings(
    name := "zio-ulid-benchmarks",
    scalaVersion := scala2Version,
    publish / skip := true,
    libraryDependencies ++= Seq(
      BenchmarkLibs.AirframeULID,
      BenchmarkLibs.ULID4S,
      BenchmarkLibs.ULIDCreator,
      BenchmarkLibs.ScalaUlid,
      BenchmarkLibs.SulkyUlid
    )
  )
  .dependsOn(`zio-ulid`.jvm)

lazy val examples = project
  .in(file("./examples"))
  .settings(
    name := "zio-ulid-examples",
    scalaVersion := scala2Version,
    publish / skip := true
  )
  .dependsOn(`zio-ulid`.jvm)
