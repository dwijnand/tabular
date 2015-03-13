package pre

import sbt._, Def.Initialize, Scoped.DefinableTask

object SbtKit {
  implicit class DefinableTaskWithRemove[A](val _t: DefinableTask[Seq[A]]) extends AnyVal {
    def -=(x: A): Setting[Task[Seq[A]]] = _t ~= (_ filterNot x.==)
  }

  implicit final class SettingKeyOps[A](val key: SettingKey[A]) {
    def mapValue[B](f: A => B): Initialize[B] = Def setting f(key.value)
  }
}