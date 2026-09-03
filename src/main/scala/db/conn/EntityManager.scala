package db.conn

import java.sql.{ResultSet, Statement}
import scala.collection.mutable.ArrayBuffer

abstract class EntityManager[T](stat: Statement, val tableName: String, val columns: String) {
  protected def constructModelClass(rs: ResultSet): T

  protected def getInsertValues(mc: T): String

  protected def getEntityById(id: Int): Option[T] = {
    val query = s"SELECT $columns FROM $tableName WHERE id = $id"

    val rs = stat.executeQuery(query)

    if (rs.next())
      Some(constructModelClass(rs))
    else
      None
  }

  protected def getList: Seq[T] = {
    val query = s"SELECT $columns FROM $tableName ORDER BY id"
    val ab = ArrayBuffer.empty[T]

    val rs = stat.executeQuery(query)

    while (rs.next())
      ab.append(constructModelClass(rs))

    ab.toSeq
  }

  protected def insert(mc: T): Unit = stat.execute(s"INSERT INTO $tableName ($columns) VALUES (${getInsertValues(mc)})")

  protected def getNewId: Int = {
    val query = s"SELECT MAX(id) AS maxId FROM $tableName"
    val rs = stat.executeQuery(query)

    1 + (if (rs.next()) rs.getInt("maxId") else 0)
  }

  protected def getStringForInsert(optStr: Option[String]): String = optStr.fold("NULL"){ x => s"'$x'"}
}
