import org.scalajs.sbtplugin.cross.CrossProject

lazy val tabular = project in file(".") aggregate (tabularJVM, tabularJS)

val tabularCross = CrossProject("tabular", file("."), CrossType.Pure)
val tabularJVM   = tabularCross.jvm
val tabularJS    = tabularCross.js

organization in Global := "com.dwijnand"
        name in Global := "tabular"
    licences in Global := Seq(Apache2)
   startYear in Global := Some(2015)
 description in Global := "A way to show data in tabular form"
  developers in Global := List(Developer("dwijnand", "Dale Wijnand", "dale wijnand gmail com", url("https://dwijnand.com")))
     scmInfo in Global := Some(ScmInfo(url(s"https://github.com/dwijnand/tabular"), "scm:git:git@github.com:dwijnand/tabular.git"))

          scala211 in Global := "2.11.8"
          scala210 in Global := "2.10.6"
      scalaVersion in Global := scala211.value
crossScalaVersions in Global := Seq(scala211.value, scala210.value)
// TODO: Consider adding support for Scala 2.12 & Scala.js

       maxErrors in Global := 15
triggeredMessage in Global := Watched.clearWhenTriggered

scalacOptions in Global ++= "-encoding utf8"
scalacOptions in Global ++= "-deprecation -feature -unchecked -Xlint"
scalacOptions in Global  += "-language:experimental.macros"
scalacOptions in Global  += "-language:higherKinds"
scalacOptions in Global  += "-language:implicitConversions"
scalacOptions in Global  += "-language:postfixOps"
scalacOptions in Global  += "-Xfuture"
scalacOptions in Global  += "-Yno-adapted-args"
scalacOptions in Global  += "-Ywarn-dead-code"
scalacOptions in Global  += "-Ywarn-numeric-widen"
scalacOptions in Global  += "-Ywarn-unused".ifScala211Plus.value
scalacOptions in Global  += "-Ywarn-unused-import".ifScala211Plus.value
scalacOptions in Global  += "-Ywarn-value-discard"

scalacOptions in Global in console -= "-Ywarn-unused-import"

wartremoverWarnings in Global ++= Warts.unsafe
wartremoverWarnings in Global  += Wart.Enumeration
wartremoverWarnings in Global  += Wart.ExplicitImplicitTypes
wartremoverWarnings in Global  += Wart.FinalCaseClass
wartremoverWarnings in Global  += Wart.JavaConversions
wartremoverWarnings in Global  += Wart.MutableDataStructures
wartremoverWarnings in Global ++= Wart.NoNeedForMonad.ifScala211Plus.value.toList // bombs b/c uses quasiquotes #106
wartremoverWarnings in Global  += Wart.Nothing
wartremoverWarnings in Global  += Wart.Option2Iterable
wartremoverWarnings in Global  -= Wart.Any                    // bans f-interpolator #158
wartremoverWarnings in Global  -= Wart.DefaultArguments
wartremoverWarnings in Global  -= Wart.NonUnitStatements      // bans this.type #118
wartremoverWarnings in Global  -= Wart.Product
wartremoverWarnings in Global  -= Wart.Serializable
wartremoverWarnings in Global  -= Wart.Throw
wartremoverWarnings in Global  -= Wart.ToString // TODO: Add TryShow (non default unsafe wart)

libraryDependencies in tabularJVM += "com.lihaoyi" %% "utest" % "0.3.1" % "test"
libraryDependencies in tabularJS  += "com.lihaoyi" %% "utest" % "0.3.1" % "test"

testFrameworks in Global += new TestFramework("utest.runner.Framework")

// initialCommands in Global in console += "\n" + IO.read((resourceDirectory in Compile).value / "initialCommands.scala")

             fork in Global in Test := false
      logBuffered in Global in Test := false
parallelExecution in Global in Test := true

      fork in Global in run := true
cancelable in Global        := true

releaseCrossBuild := true

// Force the root project to not share sources with the cross project, but still have base = file(".")
baseDirectory := file("./project/root")
