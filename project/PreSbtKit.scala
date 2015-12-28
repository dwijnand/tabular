import sbt._
import sbt.Def.Initialize
import sbt.Scoped.DefinableTask

object SbtKitPre {
  implicit class DefinableTaskWithRemove[A](val _t: DefinableTask[Seq[A]]) extends AnyVal {
    def -=(x: A): Setting[Task[Seq[A]]] = _t ~= (_ filterNot x.==)
  }

  implicit class SettingKeyOps[A](val _s: SettingKey[A]) extends AnyVal {
    def mapValue[B](f: A => B): Initialize[B] = Def setting f(_s.value)
  }
}
