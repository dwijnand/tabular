import tabular._

case class Version(major: Int, minor: Int, patch: Int)
case class GAV(g: String, a: String, v: Version)

val deps = Seq(
  GAV("com.example.foo", "foo-server", Version(1, 2, 3)),
  GAV("com.acme.bar", "bar-scala-sdk", Version(12, 0, 1))
)
