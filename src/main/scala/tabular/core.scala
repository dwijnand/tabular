package tabular

final case class TravWithTabular[A](private val xs: TraversableOnce[A]) extends AnyVal {
  def tabular(columns: (A => StrWithAlign)*): Seq[String] = {
    if (xs.isEmpty || columns.isEmpty) Nil
    else {
      val values = xs.toVector
      val functions = columns.toVector

      val rows = values map (x => functions map (f => f(x).str))
      val cols = functions map (f => values map (x => f(x).str))

      val widths = cols map (_ map (_.length) max)
      val aligns = functions map (_(values.head).align)
      val rowFormat = (widths, aligns).zipped map ((width, align) => align alignBy width) mkString " "

      rows map (row => rowFormat.format(row: _*))
    }
  }

  def showSeq(): Unit = println(xs mkString ("[", ",", "]"))

  def printEach(): Unit = xs foreach println
}

final case class ProductsWithTabular(private val xs: Traversable[Product]) extends AnyVal {
  private def trimHeader(h: String): Int => String = {
    case i if i >= h.length => h
    case i if i > 5         => h.substring(0, i - 2) + ".."
    case i if i > 1         => h.substring(0, i - 1) + "-"
    case _                  => h.substring(0, 1)
  }

  def showPs: Seq[String] = {
    xs.headOption match {
      case None    => Nil
      case Some(h) =>
        val rows = xs.toVector map (_.productIterator.toVector map (_.toString))
        val cols = (0 until h.productArity).toVector map (idx => xs map (_.productElement(idx).toString))

        // TODO: deal with > 267 chars
        val widths = cols map (col => col map (_.length) max)

        val headers0 = h.getClass.getDeclaredFields.toVector map (_.getName)
        val headers = headers0 zip widths map Function.uncurried(trimHeader _).tupled

        val rowFormat = widths map (_.ralign) mkString " "
        (headers +: rows) map (row => rowFormat.format(row.seq: _*))
    }
  }
}



final case class TravKVWithTabular[K, V](private val xs: TraversableOnce[(K, V)]) extends AnyVal {
  def showkv(implicit z: V => String = _.toString): Seq[String] = xs tabular (_._1.rj + ":", kv => z(kv._2))
}


final case class TravKVsWithTabular[K, V](private val xs: TraversableOnce[(K, TraversableOnce[V])]) extends AnyVal {
  def showkvs(implicit z: TraversableOnce[V] => String = _ mkString ", "): Seq[String] = xs showkv z
}


final case class MatrixWithTabular[T](private val xss: TraversableOnce[TraversableOnce[T]]) extends AnyVal {
  def showM: Vector[String] = {
    val maxWidth = xss.toVector.foldLeft(0)((acc, x) => acc max x.size)

    val rows = xss.toVector map (_.toVector map (_.toString) padTo(maxWidth, ""))

    val cols = (0 until maxWidth).toVector map (idx => xss map (_.toIndexedSeq.applyOrElse(idx, (_: Int) => "").toString))

    val widths = cols map (col => col map (_.length) max)

    val rowFormat = widths map (_.ralign) mkString " "
    rows map (row => rowFormat.format(row.seq: _*))
  }
}
