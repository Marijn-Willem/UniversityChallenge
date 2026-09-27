package http.html

import db.conn.{DbManager, SeasonManager}
import db.model.Model.Season
import http.html.HtmlUtil.{getInpIdInput, getPortalUrl, getSelectOptions, getStringFromOption}

import java.sql.Statement

object SeasonHtmlBuilder {
  def getHtmlSeasonPortal(id: Option[Int]): String = HtmlUtil.withHtmlTemplate((stat: Statement) =>
    "<div>\n<select id=\"selSeason\">\n</select>\n</div>\n" +
    "<div><input type=\"button\" onclick=\"goToManageSeason();\" value=\"Manage\" /></div>\n" +
    "<div><input type=\"button\" onclick=\"goToInsertSeason();\" value=\"Insert\" /></div>\n" +
    "<div><input type=\"button\" onclick=\"goToSeasonEpisodePortal();\" value=\"Episodes\" /></div>\n" +
    getInpIdInput(id)
  , Seq("season"), Some(""), Some("loadSeasonList()"))

  def getHtmlManageSeason(id: Option[Int]): String = {
    val contSpecific = (maybeSeason: Option[Season], _: Statement) =>
      s"<div>Name: <input id=\"nm\" value=\"${getStringFromOption(maybeSeason.flatMap(_.name))}\" /></div>\n"

    HtmlUtil.getHtmlManageIdEntity(id,
      (stat: Statement) => new SeasonManager(stat),
      contSpecific,
      Seq("season"), Some(getPortalUrl("season", id)))
  }

  def getHtmlSeasonOptions(id: Option[Int]): String = DbManager.getStringFromStatement((stat: Statement) =>
    getSelectOptions(new SeasonManager(stat), id))

  def getResultUpsertSeason(name: String, id: Option[Int]): String =
    HtmlUtil.getResultUpsert(id,
      (stat: Statement) => new SeasonManager(stat),
      (id: Int) => Season(id, None),
      (s: Season) => s.copy(name = Some(name))
    )
}
