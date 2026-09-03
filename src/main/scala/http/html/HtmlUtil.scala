package http.html

import db.conn.DbManager

import java.sql.Statement

object HtmlUtil {
  def withHtmlTemplate(fBody: Statement => String, jsList: Seq[String] = Seq.empty, onload: Option[String] = None): String = {
    def getJsTag(js: String): String = s"<script type=\"text/javascript\" src=\"/js/$js.js\"></script>"

    val linkTag = "<link rel=\"stylesheet\" href=\"/css/styling.css\" />"
    val jsTags = ("general"+:jsList).map(getJsTag).mkString("\n")

    val fStat = (stat: Statement) => s"<!DOCTYPE html>\n<html>\n<head>\n$linkTag\n$jsTags\n</head>\n" +
      s"<body${getStringFromOption(onload.map(x => " onload=\""+x+"\";"))}>\n${fBody(stat)}</body>\n<html>\n"

    DbManager.getStringFromStatement(fStat)
  }

  def getStringFromOption(optStr: Option[String]): String = optStr.getOrElse("")
}
