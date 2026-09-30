package db.conn

import db.model.Model.Episode

import java.sql.{ResultSet, Statement}

class EpisodeManager(stat: Statement) extends IdEntityManager[Episode](stat, "episode", Seq("roundid")) {
  override protected def constructModelClass(rs: ResultSet): Episode = Episode(rs.getInt("id"), rs.getInt("roundid"))

  override protected def getInsertValues(mc: Episode): Seq[String] = Seq(mc.roundId.toString)
}
