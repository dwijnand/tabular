import sbt._, Keys._

object MiscPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  object autoImport {
    def scalaPartV = scalaVersion(CrossVersion.partialVersion)

    implicit final class AnyWithIfScala10[A](val __x: A) {
      def ifScala210     = Def setting (scalaPartV.value collect { case (2, 10)           => __x })
      def ifScala211Plus = Def setting (scalaPartV.value collect { case (2, y) if y >= 11 => __x })
    }

    val noDocs = Def.settings(sources in (Compile, doc) := Nil, publishArtifact in (Compile, packageDoc) := false)
    val noSources = Def.settings(publishArtifact in (Compile, packageSrc) := false)
    val noPackage = Def.settings(Keys.`package` := file(""), packageBin := file(""), packagedArtifacts := Map())
    val noPublish = Def.settings(
      makePom         := file(""),
      deliver         := file(""),
      deliverLocal    := file(""),
      publish         := {},
      publishLocal    := {},
      publishM2       := {},
      publishArtifact := false,
      publishTo       := Some(Resolver.file("devnull", file("/dev/null")))
    )
    val noArtifacts = Def.settings(noPackage, noPublish)

    implicit def addRemoveOption[T]: AddRemoveOption[T] = new AddRemoveOption[T]
    class AddRemoveOption[T] extends AnyRef
        with Append.Sequence[Seq[T], Option[T], Option[T]]
        with Remove.Value[Seq[T], Option[T]] with Remove.Values[Seq[T], Option[T]]
    {
      def appendValue( a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a :+ _)
        def appendValues(a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a :+ _)

      def removeValue( a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a filterNot _.==)
        def removeValues(a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a filterNot _.==)
      }
  }
}
