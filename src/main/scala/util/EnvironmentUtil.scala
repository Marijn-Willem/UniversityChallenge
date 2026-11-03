package util

object EnvironmentUtil {
  def getEnvironmentVariable(variable: String, alt: String): String =
    Option(System.getenv(variable)).getOrElse(alt)
}
