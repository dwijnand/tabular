package tabular

import utest._

object TabularTestAux {
  val m = scala.collection.immutable.ListMap(
    "a" -> 1,
    "bb" -> 2,
    "ccc" -> 3,
    "dd" -> 4,
    "e" -> 5
  )

  final case class Ver(x: Int, y: Int, z: Int)       { override def toString = s"$x.$y.$z"     }
  final case class Dep(g: String, a: String, v: Ver) { override def toString = s"$g % $a % $v" }

  val deps = Seq(
    Dep("com.example.foo", "foo-server", Ver(1, 2, 103)),
    Dep("com.acme.bar", "bar-scala-sdk", Ver(12, 0, 1))
  )
}

object TabularTests extends TestSuite {
  import TabularTestAux._
  val tests = TestSuite {
    'showkv {
      val actual = m.showkv mkString "\n"
      val l1 = "  a: 1"
      val l2 = " bb: 2"
      val l3 = "ccc: 3"
      val l4 = " dd: 4"
      val l5 = "  e: 5"
      val expect = Seq(l1, l2, l3, l4, l5) mkString "\n"
      assert(actual == expect)
    }
    'tabular {
      val actual = deps tabular (_.g.rj, "%%", _.a.cj, '%', _.v) mkString "\n"
      val l1 = "com.example.foo %%  foo-server   % 1.2.103"
      val l2 = "   com.acme.bar %% bar-scala-sdk % 12.0.1 "
      val expect = Seq(l1, l2) mkString "\n"
      assert(actual == expect)
    }
  }
}
