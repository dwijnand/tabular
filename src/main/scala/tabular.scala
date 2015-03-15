package net.mox9

import scala.language.implicitConversions

// TODO: Add AnyVals back
abstract class TabularPackage {
  type ->[+A, +B] = Product2[A, B]

  sealed trait TextAlign extends Any { def alignBy(width: Int): String }
  case object LAlign extends TextAlign { def alignBy(width: Int) = leftFmt(width) }
  case object RAlign extends TextAlign { def alignBy(width: Int) = rightFmt(width) }

  sealed trait StrWithAlign

  trait StrWithAlign0 {
    implicit def liftAny[A](x: A): StrWithAlign = x.toString.lj
  }
  object StrWithAlign extends StrWithAlign0 {
    implicit class StrWithAlignOps(private val swa: StrWithAlign) {
      def str: String = swa match {
        case ls: LString => ls._1
        case rs: RString => rs._1
      }
      def fmt: TextAlign = swa match {
        case ls: LString => ls._2
        case rs: RString => rs._2
      }
    }
  }

  final class LString(val _1: String) extends StrWithAlign {
    def _2 = LAlign
    def canEqual(that: Any): Boolean = that.isInstanceOf[LString]
  }
  final class RString(val _1: String) extends StrWithAlign {
    def _2 = RAlign
    def canEqual(that: Any): Boolean = that.isInstanceOf[RString]
  }

  implicit class StringWithTextAlign(private val s: String) {
    def lj = new LString(s)
    def rj = new RString(s)
  }

  implicit class TraversableW[A](private val xs: Traversable[A]) {
    def tabular(fs: (A => StrWithAlign)*): String = {
      if (xs.isEmpty || fs.isEmpty) ""
      else {
        val rows0 = xs.toVector
        val cols0 = fs.toVector

        def rows = rows0 map (x => cols0 map (f => f(x).str))
        def cols = cols0 map (f => rows0 map (x => f(x).str))
        def renderLines = {
          def maxWidths = cols map (_ map (_.length) max)
          def aligns = cols0 map (_(rows0.head).fmt)
          def colFmts = (aligns, maxWidths).zipped map ((align, width) => align alignBy width)
          val rowFormat = colFmts mkString " "
          rows map (row => rowFormat.format(row: _*))
        }
        renderLines mkString "\n"
      }
    }
  }

  implicit class TraversableKVW[K, V](private val xs: Traversable[K -> V]) {
    def kvFormat = rightFmt(xs map (_._1.toString.length) max) + ": %s"

    def showkv(vShow: V => String = _.toString): String = {
      val fmt = kvFormat
      xs map (kv => fmt.format (kv._1, vShow(kv._2))) mkString "\n"
    }
  }

  implicit class TraversableKMVW[K, V](private val xs: Traversable[K -> Traversable[V]]) {
    def showkvs(sep: String = ", "): String = xs showkv (_ mkString sep)
  }

  def leftFmt(i: Int) = if (i == 0) "%s" else s"%-${i}s"
  def rightFmt(i: Int) = if (i == 0) "%s" else s"%${i}s"
}

package object tabular extends TabularPackage
