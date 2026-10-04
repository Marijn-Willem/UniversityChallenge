package util

object StringUtil {
  object OrderingReverseString extends Ordering[String] {
    override def compare(x: String, y: String): Int = -x.compareTo(y)
  }
  
  def getStringFromOption(optStr: Option[String], alt: String = ""): String = optStr.getOrElse(alt)
}
