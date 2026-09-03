package db.conn

import db.model.Model.City

import java.sql.{ResultSet, Statement}

class CityManager(stat: Statement) extends EntityManager[City](stat, "city", "id, name") {
  override protected def constructModelClass(rs: ResultSet): City = City(rs.getInt("id"), Option(rs.getString("name")))

  override protected def getInsertValues(mc: City): String = s"${mc.id}, ${getStringForInsert(mc.name)}"

  override def getEntityById(id: Int): Option[City] = super.getEntityById(id)

  override def getList: Seq[City] = super.getList

  override def getNewId: Int = super.getNewId

  override def insert(mc: City): Unit = super.insert(mc)
}
