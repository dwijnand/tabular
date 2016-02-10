package tabular

final case class StringFormatFunc(val fmt: String) extends (Any => String) {
  def apply(x: Any): String = fmt format x
}

final case class IntToFormatString(private val x: Int) extends AnyVal {
  def  leftFormatString: String = if (x == 0) "%s" else "%%-%ds" format x
  def rightFormatString: String = if (x == 0) "%s" else "%%%ds"  format x

  def flushLeft:  StringFormatFunc = StringFormatFunc(x.leftFormatString)
  def flushRight: StringFormatFunc = StringFormatFunc(x.rightFormatString)
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

final case class LString(override val string: String) extends StringWithAlignment.Impl(string, FlushLeft)
final case class RString(override val string: String) extends StringWithAlignment.Impl(string, FlushRight)
final case class CString(override val string: String) extends StringWithAlignment.Impl(string, Centered)

object StringWithAlignment {
  implicit def liftAny[A](x: A): StringWithAlignment = x.lj
  implicit def liftImpl(x: StringWithAlignment): StringWithAlignment.Impl = x match { case y: StringWithAlignment.Impl => y }

  implicit class Ops(val swa: StringWithAlignment) extends AnyVal {
    def format(width: Int): String = swa.alignment format (swa.string, width)
//  def +(s2: String) = swa match {
//    case LString(s) => LString(s + s2)
//    case RString(s) => RString(s + s2)
//    case CString(s) => CString(s + s2)
//  }
  }

  sealed abstract class Impl(val string: String, val alignment: TextAlignment) extends StringWithAlignment
}


//TODO ljustify? centered? ljust/rjust/center
final case class AnyToStringWithAlignment[A](private val x: A) extends AnyVal {
  def lj: LString = new LString(x.toString)
  def rj: RString = new RString(x.toString)
  def cj: CString = new CString(x.toString)
}
