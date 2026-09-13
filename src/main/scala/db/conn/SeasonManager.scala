package db.conn

import db.model.Model.Season

import java.sql.{ResultSet, Statement}

class SeasonManager(stat: Statement) extends IdEntityManager[Season](stat, "season", Seq("name")) {
  override protected def constructModelClass(rs: ResultSet): Season =
    Season(rs.getInt("id"), Option(rs.getString("name")))

  override protected def getInsertValues(mc: Season): Seq[String] = Seq(getStringForInsert(mc.name))
}
