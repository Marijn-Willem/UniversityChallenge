package db.conn

import db.model.Model.SeasonTeam

import java.sql.Statement
import scala.collection.mutable.ArrayBuffer

class SeasonTeamManager(stat: Statement) {
  def getSeasonIdsForTeam(teamId: Int): Seq[Int] = {
    val ab = ArrayBuffer.empty[Int]
    val rs = stat.executeQuery(s"SELECT seasonid FROM seasonteam WHERE teamid = $teamId")

    while (rs.next())
      ab.append(rs.getInt("seasonid"))

    ab.toSeq
  }

  def getTeamIdsForSeason(seasonId: Int): Seq[Int] = {
    val ab = ArrayBuffer.empty[Int]
    val rs = stat.executeQuery(s"SELECT teamid FROM seasonteam WHERE seasonid = $seasonId")

    while (rs.next())
      ab.append(rs.getInt("teamid"))

    ab.toSeq
  }

  def insert(seasonTeam: SeasonTeam): Unit =
    stat.execute(s"INSERT INTO seasonteam (seasonid, teamid) VALUES (${seasonTeam.seasonId}, ${seasonTeam.teamId})")
}
