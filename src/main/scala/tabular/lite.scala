package tabular

final case class MapWithTabularLite[K, V](private val xs: TraversableOnce[(K, V)]) extends AnyVal {
  def showkv() = if (xs.nonEmpty) {
    val len = xs.toIterator.map(_._1.toString.length).max
    val fmt = s"%${len}s %s"
    xs foreach (kv => println(fmt format (kv._1, kv._2)))
  }
}

final case class MultimapWithTabularLite[K, V](private val xs: TraversableOnce[(K, TraversableOnce[V])]) extends AnyVal {
  def showkvs() = if (xs.nonEmpty) {
    val len = xs.toIterator.map(_._1.toString.length).max
    val fmt = s"%${len}s %s"
    def show(vs: TraversableOnce[V]) = if (vs.size == 1) vs.toIterator.next() else vs mkString ("[", ", ", "]")
    xs foreach (kv => println(fmt format (kv._1, show(kv._2))))
  }
}
