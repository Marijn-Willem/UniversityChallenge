package http.html

import db.conn.{CityManager, DbManager}
import db.model.Model.City
import http.html.HtmlUtil.*

import java.sql.Statement

object CityHtmlBuilder {
  def getHtmlCityPortal(id: Option[Int]): String = withHtmlTemplate(_ =>
    "<div>\n<select id=\"selCity\">\n</select>\n</div>\n" +
    "<div><input type=\"button\" onclick=\"goToManageCity();\" value=\"Manage\" /></div>\n" +
    "<div><input type=\"button\" onclick=\"goToInsertCity();\" value=\"Insert\" /></div>\n" +
    getInpIdInput(id)
  , Seq("city"), Some(""), Some("loadCityList()"))

  def getHtmlManageCity(id: Option[Int]): String = {
    val contentSpecific = (maybeCity: Option[City], _: Statement) =>
      "<div>Name: <input id=\"nm\" type=\"text\" value=\"" +
      s"${getStringFromOption(maybeCity.flatMap(_.name))}\" /></div>\n"

    getHtmlManageIdEntity(id,
      (stat: Statement) => new CityManager(stat),
      contentSpecific,
      Seq("city"), Some(getPortalUrl("city", id))
    )
  }

  def getHtmlCityOptions(id: Option[Int]): String = DbManager.getStringFromStatement(getCityOptions(_, id))

  def getCityOptions(stat: Statement, sel: Option[Int]): String = getSelectOptions(new CityManager(stat), sel)

  def getResultUpsertCity(name: String, id: Option[Int]): String = getResultUpsert(
    id,
    (stat: Statement) => new CityManager(stat),
    (id: Int) => City(id, None),
    (c: City) => c.copy(name = Some(name))
  )
}
