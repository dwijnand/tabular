package tabular {

sealed trait TextAlign extends Any { def alignBy(width: Int): String }
case object LAlign extends TextAlign { def alignBy(width: Int) = leftFmt(width) }
case object RAlign extends TextAlign { def alignBy(width: Int) = rightFmt(width) }

final class LString(val _1: String) extends AnyVal with StrWithAlign {
  def _2 = LAlign
  def canEqual(that: Any): Boolean = that.isInstanceOf[LString]
}
final class RString(val _1: String) extends AnyVal with StrWithAlign {
  def _2 = RAlign
  def canEqual(that: Any): Boolean = that.isInstanceOf[RString]
}

}

package object tabular {
  type ->[+A, +B]   = Product2[A, B]
  type StrWithAlign = String -> TextAlign

  implicit class StringWithRJ(private val s: String) extends AnyVal {
    def lj = new LString(s)
    def rj = new RString(s)
  }

  implicit class TraversableW[A](private val xs: Traversable[A]) extends AnyVal {
    def tabular(fs: (A => String, TextAlign)*): String = {
      if (xs.isEmpty || fs.isEmpty) ""
      else {
        val rows0 = xs.toVector
        val cols0 = fs.toVector

        def cols = cols0 map (f => rows0 map (x => f._1(x)))
        def rows = rows0 map (x => cols0 map (f => f._1(x)))
        def renderLines = {
          val maxWidths = cols map (_ map (_.length) max)
          val colFmts = (cols0, maxWidths).zipped map ((col1, width) => col1._2 alignBy width)
          val rowFormat = colFmts mkString " "
          rows map (row => rowFormat.format(row: _*))
        }
        renderLines mkString "\n"
      }
    }
  }

  implicit class TraversableKVW[K, V](private val xs: Traversable[K -> V]) extends AnyVal {
    def kvFormat = rightFmt(xs map (_._1.toString.length) max) + ": %s"

    def showkv(vShow: V => String = _.toString): String = {
      val fmt = kvFormat
      xs map (kv => fmt.format (kv._1, vShow(kv._2))) mkString "\n"
    }
  }

  implicit class TraversableKMVW[K, V](private val xs: Traversable[K -> Traversable[V]]) extends AnyVal {
    def showkvs(sep: String = ", "): String = xs showkv (_ mkString sep)
  }

  def leftFmt(i: Int) = if (i == 0) "%s" else s"%-${i}s"
  def rightFmt(i: Int) = if (i == 0) "%s" else s"%${i}s"
}
