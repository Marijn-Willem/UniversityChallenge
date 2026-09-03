package http.html

import db.conn.{CityManager, DbManager}
import db.model.Model.City
import http.html.HtmlUtil.{getStringFromOption, withHtmlTemplate}
import middleware.DataRetriever

import java.sql.Statement

object CityHtmlBuilder {
  def getHtmlCityPortal: String = withHtmlTemplate((stat: Statement) => {
    "<select id=\"selCity\">\n</select>\n" +
    "<div>Name: <input id=\"nm\" type=\"text\" /></div>\n" +
    "<div><input type=\"button\" onclick=\"insertCity();\" value=\"Insert\" /></div>\n" +
    "<div id=\"divIns\"></div>\n"
  }, Seq("city"), Some("loadCityList()"))

  def getHtmlManageCity(id: Option[Int]): String = withHtmlTemplate((stat: Statement) => {
    val maybeCity = id.foldLeft(Option.empty[City]){ case (_, i) => new CityManager(stat).getEntityById(i) }
    val buttonValue = id.foldLeft("Insert"){ case (_, _) => "Update" }

    s"<div>Name: <input id=\"nm\" type=\"text\" value=\"${getStringFromOption(maybeCity.flatMap(_.name))}\" /></div>\n" +
    s"<div><input type=\"button\" value=\"$buttonValue\" onclick=\"handleInsUpd();\" /></div>\n" +
    "<br/>\n" +
    "<div id=\"divUpd\"></div>\n"
  }, Seq("city"))

  def getCityOptions: String = DbManager.getStringFromStatement((stat: Statement) => {
    new CityManager(stat).getList.map(city =>
      s"<option value=\"${city.id}\">${getStringFromOption(city.name)}</option>\n"
    ).mkString
  })

  def getResultInsertCity(name: String): String = DbManager.getStringFromStatement((stat: Statement) => {
    DataRetriever.insertCity(stat, name)

    "City inserted"
  })
}
