package tabular

final case class AnyWithGreaterThanGreaterThan[A](private val x: A) extends AnyVal {
  def >>(): Unit = println(x)
}


// TODO: Move all side-effecting out of core?
