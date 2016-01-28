package tabular

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
