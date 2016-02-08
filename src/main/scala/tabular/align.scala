package tabular

final case class IntToFormatString(private val x: Int) extends AnyVal {
  def  leftFormatString: String = if (x == 0) "%s" else s"%-${x}s"
  def rightFormatString: String = if (x == 0) "%s" else s"%${x}s"
}

sealed trait TextAlignment extends Any       { def formatString(width: Int): String }
case object FlushLeft  extends TextAlignment { def formatString(width: Int): String = width.leftFormatString  }
case object FlushRight extends TextAlignment { def formatString(width: Int): String = width.rightFormatString }
//case object Centered extends TextAlignment

//sealed trait Column extends Any
//object Column {
//  implicit class LiteralChar(val ch: Char) extends AnyVal with Column
//  implicit class LiteralString(val s: String) extends AnyVal with Column
//  implicit class FromFunction[A](val f: A => StringAlignment) extends AnyVal with Column
//}

// Enables having an implicit conversion for Any without enriching Any with these methods
// Works because implicit conversions don't chain
sealed trait StringWithAlignment extends Any

sealed trait StringWithAlignmentOps extends Any with StringWithAlignment {
  def string: String
  def alignment: TextAlignment
}

final case class LString(string: String) extends AnyVal with StringWithAlignmentOps { def alignment = FlushLeft  }
final case class RString(string: String) extends AnyVal with StringWithAlignmentOps { def alignment = FlushRight }
//final case class CString(value: String)extends AnyVal with StringWithAlignmentOps { def alignment = Centered   }

object StringWithAlignment {
  implicit def liftAny[A](x: A): StringWithAlignment = x.lj
  implicit def liftOps(x: StringWithAlignment): StringWithAlignmentOps = x match { case y: StringWithAlignmentOps => y }
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
