import sbt._, Keys._

object SbtMisc {
  // Replace with Def.settings in 0.13.10+
  def Settings(settings: SettingsDefinition*): Seq[Setting[_]] = settings flatMap (_.settings)

  def scalaPartV = Def setting (CrossVersion partialVersion scalaVersion.value)

  implicit final class AnyWithIfScala10[A](val __x: A) {
    def ifScala210     = Def setting (scalaPartV.value collect { case (2, 10)           => __x })
    def ifScala211Plus = Def setting (scalaPartV.value collect { case (2, y) if y >= 11 => __x })
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
}
