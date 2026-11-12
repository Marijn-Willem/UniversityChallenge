package util

object OptionUtil {
  def equalsPure[T](x: Option[T], y: Option[T]): Boolean = (for {
    xPure <- x
    yPure <- y
  } yield xPure == yPure).exists(identity)
}
