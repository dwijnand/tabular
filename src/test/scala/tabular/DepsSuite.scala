package tabular

import utest._

final case class Ver(x: Int, y: Int, z: Int)       { override def toString = s"$x.$y.$z"     }
final case class Dep(g: String, a: String, v: Ver) { override def toString = s"$g % $a % $v" }

object Deps {
  val deps = Seq(
    Dep("com.example.foo", "foo-server", Ver(1, 2, 103)),
    Dep("com.acme.bar", "bar-scala-sdk", Ver(12, 0, 1))
  )
}

object DepsSuite extends TestSuite {
  val tests = TestSuite {
    'deps {
      val actual = Deps.deps tabular(_.g.rj, _.a, _.v) mkString "\n"
      val l1 = "com.example.foo foo-server    1.2.103"
      val l2 = "   com.acme.bar bar-scala-sdk 12.0.1 "
      val expect = l1 + "\n" + l2
      assert(actual == expect)
    }
  }
}
