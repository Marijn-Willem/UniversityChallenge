package util

object StringUtil {
  def getStringFromOption(optStr: Option[String], alt: String = ""): String = optStr.getOrElse(alt)
}
