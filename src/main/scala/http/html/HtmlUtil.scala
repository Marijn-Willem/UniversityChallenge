package http.html

import db.conn.{DbManager, IdEntityManager}
import db.model.Model.{IdEntity, NamedIdEntity}

import java.sql.Statement

object HtmlUtil {
  def withHtmlTemplate(fBody: Statement => String, jsList: Seq[String] = Seq.empty,
                       returnPath: Option[String] = None, onload: Option[String] = None): String = {
    def getJsTag(js: String): String = s"<script type=\"text/javascript\" src=\"/js/$js.js\"></script>"

    val styleTag = "<link rel=\"stylesheet\" href=\"/css/styling.css\" />\n"
    val jsTags = s"${("general"+:jsList).map(getJsTag).mkString("\n")}\n"
    val scriptReturnPath = getStringFromOption(returnPath.map(p =>
      s"<script type=\"text/javascript\">\nlet returnPath = '$p';\n</script>\n")
    )
    val divReturnPath = getStringFromOption(
      returnPath.map(_ => s"<br/><div><input type=\"button\" onclick=\"goToUrl(returnPath);\" value=\"Return\" /></div>\n")
    )

    val fStat = (stat: Statement) => s"<!DOCTYPE html>\n<html>\n<head>\n$styleTag$jsTags$scriptReturnPath</head>\n" +
      s"<body${getStringFromOption(onload.map(x => s" onload=\"$x\";"))}>\n${fBody(stat)}" +
      s"$divReturnPath</body>\n</html>\n"

    DbManager.getStringFromStatement(fStat)
  }

  def getHtmlManageIdEntity[T <: IdEntity](id: Option[Int], constMan: Statement => IdEntityManager[T],
                                           contentSpecific: (Option[T], Statement) => String, jsList: Seq[String],
                                           returnPath: Option[String]): String = withHtmlTemplate((stat: Statement) => {
    val maybeEntity = id.map(constMan(stat).getEntityById).getOrElse(Option.empty[T])
    val buttonValue = id.map(_ => "Update").getOrElse("Insert")

    contentSpecific(maybeEntity, stat) +
      s"<div><input id=\"butUps\" type=\"button\" value=\"$buttonValue\" onclick=\"handleUps();\" /></div>\n" +
      getInpIdInput(id) +
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

  def getSelectOptions[T <: NamedIdEntity](man: IdEntityManager[T], sel: Option[Int]): String =
    man.getList().sortBy(_.name.getOrElse("-")).map(getSelectOption(_, sel)).mkString

  def getSelectOption(entity: NamedIdEntity, sel: Option[Int]): String = {
    val id = entity.id
    val attrSel = getStringFromOption(sel.filter(_ == id).map(_ => " selected=\"selected\""))
    
    s"<option value=\"$id\"$attrSel>${getStringFromOption(entity.name)}</option>\n"
  }

  def getInpIdInput(id: Option[Int]): String = getHiddenInput("inpId", id)

  def getHiddenInput(id: String, value: Option[Int]): String = s"<input id=\"$id\" type=\"hidden\" value=\"" +
    s"${getStringFromOption(value.map(_.toString))}\" />\n"

  def getPortalUrl(entityName: String, id: Option[Int]): String = s"$entityName/portal" +
    s"${getStringFromOption(id.map(x => s"?id=$x"))}"

  def getStringFromOption(optStr: Option[String]): String = optStr.getOrElse("")
}
