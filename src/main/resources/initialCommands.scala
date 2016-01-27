import tabular._

val m = scala.collection.immutable.ListMap(
     "a" -> 1,
    "bb" -> 2,
   "ccc" -> 3,
  "dddd" -> 4,
    "ee" -> 5,
     "f" -> 6
)

val headers = scala.collection.immutable.ListMap(
       "Cache-Control" -> Seq("no-cache", "no-store"),
        "Content-Type" -> Seq("text/html; charset=UTF-8"),
            "Location" -> Seq("http://www.google.co.uk/?gfe_rd=cr&ei=E5L3VJG_JoKr7AaKtoDICw"),
      "Content-Length" -> Seq("261"),
                "Date" -> Seq("Wed, 04 Mar 2015 23:15:31 GMT"),
              "Server" -> Seq("GFE/2.0"),
  "Alternate-Protocol" -> Seq("80:quic,p=0.08")
)


case class Ver(major: Int, minor: Int, patch: Int) {
  override def toString = s"$major.$minor.$patch"
}
case class Dep(g: String, a: String, v: Ver) {
  override def toString = s"$g % $a % $v"
}

val deps = Seq(
  Dep("com.example.foo", "foo-server", Ver(1, 2, 103)),
  Dep("com.acme.bar", "bar-scala-sdk", Ver(12, 0, 1))
)

implicit class AnyW[A](private val x: A) extends AnyVal {
  def >> : Unit = println(x)
}
