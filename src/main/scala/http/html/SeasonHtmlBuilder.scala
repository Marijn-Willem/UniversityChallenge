package http.html

import db.conn.{DbManager, SeasonManager}
import db.model.Model.Season
import http.html.HtmlUtil.{getSelectOption, getStringFromOption}

import java.sql.Statement

object SeasonHtmlBuilder {
  def getHtmlSeasonPortal: String = HtmlUtil.withHtmlTemplate((stat: Statement) =>
    "<div>\n<select id=\"selSeason\">\n</select>\n</div>\n" +
    "<div><input type=\"button\" onclick=\"goToManageSeason();\" value=\"Manage\" /></div>\n" +
    "<div><input type=\"button\" onclick=\"goToInsertSeason();\" value=\"Insert\" /></div>\n" +
    "<div><input type=\"button\" onclick=\"goToSeasonEpisodePortal();\" value=\"Episodes\" /></div>\n"
  , Seq("season"), Some(""), Some("loadSeasonList()"))

  def getHtmlManageSeason(id: Option[Int]): String = {
    val contSpecific = (maybeSeason: Option[Season], _: Statement) =>
      s"<div>Name: <input id=\"nm\" value=\"${getStringFromOption(maybeSeason.flatMap(_.name))}\" /></div>\n"

    HtmlUtil.getHtmlManageIdEntity(id,
      (stat: Statement) => new SeasonManager(stat),
      contSpecific,
      Seq("season"), Some("season/portal"))
  }

  def getHtmlSeasonOptions: String = DbManager.getStringFromStatement((stat: Statement) =>
    new SeasonManager(stat).getList().map(getSelectOption(_)).mkString
  )

  def getResultUpsertSeason(name: String, id: Option[Int]): String =
    HtmlUtil.getResultUpsert(id,
      (stat: Statement) => new SeasonManager(stat),
      (id: Int) => Season(id, None),
      (s: Season) => s.copy(name = Some(name))
    )
}
