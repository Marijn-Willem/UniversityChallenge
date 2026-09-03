package db.conn

import java.sql.{DriverManager, Statement}
import java.util.Properties

object DbManager {
  private val host = "jdbc:postgresql://127.0.0.1:5432"
  private val db = "universitychallenge"
  private val props = {
    val p = new Properties()
    
    p.setProperty("user", "postgres")
    p.setProperty("password", "postgres")
    
    p
  }
  
  def getStringFromStatement(fStat: Statement => String): String = {
    val conn = DriverManager.getConnection(s"$host/$db", props)
    val stat = conn.createStatement()
    
    val str = fStat(stat)
    
    conn.close()
    
    str
  }
}
