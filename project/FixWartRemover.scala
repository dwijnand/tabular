import sbt._, Keys._
import wartremover.WartRemover, WartRemover.autoImport._

object FixWartRemover extends AutoPlugin {
  override def requires = plugins.JvmPlugin && WartRemover
  override def trigger = allRequirements

  override def globalSettings = Seq(
    wartremoverWarnings := Nil
  )
}
