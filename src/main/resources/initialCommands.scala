import net.mox9.tabular._

val m = Map("a" -> 1, "bb" -> 2, "ccc" -> 3)

val mm = Map("a" -> Seq(1), "bb" -> Seq(2, 2), "ccc" -> Seq(3, 3, 3))

case class Ver(major: Int, minor: Int, patch: Int) {
  override def toString = s"$major.$minor.$patch"
}
case class Dep(g: String, a: String, v: Ver) {
  override def toString = s"$g % $a % $v"
}

val deps = Seq(
  Dep("com.example.foo", "foo-server", Ver(1, 2, 3)),
  Dep("com.acme.bar", "bar-scala-sdk", Ver(12, 0, 1))
)
