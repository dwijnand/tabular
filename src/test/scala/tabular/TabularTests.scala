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

  val headers = scala.collection.immutable.ListMap(
         "Cache-Control" -> Seq("no-cache", "no-store"),
          "Content-Type" -> Seq("text/html; charset=UTF-8"),
              "Location" -> Seq("http://www.google.co.uk/?gfe_rd=cr&ei=E5L3VJG_JoKr7AaKtoDICw"),
        "Content-Length" -> Seq("261"),
                  "Date" -> Seq("Wed, 04 Mar 2015 23:15:31 GMT"),
                "Server" -> Seq("GFE/2.0"),
    "Alternate-Protocol" -> Seq("80:quic,p=0.08")
  )

  final case class Ver(x: Int, y: Int, z: Int)       { override def toString = s"$x.$y.$z"     }
  final case class Dep(g: String, a: String, v: Ver) { override def toString = s"$g % $a % $v" }

  val deps = Seq(
    Dep("com.example.foo", "foo-server", Ver(1, 2, 103)),
    Dep("com.acme.bar", "bar-scala-sdk", Ver(12, 0, 1))
  )

  final case class Foo(i: Int, str: String, bool: Boolean)

  val xs = Seq(Foo(23, "foo", true), Foo(0, "", false), Foo(999, "quiz", true))

  val matrix = Seq(
    Seq(1, 2, 3),
    Seq(4, 5, 6),
    Seq(7, 8, 9)
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
    'showkvs {
      val actual = headers.showkvs mkString "\n"
      val l1 = "     Cache-Control: no-cache, no-store                                          "
      val l2 = "      Content-Type: text/html; charset=UTF-8                                    "
      val l3 = "          Location: http://www.google.co.uk/?gfe_rd=cr&ei=E5L3VJG_JoKr7AaKtoDICw"
      val l4 = "    Content-Length: 261                                                         "
      val l5 = "              Date: Wed, 04 Mar 2015 23:15:31 GMT                               "
      val l6 = "            Server: GFE/2.0                                                     "
      val l7 = "Alternate-Protocol: 80:quic,p=0.08                                              "
      val expect = Seq(l1, l2, l3, l4, l5, l6, l7) mkString "\n"
      assert(actual == expect)
    }
    'tabular {
      val actual = deps tabular (_.g.rj, "%%", _.a.cj, '%', _.v) mkString "\n"
      val l1 = "com.example.foo %%  foo-server   % 1.2.103"
      val l2 = "   com.acme.bar %% bar-scala-sdk % 12.0.1 "
      val expect = Seq(l1, l2) mkString "\n"
      assert(actual == expect)
    }
    'showPs {
      val actual = xs.showPs mkString "\n"
      val l0 = "  i  str  bool"
      val l1 = " 23  foo  true"
      val l2 = "  0      false"
      val l3 = "999 quiz  true"
      val expect = Seq(l0, l1, l2, l3) mkString "\n"
      assert(actual == expect)
    }
    'showM {
      val actual = matrix.showM mkString "\n"
      val l1 = "1 2 3"
      val l2 = "4 5 6"
      val l3 = "7 8 9"
      val expect = Seq(l1, l2, l3) mkString "\n"
      assert(actual == expect)
    }
  }
}
