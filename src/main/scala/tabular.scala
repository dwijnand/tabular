package object tabular {
  type ->[+A, +B]  = Product2[A, B]

  implicit class TraversableW[A](private val xs: Traversable[A]) extends AnyVal {
    def tabular(fs: (A => String)*): String = {
      if (xs.isEmpty || fs.isEmpty) ""
      else {
        val rows0 = xs.toVector
        val cols0 = fs.toVector

        def cols = cols0 map (f => rows0 map (x => f(x)))
        def rows = rows0 map (x => cols0 map (f => f(x)))
        def renderLines = {
          val maxWidths = cols map (_ map (_.length) max)
          val rowFormat = maxWidths map leftFmt mkString " "
          rows map (row => rowFormat.format(row.toSeq: _*))
        }
        renderLines mkString "\n"
      }
    }
  }

  implicit class TraversableKVW[K, V](private val xs: Traversable[K -> V]) extends AnyVal {
    def kvFormat = rightFmt(xs map (_._1.toString.length) max) + ": %s"

    def showkv(vShow: V => String = _.toString): String = {
      val fmt = kvFormat
      xs map (kv => fmt.format (kv._1, vShow(kv._2))) mkString "\n"
    }
  }

  implicit class TraversableKMVW[K, V](private val xs: Traversable[K -> Traversable[V]]) extends AnyVal {
    def showkvs(sep: String = ", "): String = xs showkv (_ mkString sep)
  }

  def leftFmt(i: Int) = if (i == 0) "%s" else s"%-${i}s"
  def rightFmt(i: Int) = if (i == 0) "%s" else s"%${i}s"
}
