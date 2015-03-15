package net.mox9

import scala.language.implicitConversions

package tabular {

// TODO: Add AnyVals back
abstract class TabularPackage {
  type ->[+A, +B] = Product2[A, B]

  sealed trait TextAlign extends Any { def alignBy(width: Int): String }
  case object LAlign extends TextAlign { def alignBy(width: Int) = if (width == 0) "%s" else s"%-${width}s" }
  case object RAlign extends TextAlign { def alignBy(width: Int) = if (width == 0) "%s" else s"%${width}s" }

  sealed trait StrWithAlign
  class StrWithAlignImpl(val string: String, val align: TextAlign)
  final class LString(string: String) extends StrWithAlignImpl(string, LAlign) with StrWithAlign
  final class RString(string: String) extends StrWithAlignImpl(string, RAlign) with StrWithAlign

  trait StrWithAlign0 {
    implicit def liftAny[A](x: A): StrWithAlign = x.lj
  }
  object StrWithAlign extends StrWithAlign0 {
    implicit class StrWithAlignOps(private val swa: StrWithAlign) {
      def str: String = swa match {
        case ls: LString => ls.string
        case rs: RString => rs.string
      }
      def align: TextAlign = swa match {
        case ls: LString => ls.align
        case rs: RString => rs.align
      }
    }
  }

  implicit class AnyWithTextAlign[A](private val x: A) {
    def lj = new LString(x.toString)
    def rj = new RString(x.toString)
  }

  implicit class TraversableW[A](private val xs: Traversable[A]) {
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

  implicit class TraversableKVW[K, V](private val xs: Traversable[K -> V]) {
    def showkv(vShow: V => String = _.toString): String = xs tabular (kv => (kv._1 + ":").rj, kv => vShow(kv._2))
  }

  implicit class TraversableKMVW[K, V](private val xs: Traversable[K -> Traversable[V]]) {
    def showkvs(sep: String = ", "): String = xs showkv (_ mkString sep)
  }
}

}

package object tabular extends TabularPackage
