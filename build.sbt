import SbtKitPre._

val tabular = project in file(".")

organization := "com.dwijnand"
        name := "tabular"
     version := "0.1-SNAPSHOT"

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion := "2.11.7"
crossScalaVersions := Seq(scalaVersion.value)

scalacOptions ++= Seq("-encoding", "utf8")
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")
scalacOptions  += "-language:postfixOps"
scalacOptions  += "-Xfatal-warnings"
scalacOptions  += "-Xfuture"
scalacOptions  += "-Yinline-warnings"
scalacOptions  += "-Yno-adapted-args"
scalacOptions  += "-Ywarn-dead-code" // WARN: Too many ???s cause false positives!
scalacOptions  += "-Ywarn-numeric-widen"
scalacOptions  += "-Ywarn-unused-import"
scalacOptions  += "-Ywarn-value-discard"

// Let the REPL live
scalacOptions in (Compile, console) -= "-Xfatal-warnings"
scalacOptions in (Compile, console) -= "-Ywarn-unused-import"

maxErrors := 5
triggeredMessage := Watched.clearWhenTriggered

initialCommands in console <+= resourceDirectory in Compile mapValue (d => IO read d / "initialCommands.scala")

watchSources ++= (baseDirectory.value * "*.sbt").get
watchSources ++= (baseDirectory.value / "project" * "*.scala").get
