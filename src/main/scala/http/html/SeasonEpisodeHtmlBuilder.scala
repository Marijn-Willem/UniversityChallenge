package http.html

import db.conn.*
import db.model.Model.Round.{roundIdFirstRound, roundIdNameMap, roundIdQuarterFinal, roundIdRepechage}
import db.model.Model.{City, Episode, SeasonEpisode, Team}
import http.html.HtmlUtil.{getHiddenInput, getSelectOption, withHtmlTemplate}
import util.LocalDateUtil.{getLocalDateFromString, toDelimitedString}
import util.OptionUtil.equalsPure
import util.OrderingUtil.{OrderingOptionalInt, OrderingOptionalLocalDate}
import util.StringUtil.getStringFromOption

import java.sql.Statement

object SeasonEpisodeHtmlBuilder {
  private val cssAttrWinner = " class=\"winner\""
  private val cssAttrLosingScore = " class=\"losingScore\""
  private val cssAttrKnockedOut = " class=\"knockedOut\""

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
    val episodeList = new EpisodeManager(stat).getList()
    val episodeListInSeason = new SeasonEpisodeManager(stat).getList(seasonId)
      .sortBy(se => -se.episodeId)
      .flatMap(se => episodeList.find(_.id == se.episodeId))

    episodeListInSeason.map(getSelectOption(_, episodeId)).mkString
  })

  def getSeasonEpisodeTableRows(seasonId: Int): String = DbManager.getStringFromStatement((stat: Statement) => {
    val episodeList = new EpisodeManager(stat).getList()
    val seasonEpisodes = new SeasonEpisodeManager(stat).getList(seasonId).map(setHelpFields(_, episodeList))
    val teamIds = new SeasonTeamManager(stat).getTeamIdsForSeason(seasonId)
    val teams = new TeamManager(stat).getListForIds(teamIds)
    val cities = new CityManager(stat).getListForIds(teams.map(_.cityId))

    val seasonEpisodesOrdered = seasonEpisodes
      .sortBy(_.date)(using OrderingOptionalLocalDate)
      .sortBy(_.roundId.map(_* -1))(using OrderingOptionalInt)
    val seasonEpisodesFirstRound = sliceEpisodesByRoundId(seasonEpisodesOrdered, roundIdFirstRound)
    val seasonEpisodesRepechage = sliceEpisodesByRoundId(seasonEpisodesOrdered, roundIdRepechage)
    val seasonEpisodesQuarterFinal = sliceEpisodesByRoundId(seasonEpisodesOrdered, roundIdQuarterFinal)

    val losingScoreThreshold = getLosingScoreThreshold(seasonEpisodesFirstRound)
    val teamIdsRepechage = seasonEpisodesRepechage.flatMap(se => Seq(se.team1Id, se.team2Id).flatten)

    "<tr><th>Team 1</th><th>Team 2</th><th>Score 1</th><th>Score 2</th><th>Round</th></tr>\n" +
      seasonEpisodesOrdered.map(getSeasonEpisodeTableRow(_, teams, cities, losingScoreThreshold,
        teamIdsRepechage, seasonEpisodesQuarterFinal)).mkString
  })

  def getResultInsertSeasonEpisodes(seasonId: Int): String = DbManager.getStringFromStatement((stat: Statement) =>
    if (new SeasonEpisodeManager(stat).insertEpisodes(seasonId))
      "Insert successful"
    else
      "No insert performed"
  )

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
                                       cityList: Seq[City], losingScoreThreshold: Int,
                                       teamIdsRepechage: Seq[Int], episodesQuarterFinal: Seq[SeasonEpisode]): String = {
    val team1 = seasonEpisode.team1Id.flatMap(teamId => teamList.find(_.id == teamId))
    val team2 = seasonEpisode.team2Id.flatMap(teamId => teamList.find(_.id == teamId))

    val cssWinnerAttr1 = if (equalsPure(seasonEpisode.team1Id, seasonEpisode.winningTeamId)) cssAttrWinner else ""
    val cssWinnerAttr2 = if (equalsPure(seasonEpisode.team2Id, seasonEpisode.winningTeamId)) cssAttrWinner else ""

    val isTeam1Losing = equalsPure(seasonEpisode.team1Id, seasonEpisode.losingTeamId)
    val isTeam2Losing = equalsPure(seasonEpisode.team2Id, seasonEpisode.losingTeamId)
    val isFirstRound = seasonEpisode.roundId.contains(roundIdFirstRound)
    val losingScoreNotBelowThreshold = seasonEpisode.losingScore.exists(_ >= losingScoreThreshold)

    val cssLosingScore1 = isFirstRound && losingScoreNotBelowThreshold && isTeam1Losing
    val cssLosingScore2 = isFirstRound && losingScoreNotBelowThreshold && isTeam2Losing
    val cssLosingScoreAttr1 = if (cssLosingScore1) cssAttrLosingScore else ""
    val cssLosingScoreAttr2 = if (cssLosingScore2) cssAttrLosingScore else ""

    val isKO = isKnockedOut(seasonEpisode, losingScoreThreshold, teamIdsRepechage, episodesQuarterFinal)

    val cssKnockedOutAttr1 = if (isKO && isTeam1Losing) cssAttrKnockedOut else ""
    val cssKnockedOutAttr2 = if (isKO && isTeam2Losing) cssAttrKnockedOut else ""

    s"<tr><td$cssWinnerAttr1$cssKnockedOutAttr1>${getTeamDescription(team1, cityList)}</td>" +
      s"<td$cssWinnerAttr2$cssKnockedOutAttr2>${getTeamDescription(team2, cityList)}</td>" +
      s"<td$cssLosingScoreAttr1>${getStringFromOption(seasonEpisode.score1.map(_.toString))}</td>" +
      s"<td$cssLosingScoreAttr2>${getStringFromOption(seasonEpisode.score2.map(_.toString))}</td>" +
      s"<td>${getStringFromOption(seasonEpisode.roundId.flatMap(roundIdNameMap.get))}</td></tr>\n"
  }

  private def setHelpFields(seasonEpisode: SeasonEpisode, episodes: Seq[Episode]): SeasonEpisode = {
    val maybeScoreBasedData = for {
      score1 <- seasonEpisode.score1
      score2 <- seasonEpisode.score2
      team1Id <- seasonEpisode.team1Id
      team2Id <- seasonEpisode.team2Id
      (teamWinId, teamLoseId, scoreLose) = if (score1 > score2) (team1Id, team2Id, score2) else (team2Id, team1Id, score1)
    } yield (teamWinId, teamLoseId, scoreLose)

    seasonEpisode.copy(
      roundId = episodes.collectFirst { case e if e.id == seasonEpisode.episodeId => e.roundId },
      winningTeamId = maybeScoreBasedData.map(_._1),
      losingTeamId = maybeScoreBasedData.map(_._2),
      losingScore = maybeScoreBasedData.map(_._3)
    )
  }

  private def sliceEpisodesByRoundId(seasonEpisodes: Seq[SeasonEpisode], roundId: Int): Seq[SeasonEpisode] =
    seasonEpisodes.dropWhile(se => !se.roundId.contains(roundId))
      .takeWhile(_.roundId.contains(roundId))

  private def getLosingScoreThreshold(seasonEpisodes: Seq[SeasonEpisode]): Int =
    seasonEpisodes
      .map(se => Math.min(se.score1.getOrElse(0), se.score2.getOrElse(0)))
      .sorted
      .drop(seasonEpisodes.length-4)
      .headOption
      .getOrElse(0)

  private def getTeamDescription(maybeTeam: Option[Team], cityList: Seq[City]): String = {
    val cityAddition = getStringFromOption(for {
      team <- maybeTeam
      city <- cityList.find(_.id == team.cityId)
      tn <- team.name
      cn <- city.name if tn != cn
    } yield s" ($cn)")

    getStringFromOption(maybeTeam.flatMap(_.name).map(x => s"$x$cityAddition"))
  }

  private def isKnockedOut(seasonEpisode: SeasonEpisode, losingScoreThreshold: Int,
                           teamIdsRepechage: Seq[Int], episodesQuarterFinal: Seq[SeasonEpisode]): Boolean =
    (for {
      losingTeamId <- seasonEpisode.losingTeamId
      losingScore <- seasonEpisode.losingScore
      roundId <- seasonEpisode.roundId
    } yield
      roundId match {
        case `roundIdFirstRound` => losingScore < losingScoreThreshold ||
          (losingScore == losingScoreThreshold && teamIdsRepechage.length == 4 && !teamIdsRepechage.contains(losingTeamId))
        case `roundIdQuarterFinal` => episodesQuarterFinal
          .takeWhile(_.episodeId != seasonEpisode.episodeId)
          .count(_.losingTeamId.contains(losingTeamId)) == 1
        case _ => true
      }).exists(identity)
}
