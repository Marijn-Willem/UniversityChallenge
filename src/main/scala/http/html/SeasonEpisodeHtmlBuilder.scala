package http.html

import db.conn.{DbManager, SeasonEpisodeManager, SeasonTeamManager, TeamManager}
import db.model.Model.{SeasonEpisode, Team}
import util.LocalDateUtil.{getLocalDateFromString, toDelimitedString}

import java.sql.Statement

object SeasonEpisodeHtmlBuilder {
  def getHtmlSeasonEpisodePortal(seasonId: Int): String = HtmlUtil.withHtmlTemplate((stat: Statement) => {
    "<div>\n<select id=\"selEpId\">\n</select>\n</div>\n" +
    s"<div><input type=\"button\" onclick=\"goToManageSeasonEpisode();\" value=\"Manage\" /></div>\n" +
    s"<div><input type=\"button\" onclick=\"insertSeasonEpisodes();\" value=\"Insert\" /></div>\n" +
    "<div id=\"divIns\"></div>\n" +
    s"<input id=\"inpSeasId\" type=\"hidden\" value=\"$seasonId\" />\n"
  }, Seq("seasonepisode"), Some("season/portal"), Some(s"loadSeasonEpisodeOptions()"))

  def getHtmlManageSeasonEpisode(seasonId: Int, episodeId: Int): String = HtmlUtil.withHtmlTemplate((stat: Statement) => {
    val seasonEpisode = new SeasonEpisodeManager(stat).getSeasonEpisode(seasonId, episodeId).get
    val teamList = getTeamListForSeason(stat, seasonId)
    val optionsTeam1 = teamList.map(HtmlUtil.getSelectOption(_, seasonEpisode.team1Id))
    val optionsTeam2 = teamList.map(HtmlUtil.getSelectOption(_, seasonEpisode.team2Id))

    s"<div>Team 1: <select id=\"t1id\">\n$optionsTeam1</select>\n</div>\n" +
    s"<div>Team 2: <select id=\"t2id\">\n$optionsTeam2</select>\n</div>\n" +
    s"<div>Score 1: <input id=\"s1\" type=\"text\" value=\"${seasonEpisode.score1.getOrElse("")}\" />\n</div>\n" +
    s"<div>Score 2: <input id=\"s2\" type=\"text\" value=\"${seasonEpisode.score2.getOrElse("")}\" />\n</div>\n" +
    "<div>Date: <input id=\"dt\" type=\"text\" value=\"" +
      s"${seasonEpisode.date.map(toDelimitedString(_, "")).getOrElse("")}\" /></div>\n" +
    "<div><input type=\"button\" onclick=\"handleUpd();\" value=\"Update\" /></div>\n" +
    "<div id=\"divUpd\"></div>" +
    s"<input id=\"sid\" type=\"hidden\" value=\"$seasonId\" />\n" +
    s"<input id=\"eid\" type=\"hidden\" value=\"$episodeId\" />\n"
  }, Seq("seasonepisode"), Some(s"seasonepisode/portal?sid=$seasonId"))

  def getSeasonEpisodeOptions(seasonId: Int): String = DbManager.getStringFromStatement((stat: Statement) => {
    new SeasonEpisodeManager(stat).getList(seasonId).sortBy(_.episodeId)
      .map(se => s"<option value=\"${se.episodeId}\">Episode ${se.episodeId}</option>\n").mkString
  })

  def getResultInsertSeasonEpisodes(seasonId: Int): String = DbManager.getStringFromStatement((stat: Statement) => {
    if (new SeasonEpisodeManager(stat).insertEpisodes(seasonId))
      "Insert successful"
    else
      "No insert performed"
  })

  def getResultUpdateSeasonEpisode(seasonId: Int, episodeId: Int,
                                   team1Id: Option[Int], team2Id: Option[Int],
                                   score1: Option[Int], score2: Option[Int],
                                   date: Option[String]): String = DbManager.getStringFromStatement((stat: Statement) => {
    val seasonEpisode = SeasonEpisode(seasonId, episodeId, team1Id, team2Id, score1, score2,
      date.flatMap(getLocalDateFromString))
    new SeasonEpisodeManager(stat).updateSeasonEpisode(seasonEpisode)

    "Update successful"
  })

  private def getTeamListForSeason(stat: Statement, seasonId: Int): Seq[Team] = {
    val teamIds = new SeasonTeamManager(stat).getTeamIdsForSeason(seasonId)

    if (teamIds.nonEmpty)
      new TeamManager(stat).getList(Some(s"id IN (${teamIds.mkString(",")})"))
    else
      Seq.empty
  }
}
