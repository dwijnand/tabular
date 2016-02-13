package tabular

final case class AnyWithGreaterThanGreaterThan[A](_x: A) extends AnyVal {
  def >>(): Unit = println(_x)
}


// TODO: Move all side-effecting out of core?
