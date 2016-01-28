package tabular

sealed trait TextAlign extends Any   { def alignBy(width: Int): String }
case object LAlign extends TextAlign { def alignBy(width: Int) = width.lalign }
case object RAlign extends TextAlign { def alignBy(width: Int) = width.ralign }

// Enables having an implicit conversion for Any without enriching Any with these methods
// Works because implicit conversions don't chain
sealed trait StrWithAlign    extends Any
sealed trait StrWithAlignOps extends Any with StrWithAlign {
  def str: String
  def align: TextAlign
}
final case class LString(val str: String) extends AnyVal with StrWithAlignOps {
  def align = LAlign
  def +(s: String) = new LString(str + s)
}
final case class RString(val str: String) extends AnyVal with StrWithAlignOps {
  def align = RAlign
  def +(s: String) = new RString(str + s)
}
object StrWithAlign {
  implicit def liftAny[A](x: A): StrWithAlign = x.lj
  implicit def liftOps(x: StrWithAlign): StrWithAlignOps = x match { case y: StrWithAlignOps => y }
}

final case class TravOnceWithMaxOpt[A](private val xs: TraversableOnce[A]) extends AnyVal {
  def maxOpt[B >: A](implicit cmp: Ordering[B]): Option[B] = if (xs.isEmpty) None else Some(xs max cmp)
}

// TODO: Consider making these return xs.types

final case class TravKVWithTabular[K, V](private val xs: Traversable[(K, V)]) extends AnyVal {
  def maxKeyLen: Option[Int] = xs.toIterator.map(_._1.toString.length).maxOpt
  def tabularkv: Traversable[String] = {
    xs.maxKeyLen.fold(Nil: Traversable[String]) { len =>
      val fmt = s"%${len}s %s"
      xs map (kv => fmt format(kv._1, kv._2))
    }
  }
  def showkv(): Unit = tabularkv foreach println
}

final case class TravKVsWithTabular[K, V](private val xs: Traversable[(K, Traversable[V])]) extends AnyVal {
  def tabularkvs: Traversable[String] = {
    xs.maxKeyLen.fold(Nil: Traversable[String]) { len =>
      val fmt = s"%${len}s %s"
      def showVs(vs: Traversable[V]) = if (vs.size == 1) vs.head else vs.mkString("[", ", ", "]")
      xs map (kv => fmt format(kv._1, showVs(kv._2)))
    }
  }
  def showkvs(): Unit = tabularkvs foreach println
}

trait Tabular {
  implicit def travOnceWithMaxOpt[A](xs: TraversableOnce[A])                  : TravOnceWithMaxOpt[A]    = new TravOnceWithMaxOpt[A](xs)
  implicit def travKVWithTabular[K, V](xs: Traversable[(K, V)])               : TravKVWithTabular[K, V]  = new TravKVWithTabular[K, V](xs)
  implicit def travKVsWithTabular[K, V](xs: Traversable[(K, Traversable[V])]) : TravKVsWithTabular[K, V] = new TravKVsWithTabular[K, V](xs)
}

final case class IntWithAlign(private val x: Int) extends AnyVal {
  def lalign: String = if (x == 0) "%s" else s"%-${x}s"
  def ralign: String = if (x == 0) "%s" else s"%${x}s"
}

final case class AnyWithTextAlign[A](private val x: A) extends AnyVal {
  def lj: LString = new LString(x.toString)
  def rj: RString = new RString(x.toString)
}

final case class TraversableW[A](private val xs: Traversable[A]) extends AnyVal {
  def tabular(columns: (A => StrWithAlign)*): String = {
    if (xs.isEmpty || columns.isEmpty) ""
    else {
      val values = xs.toVector
      val functions = columns.toVector

      def rows = values map (x => functions map (f => f(x).str))
      def cols = functions map (f => values map (x => f(x).str))
      def renderLines = {
        def widths = cols map (_ map (_.length) max)
        def aligns = functions map (_(values.head).align)
        val rowFormat = (widths, aligns).zipped map ((width, align) => align alignBy width) mkString " "
        rows map (row => rowFormat.format(row: _*))
      }
      renderLines mkString "\n"
    }
  }
}

final case class TraversableKVW[K, V](private val xs: Traversable[(K, V)]) extends AnyVal {
  def showkv(implicit vShow: V => String = _.toString): String = xs tabular (_._1.rj + ":", kv => vShow(kv._2))
}

final case class TraversableKMVW[K, V](private val xs: Traversable[(K, Traversable[V])]) extends AnyVal {
  def showkvs(implicit mvShow: Traversable[V] => String = _ mkString ", "): String = xs showkv mvShow
}

final case class AnyWith_>>[A](private val x: A) extends AnyVal {
  def >>() = println(x)
}

object `package` extends Tabular {
  implicit def intWithAlign(x: Int): IntWithAlign = new IntWithAlign(x)

  implicit def anyWithTextAlign[A](x: A): AnyWithTextAlign[A] = new AnyWithTextAlign[A](x)

  implicit def traversableW[A](xs: Traversable[A]): TraversableW[A] = new TraversableW[A](xs)

  implicit def traversableKVW[K, V](xs: Traversable[(K, V)]): TraversableKVW[K, V] = new TraversableKVW[K, V](xs)

  implicit def traversableKMVW[K, V](xs: Traversable[(K, Traversable[V])]): TraversableKMVW[K, V] = new TraversableKMVW[K, V](xs)

  implicit def anyWith_>>[A](x: A): AnyWith_>>[A] = new AnyWith_>>[A](x)

  // TODO: Consider Product.showP

  private def trimHeader(h: String): Int => String = {
    case i if i >= h.length => h
    case i if i > 5         => h.substring(0, i - 2) + ".."
    case i if i > 1         => h.substring(0, i - 1) + "-"
    case _                  => h.substring(0, 1)
  }

  implicit final class ProductsWithTabular(private val xs: Traversable[Product]) {
    def tabularPs = {
      xs.headOption match {
        case None    => Nil
        case Some(h) =>
          val rows = xs.toVector map (_.productIterator.toVector map (_.toString))
          val cols = (0 until h.productArity).toVector map (idx => xs map (_.productElement(idx).toString))

          // TODO: deal with > 267 chars
          val widths = cols map (col => col map (_.length) max)

          val headers0 = h.getClass.getDeclaredFields.toVector map (_.getName)
          val headers = headers0 zip widths map Function.uncurried(trimHeader _).tupled

          val rowFormat = widths map (_.ralign) mkString " "
          (headers +: rows) map (row => rowFormat.format(row.seq: _*))
      }
    }
    def showPs()  = tabularPs foreach println
  }

  implicit final class MatrixWithTabular[T](private val xss: Traversable[Traversable[T]]) {
    def tabularM = {
      val maxWidth = xss.toVector.foldLeft(0)((acc, x) => acc max x.size)

      val rows = xss.toVector map (_.toVector map (_.toString) padTo(maxWidth, ""))

      val cols = (0 until maxWidth).toVector map (idx => xss map (_.toIndexedSeq.applyOrElse(idx, (_: Int) => "").toString))

      val widths = cols map (col => col map (_.length) max)

      val rowFormat = widths map (_.ralign) mkString " "
      rows map (row => rowFormat.format(row.seq: _*))
    }
    def showM()  = tabularM foreach println
  }

  implicit final class MapWithTabular[K, V](private val xs: Traversable[(K, V)]) {
//    def maxKeyLen = xs.toIterator.map(_._1.toString.length).max
    def tabularKV = xs map (kv => s"%${xs.maxKeyLen}s %s".format(kv._1, kv._2))
    def showKV()  = tabularKV foreach println
  }

  implicit final class MultimapWithTabular[K, V](private val xs: Traversable[(K, Traversable[V])]) {
    // TODO: alias xs.mkString("[", "],[", "]")
    def tabularKVs = xs map (kv => s"%${xs.maxKeyLen}s %s".format(kv._1, kv._2.mkString("[", "],[", "]")))
    def showKVs()  = tabularKVs foreach println
  }
}
