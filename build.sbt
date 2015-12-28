val tabular = project in file(".")

organization := "com.dwijnand"
        name := "tabular"
     version := "0.1.0-SNAPSHOT"
    licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
 description := name.value

      scalaVersion := "2.11.7"
crossScalaVersions := Seq(scalaVersion.value)

       maxErrors := 5
triggeredMessage := Watched.clearWhenTriggered

scalacOptions ++= Seq("-encoding", "utf8")
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")
scalacOptions  += "-language:higherKinds"
scalacOptions  += "-language:implicitConversions"
scalacOptions  += "-language:postfixOps"
scalacOptions  += "-Xfuture"
scalacOptions  += "-Yinline-warnings"
scalacOptions  += "-Yno-adapted-args"
scalacOptions  += "-Ywarn-dead-code" // WARN: Too many ???s cause false positives!
scalacOptions  += "-Ywarn-numeric-widen"
scalacOptions  += "-Ywarn-unused-import"
scalacOptions  += "-Ywarn-value-discard"

scalacOptions in (Compile, console) -= "-Ywarn-unused-import"
scalacOptions in (Test,    console) -= "-Ywarn-unused-import"

wartremoverWarnings ++= Warts.unsafe
wartremoverWarnings  += Wart.Enumeration
wartremoverWarnings  += Wart.ExplicitImplicitTypes
wartremoverWarnings  += Wart.FinalCaseClass
wartremoverWarnings  += Wart.JavaConversions
wartremoverWarnings  += Wart.MutableDataStructures
wartremoverWarnings  += Wart.NoNeedForMonad
wartremoverWarnings  += Wart.Option2Iterable
wartremoverWarnings  += Wart.ToString
wartremoverWarnings  -= Wart.Any                    // bans f-interpolator #158
wartremoverWarnings  -= Wart.DefaultArguments
wartremoverWarnings  -= Wart.NonUnitStatements      // bans this.type #118
wartremoverWarnings  -= Wart.Product
wartremoverWarnings  -= Wart.Serializable
wartremoverWarnings  -= Wart.Throw

initialCommands in console += "\n" + IO.read((resourceDirectory in Compile).value / "initialCommands.scala")

parallelExecution in Test := true
fork in Test := false

fork in run := true
connectInput in run := true
cancelable in Global := true

sources in (Compile, doc) := Nil
publishArtifact in (Compile, packageDoc) := false

watchSources ++= (baseDirectory.value * "*.sbt").get
watchSources ++= (baseDirectory.value / "project" * "*.scala").get
