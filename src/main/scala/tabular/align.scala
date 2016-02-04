package tabular

final case class IntWithAlign(private val x: Int) extends AnyVal {
  def lalign: String = if (x == 0) "%s" else s"%-${x}s"
  def ralign: String = if (x == 0) "%s" else s"%${x}s"
}

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
final case class LString(str: String) extends AnyVal with StrWithAlignOps {
  def align = LAlign
//def +(s: String) = new LString(str + s)
}
final case class RString(str: String) extends AnyVal with StrWithAlignOps {
  def align = RAlign
//def +(s: String) = new RString(str + s)
}
object StrWithAlign {
  implicit def liftAny[A](x: A): StrWithAlign = x.lj
  implicit def liftOps(x: StrWithAlign): StrWithAlignOps = x match { case y: StrWithAlignOps => y }
}


final case class AnyWithTextAlign[A](private val x: A) extends AnyVal {
  def lj: LString = new LString(x.toString)
  def rj: RString = new RString(x.toString)
}

// ---

final case class IntToFormatString(private val x: Int) extends AnyVal {
  def  leftFormatString: String = if (x == 0) "%s" else s"%-${x}s"
  def rightFormatString: String = if (x == 0) "%s" else s"%${x}s"
}

sealed trait TextAlignment extends Any
final case object FlushLeft  extends TextAlignment
final case object FlushRight extends TextAlignment
final case object Centered   extends TextAlignment

sealed trait Column extends Any
object Column {
  implicit class LiteralChar(val ch: Char) extends AnyVal with Column
  implicit class LiteralString(val s: String) extends AnyVal with Column
  implicit class FromFunction[A](val f: A => StrWithAlign) extends AnyVal with Column
}

// ---
// TODO ljustify? centered?

object Centered {
  def center1(s: String, len: Int) = {
    val out = s"%${len}s%s%${len}s" format ("", s, "")
    val mid = out.length / 2
    val start = mid - (len / 2)
    val end = start + len
    out substring (start, end)
  }

  def center2(s: String, len: Int) = s"%-${len}s" format (s"%${s.length + (len - s.length) / 2}s" format s)
}
