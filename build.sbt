import SbtMisc._

lazy val tabular = project in file(".")

organization := "com.dwijnand"
        name := "tabular"
     version := "0.1.0-SNAPSHOT"
    licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
 description := "A way to show data in tabular form"
    homepage := Some(url("https://github.com/dwijnand/tabular"))

val scala211 = settingKey[String]("")
val scala210 = settingKey[String]("")
          scala211 := "2.11.7"
          scala210 := "2.10.6"
      scalaVersion := scala211.value
crossScalaVersions := Seq(scala211.value, scala210.value)
// TODO: Consider adding support for Scala 2.12 & Scala.js

scalacOptions ++= Seq("-encoding", "utf8")
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")
scalacOptions  += "-language:higherKinds"
scalacOptions  += "-language:implicitConversions"
scalacOptions  += "-language:postfixOps"
scalacOptions  += "-Xfuture"
scalacOptions  += "-Yinline-warnings"
scalacOptions  += "-Yno-adapted-args"
scalacOptions  += "-Ywarn-dead-code"
scalacOptions  += "-Ywarn-numeric-widen"
scalacOptions ++= "-Ywarn-unused".ifScala211Plus.value.toList
scalacOptions ++= "-Ywarn-unused-import".ifScala211Plus.value.toList
scalacOptions  += "-Ywarn-value-discard"
// TODO: Consider no predef and no import

scalacOptions in (Compile, console) -= "-Ywarn-unused-import"
scalacOptions in (Test,    console) -= "-Ywarn-unused-import"

wartremoverWarnings ++= Warts.unsafe
wartremoverWarnings  += Wart.Enumeration
wartremoverWarnings  += Wart.ExplicitImplicitTypes
wartremoverWarnings  += Wart.FinalCaseClass
wartremoverWarnings  += Wart.JavaConversions
wartremoverWarnings  += Wart.MutableDataStructures
wartremoverWarnings ++= Wart.NoNeedForMonad.ifScala211Plus.value.toList // bombs b/c uses quasiquotes #106
wartremoverWarnings  += Wart.Nothing
wartremoverWarnings  += Wart.Option2Iterable
wartremoverWarnings  -= Wart.Any                    // bans f-interpolator #158
wartremoverWarnings  -= Wart.DefaultArguments
wartremoverWarnings  -= Wart.NonUnitStatements      // bans this.type #118
wartremoverWarnings  -= Wart.Product
wartremoverWarnings  -= Wart.Serializable
wartremoverWarnings  -= Wart.Throw
wartremoverWarnings  -= Wart.ToString // TODO: Add TryShow (non default unsafe wart)

       maxErrors := 5
triggeredMessage := Watched.clearWhenTriggered

dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value // sbt/sbt#2286
dependencyOverrides += "org.scala-lang" % "scala-library"  % scalaVersion.value
dependencyOverrides += "org.scala-lang" % "scala-reflect"  % scalaVersion.value
dependencyOverrides += "org.scala-lang" % "scalap"         % scalaVersion.value

libraryDependencies += "com.lihaoyi" %% "utest" % "0.3.1" % "test"

testFrameworks += new TestFramework("utest.runner.Framework")

initialCommands in console += "\n" + IO.read((resourceDirectory in Compile).value / "initialCommands.scala")

parallelExecution in Test := true
fork in Test := false

fork in run := true
cancelable in Global := true

pomExtra := pomExtra.value ++ {
    <developers>
        <developer>
            <id>dwijnand</id>
            <name>Dale Wijnand</name>
            <email>dale wijnand gmail com</email>
            <url>dwijnand.com</url>
        </developer>
    </developers>
    <scm>
        <connection>scm:git:github.com/dwijnand/tabular.git</connection>
        <developerConnection>scm:git:git@github.com:dwijnand/tabular.git</developerConnection>
        <url>https://github.com/dwijnand/tabular</url>
    </scm>
}

releaseCrossBuild := true

watchSources ++= (baseDirectory.value * "*.sbt").get
watchSources ++= (baseDirectory.value / "project" * "*.scala").get
