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

  // Remove with sbt 0.13.12+
  implicit def appendOption[T]: Append.Sequence[Seq[T], Option[T], Option[T]] =
    new Append.Sequence[Seq[T], Option[T], Option[T]] {
      def appendValue(a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a :+ _)
      def appendValues(a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a :+ _)
    }

  implicit def removeOption[T]: Remove.Value[Seq[T], Option[T]] with Remove.Values[Seq[T], Option[T]] =
    new Remove.Value[Seq[T], Option[T]] with Remove.Values[Seq[T], Option[T]] {
      def removeValue(a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a filterNot _.==)
      def removeValues(a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a filterNot _.==)
    }
}
