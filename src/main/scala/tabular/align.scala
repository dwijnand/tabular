package tabular

final case class StringFormatFunc(val fmt: String) extends (Any => String) {
  def apply(x: Any): String = fmt format x
}

final case class IntToFormatString(private val x: Int) extends AnyVal {
  def  leftFormatString: String = if (x == 0) "%s" else s"%-${x}s"
  def rightFormatString: String = if (x == 0) "%s" else s"%${x}s"

  def flushLeft:  StringFormatFunc = StringFormatFunc(x.leftFormatString)
  def flushRight: StringFormatFunc = StringFormatFunc(x.rightFormatString)
}

sealed trait TextAlignment                   { def formatString(width: Int): StringFormatFunc }
case object FlushLeft  extends TextAlignment { def formatString(width: Int): StringFormatFunc = width.flushLeft  }
case object FlushRight extends TextAlignment { def formatString(width: Int): StringFormatFunc = width.flushRight }
//case object Centered extends TextAlignment

//sealed trait Column extends Any
//object Column {
//  implicit class LiteralChar(val ch: Char) extends AnyVal with Column
//  implicit class LiteralString(val s: String) extends AnyVal with Column
//  implicit class FromFunction[A](val f: A => StringAlignment) extends AnyVal with Column
//}

// Enables having an implicit conversion for Any without enriching Any with these methods
// Works because implicit conversions don't chain
sealed trait StringWithAlignment

final case class LString(override val string: String) extends StringWithAlignment.Impl(string, FlushLeft)
final case class RString(override val string: String) extends StringWithAlignment.Impl(string, FlushRight)
//final case class CString(value: String)extends AnyVal with StringWithAlignmentOps { def alignment = Centered   }

object StringWithAlignment {
  implicit def liftAny[A](x: A): StringWithAlignment = x.lj
  implicit def liftImpl(x: StringWithAlignment): StringWithAlignment.Impl = x match { case y: StringWithAlignment.Impl => y }

  implicit class Ops(val swa: StringWithAlignment) extends AnyVal {
    def format(width: Int): String = swa.alignment formatString width apply swa.string
  }

  sealed abstract class Impl(val string: String, val alignment: TextAlignment) extends StringWithAlignment
}


final case class AnyToStringWithAlignment[A](private val x: A) extends AnyVal {
  def lj: LString = new LString(x.toString)
  def rj: RString = new RString(x.toString)
//def cj: CString = new CString(x.toString)
}

// ---
// TODO ljustify? centered?

object CenteredFn {
  def center1(s: String, len: Int) = {
    val out = s"%${len}s%s%${len}s" format ("", s, "")
    val mid = out.length / 2
    val start = mid - (len / 2)
    val end = start + len
    out substring (start, end)
  }

  def center2(s: String, len: Int) = s"%-${len}s" format (s"%${s.length + (len - s.length) / 2}s" format s)
}
