package tabular

final case class TravOnceWithMaxOpt[A](private val xs: TraversableOnce[A]) extends AnyVal {
  def maxOpt[B >: A](implicit cmp: Ordering[B]): Option[B] = if (xs.isEmpty) None else Some(xs max cmp)
}


final case class TraversableW[A](private val xs: TraversableOnce[A]) extends AnyVal {
  def tabular(columns: (A => StrWithAlign)*): String = {
    if (xs.isEmpty || columns.isEmpty) ""
    else {
      val values = xs.toVector
      val functions = columns.toVector

      def rows = values map (x => functions map (f => f(x).str))
      def cols = functions map (f => values map (x => f(x).str))
      def renderLines = {
        def widths = cols map (_ map (_.length) max)
        def aligns = functions map (_(values.head).align)
        val rowFormat = (widths, aligns).zipped map ((width, align) => align alignBy width) mkString " "
        rows map (row => rowFormat.format(row: _*))
      }
      renderLines mkString "\n"
    }
  }
}

final case class ProductsWithTabular(private val xs: Traversable[Product]) extends AnyVal {
  private def trimHeader(h: String): Int => String = {
    case i if i >= h.length => h
    case i if i > 5         => h.substring(0, i - 2) + ".."
    case i if i > 1         => h.substring(0, i - 1) + "-"
    case _                  => h.substring(0, 1)
  }

  def tabularPs: Seq[String] = {
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
  def showPs(): Unit = tabularPs foreach println
}



final case class TravKVWithTabular[K, V](private val xs: TraversableOnce[(K, V)]) extends AnyVal { // MapWithTabular
  def maxKeyLen: Option[Int] = xs.toIterator.map(_._1.toString.length).maxOpt
  def tabularkv: TraversableOnce[String] = {
    xs.maxKeyLen.fold(Nil: TraversableOnce[String]) { len =>
      val fmt = s"%${len}s %s"
      xs map (kv => fmt format(kv._1, kv._2))
    }
  }
  def showkv(): Unit = tabularkv foreach println
}

final case class TraversableKVW[K, V](private val xs: TraversableOnce[(K, V)]) extends AnyVal {
  def showkv2(implicit vShow: V => String = _.toString): String = xs tabular (_._1.rj + ":", kv => vShow(kv._2))
}


final case class TravKVsWithTabular[K, V](private val xs: TraversableOnce[(K, Traversable[V])]) extends AnyVal { // MultimapWithTabular
  // TODO: alias xs.mkString("[", "],[", "]")
  def tabularkvs: TraversableOnce[String] = {
    xs.maxKeyLen.fold(Nil: TraversableOnce[String]) { len =>
      val fmt = s"%${len}s %s"
      // TODO: If vs.size == 1 drop parens?
      def showVs(vs: Traversable[V]) = if (vs.size == 1) vs.head else vs.mkString("[", ", ", "]")
      xs map (kv => fmt format(kv._1, showVs(kv._2)))
    }
  }
  def showkvs(): Unit = tabularkvs foreach println
}

final case class TraversableKMVW[K, V](private val xs: TraversableOnce[(K, TraversableOnce[V])]) extends AnyVal {
  def showkvs2(implicit mvShow: TraversableOnce[V] => String = _ mkString ", "): String = xs showkv2 mvShow
}


final case class MatrixWithTabular[T](private val xss: TraversableOnce[TraversableOnce[T]]) extends AnyVal {
  def tabularM: Vector[String] = {
    val maxWidth = xss.toVector.foldLeft(0)((acc, x) => acc max x.size)

    val rows = xss.toVector map (_.toVector map (_.toString) padTo(maxWidth, ""))

    val cols = (0 until maxWidth).toVector map (idx => xss map (_.toIndexedSeq.applyOrElse(idx, (_: Int) => "").toString))

    val widths = cols map (col => col map (_.length) max)

    val rowFormat = widths map (_.ralign) mkString " "
    rows map (row => rowFormat.format(row.seq: _*))
  }
  def showM(): Unit = tabularM foreach println
}

trait Tabular {
  implicit def travOnceWithMaxOpt[A](xs: TraversableOnce[A])                      : TravOnceWithMaxOpt[A]    = TravOnceWithMaxOpt[A](xs)
  implicit def travKVWithTabular[K, V](xs: TraversableOnce[(K, V)])               : TravKVWithTabular[K, V]  = TravKVWithTabular[K, V](xs)
  implicit def travKVsWithTabular[K, V](xs: TraversableOnce[(K, Traversable[V])]) : TravKVsWithTabular[K, V] = TravKVsWithTabular[K, V](xs)
}

object `package` extends Tabular {
  implicit def intWithAlign(x: Int): IntWithAlign = IntWithAlign(x)

  implicit def anyWithTextAlign[A](x: A): AnyWithTextAlign[A] = AnyWithTextAlign[A](x)

  implicit def anyWithGreaterThanGreaterThan[A](x: A): AnyWithGreaterThanGreaterThan[A] = AnyWithGreaterThanGreaterThan[A](x)


  implicit def traversableW[A](xs: TraversableOnce[A]): TraversableW[A] = TraversableW[A](xs)

  implicit def traversableKVW[K, V](xs: TraversableOnce[(K, V)]): TraversableKVW[K, V] = TraversableKVW[K, V](xs)

  implicit def traversableKMVW[K, V](xs: TraversableOnce[(K, TraversableOnce[V])]): TraversableKMVW[K, V] = TraversableKMVW[K, V](xs)

  implicit def productsWithTabular(xs: Traversable[Product]): ProductsWithTabular = ProductsWithTabular(xs)

  implicit def matrixWithTabular[T](xss: TraversableOnce[TraversableOnce[T]]): MatrixWithTabular[T] = MatrixWithTabular(xss)
}

// TODO: Make everything take TraversableOnce
// TODO: showkv vs showKV, showps vs showPs
// TODO: Consider Product.showP
// TODO: Consider making these return xs.types
// TODO: Reconsider @inline
