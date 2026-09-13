package http.html

import db.conn.{DbManager, TeamManager}
import db.model.Model.Team
import http.html.HtmlUtil.{getResultUpsert, getSelectOption, withHtmlTemplate}

import java.sql.Statement

object TeamHtmlBuilder {
  def getHtmlTeamPortal: String = withHtmlTemplate((stat: Statement) =>
    "<div>\n<select id=\"selTeam\">\n</select>\n</div>\n" +
      "<div><input type=\"button\" onclick=\"goToManageTeam();\" value=\"Manage\" /></div>\n" +
      "<div><input type=\"button\" onclick=\"goToInsertTeam();\" value=\"Insert\" /></div>\n" +
      "<div><input type=\"button\" onclick=\"goToSeasonTeamPortal();\" value=\"Season Team\" /></div>\n"
  , Seq("team"), Some(""), Some("loadTeamList()"))

  def getHtmlManageTeam(id: Option[Int]): String = {
    val contentSpecific = (maybeTeam: Option[Team], stat: Statement) =>
      s"<div>Name: <input id=\"nm\" value=\"${HtmlUtil.getStringFromOption(maybeTeam.flatMap(_.name))}\" /></div>\n" +
        s"<div>\nCity: <select id=\"cid\">\n${CityHtmlBuilder.getCityOptions(stat, maybeTeam.map(_.cityId))}" +
        "</select>\n</div>\n"

    HtmlUtil.getHtmlManageIdEntity(id,
      (stat: Statement) => new TeamManager(stat),
      contentSpecific,
      Seq("team"), Some("team/portal"))
  }

  def getHtmlTeamOptions: String = DbManager.getStringFromStatement((stat: Statement) =>
    new TeamManager(stat).getList().map(getSelectOption(_)).mkString
  )

  def getResultUpsertTeam(name: String, cityId: Int, id: Option[Int]): String = getResultUpsert(
    id,
    (stat: Statement) => new TeamManager(stat),
    (id: Int) => Team(id, None, 0),
    (t: Team) => t.copy(name = Some(name), cityId = cityId)
  )
}
