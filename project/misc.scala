import sbt._, Keys._

object SbtMisc {
  // Replace with Def.settings in 0.13.10+
  def Settings(settings: SettingsDefinition*): Seq[Setting[_]] = settings flatMap (_.settings)

  def scalaPartV = Def setting (CrossVersion partialVersion scalaVersion.value)

  implicit final class AnyWithIfScala10[A](val __x: A) {
    def ifScala210     = Def setting (scalaPartV.value collect { case (2, 10)           => __x })
    def ifScala211Plus = Def setting (scalaPartV.value collect { case (2, y) if y >= 11 => __x })
  }
}
