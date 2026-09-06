package http.html

import db.conn.{DbManager, IdEntityManager}
import db.model.Model.IdEntity

import java.sql.Statement

object HtmlUtil {
  def withHtmlTemplate(fBody: Statement => String, jsList: Seq[String] = Seq.empty,
                       returnPath: Option[String] = None, onload: Option[String] = None): String = {
    def getJsTag(js: String): String = s"<script type=\"text/javascript\" src=\"/js/$js.js\"></script>"

    val linkTag = "<link rel=\"stylesheet\" href=\"/css/styling.css\" />"
    val jsTags = ("general"+:jsList).map(getJsTag).mkString("\n")
    val divReturnPath = getStringFromOption(
      returnPath.map(p => s"<br/><div><input type=\"button\" onclick=\"goToUrl('$p');\" value=\"Return\" /></div>\n")
    )

    val fStat = (stat: Statement) => s"<!DOCTYPE html>\n<html>\n<head>\n$linkTag\n$jsTags\n</head>\n" +
      s"<body${getStringFromOption(onload.map(x => " onload=\""+x+"\";"))}>\n${fBody(stat)}" +
      s"$divReturnPath</body>\n<html>\n"

    DbManager.getStringFromStatement(fStat)
  }

  def getResultUpsert[T <: IdEntity](id: Option[Int], constMan: Statement => IdEntityManager[T],
                                     constT: Int => T, cp: T => T): String =
    DbManager.getStringFromStatement((stat: Statement) => {
      val man = constMan(stat)

      lazy val entityFromDb = man.getEntityById(id.get)
      lazy val entityCreated = constT(man.getNewId)

      val entityNew = cp(id.flatMap(_ => entityFromDb).getOrElse(entityCreated))

      val action = id.foldLeft(man.insert) { case (_, _) => man.update }

      action(entityNew)

      entityNew.id.toString
    })

  def getStringFromOption(optStr: Option[String]): String = optStr.getOrElse("")
}
