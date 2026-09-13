package util

import java.time.LocalDate

object LocalDateUtil {
  def toDelimitedString(localDate: LocalDate, delim: String): String = s"${localDate.getYear}$delim" +
    s"${intToTwoDigitString(localDate.getMonthValue)}$delim" +
    s"${intToTwoDigitString(localDate.getDayOfMonth)}"

  def getLocalDateFromString(str: String): Option[LocalDate] = 
    Some(str).collect { case x if x.length == 8 && str.forall(Character.isDigit) => 
      LocalDate.of(str.substring(0, 4).toInt, str.substring(4, 6).toInt, str.substring(6, 8).toInt)
    }
  
  private def intToTwoDigitString(i: Int): String = s"${if (i < 10) "0" else ""}$i"
}
