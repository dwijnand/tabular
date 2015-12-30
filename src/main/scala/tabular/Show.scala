package tabular

package zzz {

  trait ToS extends Any {
    def to_s: String
    override def toString = to_s
  }

  trait Show[-A] {
    def show(x: A): String
    def shown(x: A): Shown = new Shown(show(x))
  }

  final class Shown(val to_s: String) extends AnyVal with ToS

  object Show {
    def native[A](): Show[A]              = Show("" + _)
    def apply[A](f: A => String): Show[A] = new Show[A] { def show(x: A) = f(x) }
  }

}

trait Show[-A] extends Any { def show(x: A): String }

trait ShowDirect extends Any                 { def to_s: String             }
trait ShowSelf   extends Any with ShowDirect { override def toString = to_s }

class TryShow[-A](shows: Show[A]) {
  def show(x: A): String = if (shows == null) "" + x else shows show x
}

object TryShow {
  implicit def apply[A](implicit z: Show[A] = Show.inheritShow[A]): TryShow[A] = new TryShow[A](z)
}

final case class TryShown(__shown_rep: String) extends AnyVal {
  override def toString = __shown_rep
}

/** Used to achieve type-safety in the show interpolator.
 *  It's the String resulting from passing a value through its Show instance. */
final case class Shown(to_s: String) extends AnyVal with ShowSelf {
  def ~ (that: Shown): Shown = new Shown(to_s + that.to_s)
}

object Shown {
  def empty: Shown             = new Shown("")
  def apply(ss: Shown*): Shown = if (ss.isEmpty) empty else ss reduce (_ ~ _)
}

final class ShowDirectOps(val x: ShowDirect) extends AnyVal {
  def + (that: ShowDirect): ShowDirect                = Shown(x.to_s + that.to_s)
  def + [A](that: A)(implicit z: Show[A]): ShowDirect = Shown(x.to_s + (z show that))
}

object Show {
  final class Impl[-A](val f: A => String) extends AnyVal with Show[A] { def show(x: A) = f(x) }

  def apply[A](f: A => String): Show[A] = new Impl[A](f)

  /** This of course is not implicit as that would defeat the purpose of the endeavor. */
  val Inherited: Show[Any] = apply[Any] {
    case null          => ""
    case x: ShowDirect => x.to_s
    case x             => x.toString
  }

  def inheritShow[A]: Show[A] = Show.Inherited
}

object Unsafe {
  implicit def inheritedShow[A]: Show[A] = Show.inheritShow
}

final class ShowBy[A] { def apply[B](f: A => B)(implicit z: Show[B]): Show[A] = Show[A](f andThen z.show) }

object ShowBy {
  def showBy[A] = new ShowBy[A]
}

trait ShowInstances extends ShowEach {
  import Show.inheritShow

  implicit def showBoolean: Show[Boolean]     = inheritShow
  implicit def showChar: Show[Char]           = inheritShow
  implicit def showDouble: Show[Double]       = inheritShow
  implicit def showInt: Show[Int]             = inheritShow
  implicit def showLong: Show[Long]           = inheritShow
  implicit def showString: Show[String]       = inheritShow
  implicit def showThrowable: Show[Throwable] = inheritShow

  implicit def showClass: Show[java.lang.Class[_]]                      = Show(c => scala.reflect.NameTransformer decode c.getName.split('.').last)
  implicit def showDirect: Show[ShowDirect]                             = Show(_.to_s)
//  implicit def showOption[A: Show] : Show[Option[A]]                    = Show(_.fold("-")(_.render))
//  implicit def showPair[A: Show, B: Show] : Show[A -> B]                = Show(x => x._1 ~ " -> " ~ x._2 render)
  implicit def showStackTraceElement: Show[java.lang.StackTraceElement] = Show(x => s"\tat$x\n")
}

class FormatFun(val fmt: String) extends (Any => String) with ShowSelf {
  def apply(x: Any): String = fmt format x
  def to_s = fmt
}

trait ShowEach {
  final val EOL = java.lang.System getProperty "line.separator"

  def cond[A](p: Boolean, thenp: => A, elsep: => A): A = if (p) thenp else elsep
  def leftFormatString[A](n: Int): FormatFun = new FormatFun(cond(n == 0, "%s", "%%-%ds" format n))

  case class FunctionGrid[A, B](values: Vector[A], functions: Vector[A => B]) {
    def rows    = values map (v => functions map (f => f(v)))
    def columns = functions map (f => values map (v => f(v)))

    def renderLines(implicit z: Show[B]): Vector[String]               = {
      val widths    = columns map (_ map z.show map (_.length) max)
      val formatFns = widths map leftFormatString

      rows map (formatFns zip _ map (_ apply _) mkString ' ')
    }
    def render(implicit z: Show[B]): String = renderLines mkString EOL
  }

  private def tabular[A](xs: Vector[A])(columns: (A => String)*): String =
    if (xs.nonEmpty && columns.nonEmpty) FunctionGrid(xs, columns.toVector).render(Show.inheritShow) else ""

//  implicit def showExMap[K: Show, V: Show] : Show[ExMap[K, V]]                = Show(xs => tabular(xs.entries.pairs)(_.render))
//  implicit def showZipped[A1: Show, A2: Show] : Show[ZipView[A1, A2]]         = ShowBy.showBy[ZipView[A1, A2]](_.pairs)
//  implicit def showArray[A: Show] : Show[Array[A]]                            = ShowBy.showBy[Array[A]](_.toVec)
  implicit def showJavaEnum[A <: java.lang.Enum[A]] : Show[java.lang.Enum[A]] = Show.inheritShow
}

package hcn {
  import scala.collection.convert._
  object PackageObject extends DecorateAsScala with DecorateAsJava with scala.io.AnsiColor {
    implicit class CStringOps(val s: String) extends AnyVal {
      private def bold(color: String): String = BOLD + color + s + RESET
      private def in(color: String): String   = color + s + RESET

      def inRed: String     = bold(RED)
      def inGreen: String   = bold(GREEN)
      def inCyan: String    = bold(CYAN)
      def inMagenta: String = bold(MAGENTA)
      def inBlue: String    = bold(BLUE)
      def inYellow: String  = bold(YELLOW)

      def bgRed: String     = in(RED_B)
      def bgGreen: String   = in(GREEN_B)
      def bgCyan: String    = in(CYAN_B)
      def bgMagenta: String = in(MAGENTA_B)
      def bgBlue: String    = in(BLUE_B)
      def bgYellow: String  = in(YELLOW_B)
    }
  }
}
