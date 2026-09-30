package http.html

import HtmlUtil.{getHiddenInput, getSelectOption, withHtmlTemplate}
import db.conn.{DbManager, EpisodeManager, SeasonEpisodeManager, SeasonTeamManager, TeamManager}
import db.model.Model.{Round, SeasonEpisode, Team}
import util.StringUtil.getStringFromOption
import util.LocalDateUtil.{OrderingOptionalLocalDate, getLocalDateFromString, toDelimitedString}

import java.sql.Statement

object SeasonEpisodeHtmlBuilder {
  private val cssAttrWinner = " class=\"winner\""
  private val cssAttrLosingScore = " class=\"losingScore\""

  def getHtmlSeasonEpisodePortal(seasonId: Int, episodeId: Option[Int]): String = withHtmlTemplate((stat: Statement) => {
    "<div>\n<select id=\"selEpId\">\n</select>\n</div>\n" +
    s"<div><input type=\"button\" onclick=\"goToManageSeasonEpisode();\" value=\"Manage\" /></div>\n" +
    s"<div><input type=\"button\" onclick=\"insertSeasonEpisodes();\" value=\"Insert\" /></div>\n" +
    "<div id=\"divIns\"></div>\n" +
    "<table id=\"tblEp\"></table>\n" +
    s"<input id=\"inpSeasId\" type=\"hidden\" value=\"$seasonId\" />\n" +
    getHiddenInput("inpEpId", episodeId)
  }, Seq("seasonepisode"), Some(s"season/portal?id=$seasonId"), Some(s"loadSeasonEpisodeData()"))

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

  def getSeasonEpisodeTableRows(seasonId: Int): String = DbManager.getStringFromStatement((stat: Statement) => {
    val episodeList = new EpisodeManager(stat).getList()
    val seasonEpisodes = new SeasonEpisodeManager(stat).getList(seasonId)
    val teamIds = new SeasonTeamManager(stat).getTeamIdsForSeason(seasonId)
    val teams = new TeamManager(stat).getListForIds(teamIds)
    
    val seasonEpisodesFirstRound = seasonEpisodes.filter(se => 
      episodeList.find(_.id == se.episodeId)
        .exists(_.roundId == Round.roundIdFirstRound)
    ).sortBy(_.date)(using OrderingOptionalLocalDate)

    val losingScoreThreshold = getLosingScoreThreshold(seasonEpisodesFirstRound)

    "<tr><th>Team 1</th><th>Team 2</th><th>Score 1</th><th>Score 2</th></tr>\n" +
      seasonEpisodesFirstRound.map(getSeasonEpisodeTableRow(_, teams, losingScoreThreshold)).mkString
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

  private def getSeasonEpisodeTableRow(seasonEpisode: SeasonEpisode, teamList: Seq[Team],
                                       losingScoreThreshold: Int): String = {
    val team1 = seasonEpisode.team1Id.flatMap(teamId => teamList.find(_.id == teamId))
    val team2 = seasonEpisode.team2Id.flatMap(teamId => teamList.find(_.id == teamId))
    val score1AsInt = seasonEpisode.score1.getOrElse(0)
    val score2AsInt = seasonEpisode.score2.getOrElse(0)

    val cssWinnerAttr1 = if (score1AsInt > score2AsInt) cssAttrWinner else ""
    val cssWinnerAttr2 = if (score2AsInt > score1AsInt) cssAttrWinner else ""

    val cssLosingScore1 = score1AsInt < score2AsInt && score1AsInt >= losingScoreThreshold
    val cssLosingScore2 = score2AsInt < score1AsInt && score2AsInt >= losingScoreThreshold
    val cssLosingScoreAttr1 = if (cssLosingScore1) cssAttrLosingScore else ""
    val cssLosingScoreAttr2 = if (cssLosingScore2) cssAttrLosingScore else ""

    s"<tr><td$cssWinnerAttr1>${getStringFromOption(team1.flatMap(_.name))}</td>" +
      s"<td$cssWinnerAttr2>${getStringFromOption(team2.flatMap(_.name))}</td>" +
      s"<td$cssLosingScoreAttr1>${getStringFromOption(seasonEpisode.score1.map(_.toString))}</td>" +
      s"<td$cssLosingScoreAttr2>${getStringFromOption(seasonEpisode.score2.map(_.toString))}</td></tr>\n"
  }

  private def getLosingScoreThreshold(seasonEpisodes: Seq[SeasonEpisode]): Int = {
    seasonEpisodes
      .map(se => Math.min(se.score1.getOrElse(0), se.score2.getOrElse(0)))
      .sorted
      .drop(seasonEpisodes.length-4)
      .headOption
      .getOrElse(0)
  }
}
