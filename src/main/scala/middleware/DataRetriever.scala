package middleware

import db.conn.CityManager
import db.model.Model.City

import java.sql.Statement

object DataRetriever {
  def insertCity(stat: Statement, name: String): Unit = {
    val cm = new CityManager(stat)
    val id = cm.getNewId
    
    cm.insert(City(id, Some(name)))
  }
}
