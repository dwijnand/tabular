package tabular

final case class IntWithAlign(private val x: Int) extends AnyVal {
  def lalign: String = if (x == 0) "%s" else s"%-${x}s"
  def ralign: String = if (x == 0) "%s" else s"%${x}s"
}
