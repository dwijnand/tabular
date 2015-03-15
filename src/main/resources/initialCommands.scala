import net.mox9.tabular._

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
