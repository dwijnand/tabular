import sbt._, Keys._

import scala.language.implicitConversions

object Build extends Build {
  val tabular = project in file(".") settings (
    version in ThisBuild := "0.1-SNAPSHOT",

    licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),

    scalaVersion := "2.11.6",
    crossScalaVersions := Seq(scalaVersion.value),

    scalacOptions ++= Seq("-encoding", "utf8"),
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint"),
    scalacOptions  += "-language:postfixOps",
    scalacOptions  += "-Xfatal-warnings",
    scalacOptions  += "-Xfuture",
    scalacOptions  += "-Yinline-warnings",
    scalacOptions  += "-Yno-adapted-args",
    scalacOptions  += "-Ywarn-dead-code", // WARN: Too many ???s cause false positives!
    scalacOptions  += "-Ywarn-numeric-widen",
    scalacOptions  += "-Ywarn-unused-import",
    scalacOptions  += "-Ywarn-value-discard",

    maxErrors := 5,
    triggeredMessage := Watched.clearWhenTriggered,

    watchSources ++= (baseDirectory.value * "*.sbt").get,
    watchSources ++= (baseDirectory.value / "project" * "*.scala").get)
}
