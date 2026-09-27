package db.conn

import db.model.Model.IdEntity

import java.sql.{ResultSet, Statement}
import scala.collection.mutable.ArrayBuffer

abstract class IdEntityManager[T <: IdEntity](stat: Statement, val tableName: String, val columnNames: Seq[String]) {
  private val columnString = ("id"+:columnNames).mkString(", ")

  protected def constructModelClass(rs: ResultSet): T

  protected def getInsertValues(mc: T): Seq[String]

  def getEntityById(id: Int): Option[T] = {
    val query = s"SELECT $columnString FROM $tableName WHERE id = $id"

    val rs = stat.executeQuery(query)

    if (rs.next())
      Some(constructModelClass(rs))
    else
      None
  }

  def getList(whereClause: Option[String] = None): Seq[T] = {
    val query = s"SELECT $columnString FROM $tableName" +
      s"${whereClause.map(x => s" WHERE $x").getOrElse("")}"
    val ab = ArrayBuffer.empty[T]

    val rs = stat.executeQuery(query)

    while (rs.next())
      ab.append(constructModelClass(rs))

    ab.toSeq
  }

  def update(mc: T): Unit = {
    val updateStatement = columnNames.zip(getInsertValues(mc)).map({ _ + " = " + _ }).mkString(", ")

    stat.execute(s"UPDATE $tableName SET $updateStatement WHERE id = ${mc.id}")
  }

  def insert(mc: T): Unit = stat.execute(s"INSERT INTO $tableName ($columnString) " +
    s"VALUES (${(mc.id+:getInsertValues(mc)).mkString(", ")})")

  def getNewId: Int = {
    val query = s"SELECT MAX(id) AS maxId FROM $tableName"
    val rs = stat.executeQuery(query)

    1 + (if (rs.next()) rs.getInt("maxId") else 0)
  }

  protected def getStringForInsert(optStr: Option[String]): String = optStr.map(x =>
    s"'${replaceInvalidDbCharacters(x)}'").getOrElse("NULL")

  private def replaceInvalidDbCharacters(str: String) = str.replace("'", "''")
}
