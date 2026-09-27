package db.conn

import db.model.Model.SeasonEpisode
import util.LocalDateUtil

import java.sql.{ResultSet, Statement}
import java.time.LocalDate
import scala.collection.mutable.ArrayBuffer

class SeasonEpisodeManager(stat: Statement) {
  def getSeasonEpisode(seasonId: Int, episodeId: Int): Option[SeasonEpisode] = {
    val rs = getResultSet(seasonId, Some(episodeId))

    if (rs.next())
      Some(getSeasonEpisodeFromResultSet(rs))
    else
      None
  }

  def getList(seasonId: Int): Seq[SeasonEpisode] = {
    val rs = getResultSet(seasonId, None)

    val ab = ArrayBuffer.empty[SeasonEpisode]

    while (rs.next())
      ab.append(getSeasonEpisodeFromResultSet(rs))
    
    ab.toSeq
  }

  def insertEpisodes(seasonId: Int): Boolean = {
    val doInsert = !stat.executeQuery(s"SELECT 1 FROM seasonepisode WHERE seasonid = $seasonId").next()

    if (doInsert)
      stat.execute(s"INSERT INTO seasonepisode (seasonid, episodeid) SELECT $seasonId, id FROM episode")

    doInsert
  }

  def updateSeasonEpisode(seasonEpisode: SeasonEpisode): Unit =
    stat.execute(s"UPDATE seasonepisode SET team1id = ${getStringForIntUpdate(seasonEpisode.team1Id)}, " +
    s"team2id = ${getStringForIntUpdate(seasonEpisode.team2Id)}, " +
    s"score1 = ${getStringForIntUpdate(seasonEpisode.score1)}, " +
    s"score2 = ${getStringForIntUpdate(seasonEpisode.score2)}, " +
    s"date = ${getStringForLocalDateUpdate(seasonEpisode.date)} " +
    s"WHERE seasonid = ${seasonEpisode.seasonId} AND episodeid = ${seasonEpisode.episodeId}")

  private def getResultSet(seasonId: Int, episodeId: Option[Int]): ResultSet =
    stat.executeQuery("SELECT seasonid, episodeid, team1id, team2id, score1, score2, \"date\" FROM seasonepisode " +
      s"WHERE seasonid = $seasonId${episodeId.map(id => s" AND episodeid = $id").getOrElse("")}")

  private def getSeasonEpisodeFromResultSet(rs: ResultSet): SeasonEpisode =
    SeasonEpisode(
      rs.getInt("seasonid"),
      rs.getInt("episodeid"),
      getOptionalIntFromResultSet(rs, "team1id"),
      getOptionalIntFromResultSet(rs, "team2id"),
      getOptionalIntFromResultSet(rs, "score1"),
      getOptionalIntFromResultSet(rs, "score2"),
      Option(rs.getDate("date")).map(_.toLocalDate)
    )

  private def getOptionalIntFromResultSet(rs: ResultSet, colName: String): Option[Int] =
    Option(rs.getInt(colName)).filterNot(_ => rs.wasNull())

  private def getStringForIntUpdate(maybeInt: Option[Int]): String = maybeInt.map(_.toString).getOrElse("NULL::integer") 

  private def getStringForLocalDateUpdate(maybeLocalDate: Option[LocalDate]): String =
    maybeLocalDate.map(ld => s"'${LocalDateUtil.toDelimitedString(ld, "-")}'").getOrElse("NULL::date")
}
