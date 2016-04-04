package tabular

final case class TravWithTabular[A](_xs: TraversableOnce[A]) extends AnyVal {
  def tabular(columns: (A => StringWithAlignment)*): Seq[String] = {
    if (_xs.isEmpty || columns.isEmpty) Nil
    else {
      val values: Vector[A] = _xs.toVector
      val functions: Vector[A => StringWithAlignment] = columns.toVector

  //  lazy val rows: Vector[Vector[String]] = values map (x => functions map (fn => fn(x).string))
      lazy val cols: Vector[Vector[String]] = functions map (fn => values map (x => fn(x).string))

      val maxWidths: Vector[Int] = cols map (_ map (_.length) max)

      def showRow(x: A): String =
        (functions, maxWidths).zipped map ((fn, maxWidth) => fn(x) format maxWidth) mkString " "

      values map showRow
    }
  }

  def showSeq(): Unit = println(_xs mkString ("[", ",", "]"))

  def printEach(): Unit = _xs foreach println
}

final case class ProductsWithTabular(_xs: Traversable[Product]) extends AnyVal {
  private def trimHeader(h: String): Int => String = {
    case i if i >= h.length => h
    case i if i > 5         => h.substring(0, i - 2) + ".."
    case i if i > 1         => h.substring(0, i - 1) + "-"
    case _                  => h.substring(0, 1)
  }

  def showPs: Seq[String] = {
    _xs.headOption match {
      case None    => Nil
      case Some(h) =>
        val rows = _xs.toVector map (_.productIterator.toVector map (_.toString))
        val cols = (0 until h.productArity).toVector map (idx => _xs map (_.productElement(idx).toString))

        // TODO: deal with > 267 chars
        val widths = cols map (col => col map (_.length) max)

        val headers0 = h.getClass.getDeclaredFields.toVector map (_.getName)
        val headers = headers0 zip widths map Function.uncurried(trimHeader _).tupled

        val rowFormat = widths map (_.rightFormatString) mkString " "
        (headers +: rows) map (row => rowFormat.format(row.seq: _*))
    }
  }
}



final case class TravKVWithTabular[K, V](_xs: TraversableOnce[(K, V)]) extends AnyVal {
  def showkv(implicit z: V => String = _.toString): Seq[String] = _xs tabular (_._1.rj + ":", kv => z(kv._2))
}


final case class TravKVsWithTabular[K, V](_xs: TraversableOnce[(K, TraversableOnce[V])]) extends AnyVal {
  def showkvs(implicit z: TraversableOnce[V] => String = _ mkString ", "): Seq[String] = _xs showkv z
}


final case class MatrixWithTabular[T](_xss: TraversableOnce[TraversableOnce[T]]) extends AnyVal {
  def showM: Seq[String] = {
    val rows0 = _xss.toVector

    val maxWidth = rows0.foldLeft(0)((acc, x) => acc max x.size)

    val rows = rows0 map (_.toVector map (_.toString) padTo (maxWidth, ""))

    val cols = (0 until maxWidth).toVector map (idx => rows0 map (_.toIndexedSeq.applyOrElse(idx, (_: Int) => "").toString))

    val maxWidths = cols map (col => col map (_.length) max)

    val rowFormat = maxWidths map (_.rightFormatString) mkString " "
    rows map (row => rowFormat.format(row.seq: _*))
  }
}
