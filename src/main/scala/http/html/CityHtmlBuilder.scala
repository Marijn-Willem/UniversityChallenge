package http.html

import db.conn.{CityManager, DbManager}
import db.model.Model.City
import http.html.HtmlUtil.{getHtmlManageIdEntity, getResultUpsert, getSelectOption, getStringFromOption, withHtmlTemplate}

import java.sql.Statement

object CityHtmlBuilder {
  def getHtmlCityPortal: String = withHtmlTemplate((stat: Statement) =>
    "<div>\n<select id=\"selCity\">\n</select>\n</div>\n" +
    "<div><input type=\"button\" onclick=\"goToManageCity();\" value=\"Manage\" /></div>\n" +
    "<div><input type=\"button\" onclick=\"goToInsertCity();\" value=\"Insert\" /></div>\n"
  , Seq("city"), Some(""), Some("loadCityList()"))

  def getHtmlManageCity(id: Option[Int]): String = {
    val contentSpecific = (maybeCity: Option[City], _: Statement) =>
      "<div>Name: <input id=\"nm\" type=\"text\" value=\"" +
      s"${getStringFromOption(maybeCity.flatMap(_.name))}\" /></div>\n"

    getHtmlManageIdEntity(id,
      (stat: Statement) => new CityManager(stat),
      contentSpecific,
      Seq("city"), Some("city/portal")
    )
  }

  def getHtmlCityOptions: String = DbManager.getStringFromStatement((stat: Statement) => getCityOptions(stat))

  def getCityOptions(stat: Statement, sel: Option[Int] = None): String =
    new CityManager(stat).getList().map(getSelectOption(_, sel)).mkString

  def getResultUpsertCity(name: String, id: Option[Int]): String = getResultUpsert(
    id,
    (stat: Statement) => new CityManager(stat),
    (id: Int) => City(id, None),
    (c: City) => c.copy(name = Some(name))
  )
}
