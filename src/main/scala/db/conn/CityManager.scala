package db.conn

import db.model.Model.City

import java.sql.{ResultSet, Statement}

class CityManager(stat: Statement) extends IdEntityManager[City](stat, "city", Seq("name")) {
  override protected def constructModelClass(rs: ResultSet): City = City(rs.getInt("id"), Option(rs.getString("name")))

  override protected def getInsertValues(mc: City): Seq[String] = Seq(getStringForInsert(mc.name))
}
