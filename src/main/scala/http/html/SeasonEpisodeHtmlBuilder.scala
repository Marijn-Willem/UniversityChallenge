package http.html

import HtmlUtil.{withHtmlTemplate, getStringFromOption, getSelectOption, getHiddenInput}
import db.conn.{DbManager, SeasonEpisodeManager, SeasonTeamManager, TeamManager}
import db.model.Model.{SeasonEpisode, Team}
import util.LocalDateUtil.{getLocalDateFromString, toDelimitedString}

import java.sql.Statement

object SeasonEpisodeHtmlBuilder {
  def getHtmlSeasonEpisodePortal(seasonId: Int, episodeId: Option[Int]): String = withHtmlTemplate((stat: Statement) => {
    "<div>\n<select id=\"selEpId\">\n</select>\n</div>\n" +
    s"<div><input type=\"button\" onclick=\"goToManageSeasonEpisode();\" value=\"Manage\" /></div>\n" +
    s"<div><input type=\"button\" onclick=\"insertSeasonEpisodes();\" value=\"Insert\" /></div>\n" +
    "<div id=\"divIns\"></div>\n" +
    s"<input id=\"inpSeasId\" type=\"hidden\" value=\"$seasonId\" />\n" +
    getHiddenInput("inpEpId", episodeId)
  }, Seq("seasonepisode"), Some(s"season/portal?id=$seasonId"), Some(s"loadSeasonEpisodeOptions()"))

  def getHtmlManageSeasonEpisode(seasonId: Int, episodeId: Int): String = withHtmlTemplate((stat: Statement) => {
    val seasonEpisode = new SeasonEpisodeManager(stat).getSeasonEpisode(seasonId, episodeId).get
    val teamList = getTeamListForSeason(stat, seasonId)
    val optionsTeam1 = teamList.map(getSelectOption(_, seasonEpisode.team1Id))
    val optionsTeam2 = teamList.map(getSelectOption(_, seasonEpisode.team2Id))

    s"<div>Team 1: <select id=\"t1id\">\n$optionsTeam1</select>\n</div>\n" +
    s"<div>Team 2: <select id=\"t2id\">\n$optionsTeam2</select>\n</div>\n" +
    s"<div>Score 1: <input id=\"s1\" type=\"text\" value=\"${getStringFromOption(seasonEpisode.score1.map(_.toString))}\" />\n</div>\n" +
    s"<div>Score 2: <input id=\"s2\" type=\"text\" value=\"${getStringFromOption(seasonEpisode.score2.map(_.toString))}\" />\n</div>\n" +
    "<div>Date: <input id=\"dt\" type=\"text\" value=\"" +
      s"${getStringFromOption(seasonEpisode.date.map(toDelimitedString(_, "")))}\" /></div>\n" +
    "<div><input type=\"button\" onclick=\"handleUpd();\" value=\"Update\" /></div>\n" +
    "<div id=\"divUpd\"></div>" +
    s"<input id=\"sid\" type=\"hidden\" value=\"$seasonId\" />\n" +
    s"<input id=\"eid\" type=\"hidden\" value=\"$episodeId\" />\n"
  }, Seq("seasonepisode"), Some(s"seasonepisode/portal?sid=$seasonId&eid=$episodeId"))

  def getSeasonEpisodeOptions(seasonId: Int, episodeId: Option[Int]): String = DbManager.getStringFromStatement((stat: Statement) => {
    new SeasonEpisodeManager(stat).getList(seasonId).sortBy(_.episodeId)
      .map(se =>
        val eid = se.episodeId
        val attrSel = getStringFromOption(episodeId.filter(_ == eid).map(_ => " selected=\"selected\""))

        s"<option value=\"$eid\"$attrSel>Episode $eid</option>\n"
      ).mkString
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
