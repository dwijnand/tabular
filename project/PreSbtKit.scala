import sbt._
import sbt.Def.Initialize

object SbtKitPre {
  implicit class SettingKeyOps[A](val _s: SettingKey[A]) extends AnyVal {
    def mapValue[B](f: A => B): Initialize[B] = Def setting f(_s.value)
  }
}
