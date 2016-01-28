package tabular

final case class AnyWithGreaterThanGreaterThan[A](private val x: A) extends AnyVal {
  def >>() = println(x)
}
