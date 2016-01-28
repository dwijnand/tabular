def Settings(settings: SettingsDefinition*): Seq[Setting[_]] = settings flatMap (_.settings)

lazy val tabular = project in file(".")

organization := "com.dwijnand"
        name := "tabular"
     version := "0.1.0-SNAPSHOT"
    licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
 description := name.value
    homepage := Some(url("https://github.com/dwijnand/tabular"))

      scalaVersion := "2.11.7"
crossScalaVersions := Seq(scalaVersion.value)
// TODO: Consider adding support for Scala 2.11, 2.12 & Scala.js

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
scalacOptions  += "-Ywarn-unused"
scalacOptions  += "-Ywarn-unused-import"
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
wartremoverWarnings  += Wart.NoNeedForMonad
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

initialCommands in console += "\n" + IO.read((resourceDirectory in Compile).value / "initialCommands.scala")

parallelExecution in Test := true
fork in Test := false

fork in run := true
cancelable in Global := true

noDocs

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

val noDocs = Settings(sources in (Compile, doc) := Nil, publishArtifact in (Compile, packageDoc) := false)
val noPackage = Settings(Keys.`package` := file(""), packageBin := file(""), packagedArtifacts := Map())
val noPublish = Settings(
  publishArtifact := false,
  publish         := {},
  publishLocal    := {},
  publishM2       := {},
  publishTo       := Some(Resolver.file("devnull", file("/dev/null")))
)
val noArtifacts = Settings(noPackage, noPublish)

watchSources ++= (baseDirectory.value * "*.sbt").get
watchSources ++= (baseDirectory.value / "project" * "*.scala").get
