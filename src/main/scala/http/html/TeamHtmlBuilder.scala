package http.html

import db.conn.{DbManager, TeamManager}
import db.model.Model.Team
import http.html.HtmlUtil.*
import util.StringUtil.getStringFromOption

import java.sql.Statement

object TeamHtmlBuilder {
  def getHtmlTeamPortal(id: Option[Int]): String = withHtmlTemplate((stat: Statement) =>
    "<div>\n<select id=\"selTeam\">\n</select>\n</div>\n" +
      "<div><input type=\"button\" onclick=\"goToManageTeam();\" value=\"Manage\" /></div>\n" +
      "<div><input type=\"button\" onclick=\"goToInsertTeam();\" value=\"Insert\" /></div>\n" +
      "<div><input type=\"button\" onclick=\"goToSeasonTeamPortal();\" value=\"Season Team\" /></div>\n" +
      getInpIdInput(id)
  , Seq("team"), Some(""), Some("loadTeamList()"))

  def getHtmlManageTeam(id: Option[Int]): String = {
    val contentSpecific = (maybeTeam: Option[Team], stat: Statement) =>
      s"<div>Name: <input id=\"nm\" value=\"${getStringFromOption(maybeTeam.flatMap(_.name))}\" /></div>\n" +
        s"<div>\nCity: <select id=\"cid\">\n${CityHtmlBuilder.getCityOptions(stat, maybeTeam.map(_.cityId))}" +
        "</select>\n</div>\n"

    HtmlUtil.getHtmlManageIdEntity(id,
      (stat: Statement) => new TeamManager(stat),
      contentSpecific,
      Seq("team"), Some(getPortalUrl("team", id)))
  }

  def getHtmlTeamOptions(id: Option[Int]): String = DbManager.getStringFromStatement((stat: Statement) =>
    getSelectOptions(new TeamManager(stat), id))

  def getResultUpsertTeam(name: String, cityId: Int, id: Option[Int]): String = getResultUpsert(
    id,
    (stat: Statement) => new TeamManager(stat),
    (id: Int) => Team(id, None, 0),
    (t: Team) => t.copy(name = Some(name), cityId = cityId)
  )
}
