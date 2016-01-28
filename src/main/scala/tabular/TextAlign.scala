package tabular

sealed trait TextAlign extends Any   { def alignBy(width: Int): String }
case object LAlign extends TextAlign { def alignBy(width: Int) = width.lalign }
case object RAlign extends TextAlign { def alignBy(width: Int) = width.ralign }
