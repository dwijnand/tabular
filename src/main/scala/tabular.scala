package net.mox9.tabular

sealed trait TextAlign extends Any   { def alignBy(width: Int): String }
case object LAlign extends TextAlign { def alignBy(width: Int) = width.lalign }
case object RAlign extends TextAlign { def alignBy(width: Int) = width.ralign }

// Split name from properties, so auto-lifting is only by type name, not by method
// Then auto-lift this trait to give it methods, works because implicit conversions don't chain
sealed trait StrWithAlign    extends Any
sealed trait StrWithAlignOps extends Any with StrWithAlign {
  def str: String
  def align: TextAlign
}
class LString(val str: String) extends AnyVal with StrWithAlignOps {
  def align = LAlign
  def +(s: String) = new LString(str + s)
}
class RString(val str: String) extends AnyVal with StrWithAlignOps {
  def align = RAlign
  def +(s: String) = new RString(str + s)
}
object StrWithAlign {
  implicit def liftAny[A](x: A): StrWithAlign = x.lj
  implicit def liftOps(swa: StrWithAlign): StrWithAlignOps = swa match { case swao: StrWithAlignOps => swao }
}

trait Tabular {
  implicit def travOnceWithMaxOpt[A](xs: TraversableOnce[A])                  : TravOnceWithMaxOpt[A]    = new TravOnceWithMaxOpt[A](xs)
  implicit def travKVWithTabular[K, V](xs: Traversable[(K, V)])               : TravKVWithTabular[K, V]  = new TravKVWithTabular[K, V](xs)
  implicit def travKVsWithTabular[K, V](xs: Traversable[(K, Traversable[V])]) : TravKVsWithTabular[K, V] = new TravKVsWithTabular[K, V](xs)
}

final class TravOnceWithMaxOpt[A](private val xs: TraversableOnce[A]) extends AnyVal {
  def maxOpt[B >: A](implicit cmp: Ordering[B]): Option[B] = if (xs.isEmpty) None else Some(xs max cmp)
}

final class TravKVWithTabular[K, V](private val xs: Traversable[(K, V)]) extends AnyVal {
  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.ToString")) // tabularisation is excused
  def maxKeyLen = xs.toIterator.map(_._1.toString.length).maxOpt
  def tabularkv = {
    xs.maxKeyLen.fold(Nil: Traversable[String]) { len =>
      val fmt = s"%${len}s %s"
      xs map (kv => fmt format(kv._1, kv._2))
    }
  }
  def showkv() = tabularkv foreach println
}

final class TravKVsWithTabular[K, V](private val xs: Traversable[(K, Traversable[V])]) extends AnyVal {
  def tabularkvs = {
    xs.maxKeyLen.fold(Nil: Traversable[String]) { len =>
      val fmt = s"%${len}s %s"
      def showVs(vs: Traversable[V]) = if (vs.size == 1) vs.head else vs.mkString("[", ", ", "]")
      xs map (kv => fmt format(kv._1, showVs(kv._2)))
    }
  }
  def showkvs() = tabularkvs foreach println
}

object `package` extends Tabular {
  type ->[+A, +B] = Product2[A, B]

  implicit class IntWithAlign(private val x: Int) extends AnyVal {
    @inline def lalign: String = if (x == 0) "%s" else s"%-${x}s"
    @inline def ralign: String = if (x == 0) "%s" else s"%${x}s"
  }
  @inline def lalign(width: Int): String = width.lalign
  @inline def ralign(width: Int): String = width.ralign

  implicit class AnyWithTextAlign[A](private val x: A) extends AnyVal {
    def lj = new LString(x.toString)
    def rj = new RString(x.toString)
  }

  implicit class TraversableW[A](private val xs: Traversable[A]) extends AnyVal {
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

  implicit class TraversableKVW[K, V](private val xs: Traversable[K -> V]) extends AnyVal {
    def showkv(implicit vShow: V => String = _.toString): String =
      xs tabular (_._1.rj + ":", kv => vShow(kv._2))
  }

  implicit class TraversableKMVW[K, V](private val xs: Traversable[K -> Traversable[V]]) {
    def showkvs(implicit mvShow: Traversable[V] => String = _ mkString ", "): String =
      xs showkv mvShow
  }

  implicit class AnyWith_>>[A](private val x: A) extends AnyVal {
    def >>() = println(x)
  }
}
