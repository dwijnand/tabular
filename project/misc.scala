import scala.language.implicitConversions

import scala.collection.immutable

import sbt._, Keys._

sealed class Licence(val name: String, val url: URL)

final case object Apache2 extends Licence("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0"))

object MiscPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  object autoImport {
    val licences = settingKey[Seq[Licence]]("Project (typed) licences, UK spelling")
    val scala211 = settingKey[String]("")
    val scala210 = settingKey[String]("")

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

    def wordSeq(s: String): immutable.Seq[String] = (s split "\\s+" filterNot (_ == "")).to[immutable.Seq]

    implicit object AddRemoveWords extends AnyRef
        with Append.Values[Seq[String], String]
        with Remove.Values[Seq[String], String]
    {
      def appendValues(a: Seq[String], b: String): Seq[String] = a ++ wordSeq(b)
      def removeValues(a: Seq[String], b: String): Seq[String] = a filterNot wordSeq(b).contains
  }

    implicit object RemoveString extends Remove.Value[String, String] {
      def removeValue(a: String, b: String) = a.replace(b, "")
    }

    implicit final class AnyWithForScalaVersion[A](val _o: A) {
      def ifScala(p: Int => Boolean) = scalaPartV(_ collect { case (2, y) if p(y) => _o })
      def ifScalaLte(v: Int) = ifScala(_ <= v)
      def ifScalaMag(v: Int) = ifScala(_ == v)
      def ifScalaGte(v: Int) = ifScala(_ >= v)
      def for212Plus(alt: => A) = ifScalaLte(11)(_ getOrElse alt)
    }

    implicit final class ModuleIDWithCompilerPlugin(val _m: ModuleID) extends AnyVal {
      def compilerPlugin(): ModuleID = sbt.compilerPlugin(_m)
    }

    def inGlobal(ss: SettingsDefinition*): Seq[Setting[_]] = inScope(Global)(Def settings (ss: _*))

    implicit final class SettingsWithIn(val _ss: Seq[Setting[_]]) extends ScopingSetting2[Seq[Setting[_]]] {
      def in(s: Scope) = inScope(s)(_ss)
    }
  }
  import autoImport._

  override def projectSettings = Seq(
    scalacOptions in Compile in console := (scalacOptions in console).value,
    scalacOptions in Test    in console := (scalacOptions in console).value
  )

  override def buildSettings = Seq(
    watchSources ++= (baseDirectory.value * "*.sbt").get,
    watchSources ++= (baseDirectory.value / "project" * "*.scala").get
  )

  override def globalSettings = Seq(
    licenses := Nil,
    Def derive (licenses := licences.value map (l => l.name -> l.url)),
    Def derive (homepage := scmInfo.value map (_.browseUrl))
  )
}

trait ScopingSetting2[Result] {
  def in(s: Scope): Result

  def in(p: Reference): Result                                                                    = in(Select(p), This, This)
  def in(t: Scoped): Result                                                                       = in(This, This, Select(t.key))
  def in(c: ConfigKey): Result                                                                    = in(This, Select(c), This)
  def in(c: ConfigKey, t: Scoped): Result                                                         = in(This, Select(c), Select(t.key))
  def in(p: Reference, c: ConfigKey): Result                                                      = in(Select(p), Select(c), This)
  def in(p: Reference, t: Scoped): Result                                                         = in(Select(p), This, Select(t.key))
  def in(p: Reference, c: ConfigKey, t: Scoped): Result                                           = in(Select(p), Select(c), Select(t.key))
  def in(p: ScopeAxis[Reference], c: ScopeAxis[ConfigKey], t: ScopeAxis[AttributeKey[_]]): Result = in(Scope(p, c, t, This))
}
