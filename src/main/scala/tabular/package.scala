package object tabular {
  implicit def intToFormatString(x: Int): IntToFormatString = IntToFormatString(x)

  implicit def anyToStringWithAlignment[A](x: A): AnyToStringWithAlignment[A] = AnyToStringWithAlignment[A](x)

  implicit def liftCharToColumn[A](ch: Char)    : A => StringWithAlignment = x => ch.lj
  implicit def liftStringToColumn[A](s: String) : A => StringWithAlignment = x => s.lj

  implicit def anyWithGreaterThanGreaterThan[A](x: A): AnyWithGreaterThanGreaterThan[A] = AnyWithGreaterThanGreaterThan[A](x)

  implicit def travWithTabular[A](xs: TraversableOnce[A])                             : TravWithTabular[A]       = TravWithTabular[A](xs)
  implicit def productsWithTabular(xs: Traversable[Product])                          : ProductsWithTabular      = ProductsWithTabular(xs)
  implicit def travKVWithTabular[K, V](xs: TraversableOnce[(K, V)])                   : TravKVWithTabular[K, V]  = TravKVWithTabular[K, V](xs)
  implicit def travKVsWithTabular[K, V](xs: TraversableOnce[(K, TraversableOnce[V])]) : TravKVsWithTabular[K, V] = TravKVsWithTabular[K, V](xs)
  implicit def matrixWithTabular[T](xss: TraversableOnce[TraversableOnce[T]])         : MatrixWithTabular[T]     = MatrixWithTabular(xss)
}

// TODO: Naming things: show, tabular, return Unit, String, Seq[String]
// TODO: Make everything take TraversableOnce
// TODO: showkv vs showKV, showps vs showPs
// TODO: Consider Product.showP
// TODO: Consider making these return xs.types
// TODO: Reconsider @inline
