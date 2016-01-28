package tabular

final case class AnyWithTextAlign[A](private val x: A) extends AnyVal {
  def lj: LString = new LString(x.toString)
  def rj: RString = new RString(x.toString)
}
