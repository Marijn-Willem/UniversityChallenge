package util

import java.time.LocalDate
import java.time.chrono.ChronoLocalDate

object OrderingUtil {
  abstract class OrderingOptional[S, T <: Comparable[T]](f: S => T) extends Ordering[Option[S]] {
    override def compare(x: Option[S], y: Option[S]): Int = (x, y) match {
      case (Some(u), Some(v)) => f(u).compareTo(f(v))
      case (None, Some(_)) => 1
      case _ => -1
    }
  }

  object OrderingOptionalLocalDate extends OrderingOptional[LocalDate, ChronoLocalDate](identity)

  object OrderingOptionalInt extends OrderingOptional[Int, java.lang.Integer]((x: Int) => x:java.lang.Integer)

  object OrderingReverseString extends Ordering[String] {
    override def compare(x: String, y: String): Int = -x.compareTo(y)
  }
}
