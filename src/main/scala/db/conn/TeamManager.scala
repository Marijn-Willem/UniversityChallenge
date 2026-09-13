package db.conn

import db.model.Model.Team

import java.sql.{ResultSet, Statement}

class TeamManager(stat: Statement) extends IdEntityManager[Team](stat, "team", Seq("name", "cityid")) {
  override protected def constructModelClass(rs: ResultSet): Team = Team(
    rs.getInt("id"),
    Option(rs.getString("name")),
    rs.getInt("cityid")
  )

  override protected def getInsertValues(mc: Team): Seq[String] = Seq(
    getStringForInsert(mc.name),
    mc.cityId.toString
  )
}
