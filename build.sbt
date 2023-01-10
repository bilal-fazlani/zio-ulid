val scala3Version = "3.2.1"

ThisBuild / organization     := "com.bilal-fazlani"
ThisBuild / organizationName := "Bilal Fazlani"

ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/bilal-fazlani/zio-ulid"), "https://github.com/bilal-fazlani/zio-ulid.git")
)
ThisBuild / developers := List(
  Developer(
    "bilal-fazlani",
    "Bilal Fazlani",
    "bilal.m.fazlani@gmail.com",
    url("https://bilal-fazlani.com")
  )
)
ThisBuild / licenses := List("MIT License" -> url("https://github.com/bilal-fazlani/zio-ulid/blob/main/license.md"))
ThisBuild / homepage := Some(url("https://github.com/bilal-fazlani/zio-ulid"))

lazy val `zio-ulid` = project
  .in(file("."))
  .settings(
    name := "zio-ulid",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      Libs.zio,
      Libs.zioDirect,
      Libs.zioTest,
      Libs.zioTestSbt
    )
  )
