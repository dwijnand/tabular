package tabular

final case class StringFormatFunc(val fmt: String) extends (Any => String) {
  def apply(x: Any): String = fmt format x
}

final case class IntToFormatString(_x: Int) extends AnyVal {
  def  leftFormatString: String = if (_x == 0) "%s" else "%%-%ds" format _x
  def rightFormatString: String = if (_x == 0) "%s" else "%%%ds"  format _x

  def flushLeft:  StringFormatFunc = StringFormatFunc(_x.leftFormatString)
  def flushRight: StringFormatFunc = StringFormatFunc(_x.rightFormatString)
}

sealed trait TextAlignment
case object FlushLeft  extends TextAlignment
case object FlushRight extends TextAlignment
case object Centered   extends TextAlignment

object TextAlignment {
  implicit class Ops(val ta: TextAlignment) extends AnyVal {
    def format(s: String, width: Int): String = ta match {
      case FlushLeft  => width.flushLeft  apply s
      case FlushRight => width.flushRight apply s
      case Centered   => {
        val marg = width - s.length
        val left = marg / 2
        val right = marg - left
        (" " * left) + s + (" " * right)
      }
    }
  }
}

// Enables having an implicit conversion for Any without enriching Any with these methods
// Works because implicit conversions don't chain
sealed trait StringWithAlignment

// Can't de-duplify + into a super-method by using copy because of SI-5122
// Can't enrich it on because any2stringadd has higher precedence (in-scope implicits > implicit scope)
final case class LString(override val string: String) extends StringWithAlignment.Impl(string, FlushLeft)  { def +(s: String) = copy(string + s) }
final case class RString(override val string: String) extends StringWithAlignment.Impl(string, FlushRight) { def +(s: String) = copy(string + s) }
final case class CString(override val string: String) extends StringWithAlignment.Impl(string, Centered)   { def +(s: String) = copy(string + s) }

object StringWithAlignment {
  implicit def liftAny[A](x: A): StringWithAlignment = x.lj
  implicit def liftImpl(x: StringWithAlignment): StringWithAlignment.Impl = x match { case y: StringWithAlignment.Impl => y }

  implicit class Ops(val swa: StringWithAlignment) extends AnyVal {
    def format(width: Int): String = swa.alignment format (swa.string, width)
  }

  sealed abstract class Impl(val string: String, val alignment: TextAlignment) extends StringWithAlignment
}


//TODO ljustify? centered? ljust/rjust/center
final case class AnyToStringWithAlignment[A](_x: A) extends AnyVal {
  def lj: LString = new LString(_x.toString)
  def rj: RString = new RString(_x.toString)
  def cj: CString = new CString(_x.toString)
}
