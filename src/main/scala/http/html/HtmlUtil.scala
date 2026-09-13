package http.html

import db.conn.{DbManager, IdEntityManager}
import db.model.Model.{IdEntity, NamedIdEntity}

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
      s"<body${getStringFromOption(onload.map(x => s" onload=\"$x\";"))}>\n${fBody(stat)}" +
      s"$divReturnPath</body>\n<html>\n"

    DbManager.getStringFromStatement(fStat)
  }

  def getHtmlManageIdEntity[T <: IdEntity](id: Option[Int], constMan: Statement => IdEntityManager[T],
                                           contentSpecific: (Option[T], Statement) => String, jsList: Seq[String],
                                           returnPath: Option[String]): String = withHtmlTemplate((stat: Statement) => {
    val maybeEntity = id.map(constMan(stat).getEntityById).getOrElse(Option.empty[T])
    val buttonValue = id.map(_ => "Update").getOrElse("Insert")

    contentSpecific(maybeEntity, stat) +
      s"<div><input id=\"butUps\" type=\"button\" value=\"$buttonValue\" onclick=\"handleUps();\" /></div>\n" +
      s"<input id=\"inpId\" type=\"hidden\" value=\"${getStringFromOption(id.map(_.toString))}\" />\n" +
      "<div id=\"divUps\"></div>\n"
  }, jsList, returnPath)

  def getResultUpsert[T <: IdEntity](id: Option[Int], constMan: Statement => IdEntityManager[T],
                                     constT: Int => T, cp: T => T): String =
    DbManager.getStringFromStatement((stat: Statement) => {
      val man = constMan(stat)

      lazy val entityFromDb = man.getEntityById(id.get)
      lazy val entityCreated = constT(man.getNewId)

      val entityNew = cp(id.flatMap(_ => entityFromDb).getOrElse(entityCreated))

      val action = id.map(_ => man.update).getOrElse(man.insert)

      action(entityNew)

      entityNew.id.toString
    })

  def getSelectOption(entity: NamedIdEntity, sel: Option[Int] = None): String = {
    val id = entity.id
    val selected = sel.filter(_ == id).map(_ => " selected=\"selected\"").getOrElse("")
    
    s"<option value=\"$id\"$selected>${getStringFromOption(entity.name)}</option>\n"
  }

  def getStringFromOption(optStr: Option[String]): String = optStr.getOrElse("")
}
