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

final case class TravKVWithTabular[K, V](private val xs: Traversable[(K, V)]) extends AnyVal {
  def maxKeyLen = xs.toIterator.map(_._1.toString.length).maxOpt
  def tabularkv = {
    xs.maxKeyLen.fold(Nil: Traversable[String]) { len =>
      val fmt = s"%${len}s %s"
      xs map (kv => fmt format(kv._1, kv._2))
    }
  }
  def showkv() = tabularkv foreach println
}

final case class TravKVsWithTabular[K, V](private val xs: Traversable[(K, Traversable[V])]) extends AnyVal {
  def tabularkvs = {
    xs.maxKeyLen.fold(Nil: Traversable[String]) { len =>
      val fmt = s"%${len}s %s"
      def showVs(vs: Traversable[V]) = if (vs.size == 1) vs.head else vs.mkString("[", ", ", "]")
      xs map (kv => fmt format(kv._1, showVs(kv._2)))
    }
  }
  def showkvs() = tabularkvs foreach println
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
  def lj = new LString(x.toString)
  def rj = new RString(x.toString)
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
  def showkv(implicit vShow: V => String = _.toString): String =
    xs tabular (_._1.rj + ":", kv => vShow(kv._2))
}

final case class TraversableKMVW[K, V](private val xs: Traversable[(K, Traversable[V])]) extends AnyVal {
  def showkvs(implicit mvShow: Traversable[V] => String = _ mkString ", "): String =
    xs showkv mvShow
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
}
