package util

object StringUtil {
  object OrderingReverseString extends Ordering[String] {
    override def compare(x: String, y: String): Int = -x.compareTo(y)
  }
  
  def getStringFromOption(optStr: Option[String]): String = optStr.getOrElse("")
}
