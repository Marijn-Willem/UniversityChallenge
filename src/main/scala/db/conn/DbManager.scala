package db.conn

import util.EnvironmentUtil.getEnvironmentVariable
import java.sql.{DriverManager, Statement}
import java.util.Properties

object DbManager {
  private val dbConn = "jdbc:postgresql://" +
    s"${getEnvironmentVariable("UC_DBCONN", "127.0.0.1:5432/universitychallenge")}"
  private val props = {
    val p = new Properties()
    
    p.setProperty("user", getEnvironmentVariable("UC_DBUSER", "postgres"))
    p.setProperty("password", getEnvironmentVariable("UC_DBPW", "postgres"))
    
    p
  }
  
  def getStringFromStatement(fStat: Statement => String): String = {
    val conn = DriverManager.getConnection(s"$dbConn", props)
    val stat = conn.createStatement()
    
    val str = fStat(stat)
    
    conn.close()
    
    str
  }
}
