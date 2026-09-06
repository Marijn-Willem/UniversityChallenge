package http.html

import db.conn.{CityManager, DbManager}
import db.model.Model.City
import http.html.HtmlUtil.{getResultUpsert, getStringFromOption, withHtmlTemplate}

import java.sql.Statement

object CityHtmlBuilder {
  def getHtmlCityPortal: String = withHtmlTemplate((stat: Statement) => {
    "<select id=\"selCity\">\n</select>\n" +
    "<div><input type=\"button\" onclick=\"goToManageCity();\" value=\"Manage\" /></div>\n" +
    "<div><input type=\"button\" onclick=\"goToInsertCity();\" value=\"Insert\" /></div>\n"
  }, Seq("city"), Some(""), Some("loadCityList()"))

  def getHtmlManageCity(id: Option[Int]): String = withHtmlTemplate((stat: Statement) => {
    val maybeCity = id.foldLeft(Option.empty[City]){ case (_, i) => new CityManager(stat).getEntityById(i) }
    val buttonValue = id.foldLeft("Insert"){ case (_, _) => "Update" }

    s"<div>Name: <input id=\"nm\" type=\"text\" value=\"${getStringFromOption(maybeCity.flatMap(_.name))}\" /></div>\n" +
    s"<div><input id=\"butUps\" type=\"button\" value=\"$buttonValue\" onclick=\"handleUps();\" /></div>\n" +
    s"<input id=\"inpId\" type=\"hidden\" value=\"${getStringFromOption(id.map(_.toString))}\" />\n" +
    "<div id=\"divUps\"></div>\n"
  }, Seq("city"), Some("city/portal"))

  def getCityOptions: String = DbManager.getStringFromStatement((stat: Statement) => {
    new CityManager(stat).getList.map(city =>
      s"<option value=\"${city.id}\">${getStringFromOption(city.name)}</option>\n"
    ).mkString
  })

  def getResultUpsertCity(name: String, id: Option[Int]): String = getResultUpsert(
    id,
    (stat: Statement) => new CityManager(stat),
    (id: Int) => City(id, None),
    (c: City) => c.copy(name = Some(name))
  )
}
