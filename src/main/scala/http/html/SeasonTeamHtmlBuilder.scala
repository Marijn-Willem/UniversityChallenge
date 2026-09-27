package http.html

import db.conn.{DbManager, SeasonManager, SeasonTeamManager}
import db.model.Model.SeasonTeam

import java.sql.Statement

object SeasonTeamHtmlBuilder {
  def getHtmlSeasonTeamPortal(teamId: Int): String = HtmlUtil.withHtmlTemplate((stat: Statement) =>
    "<div>\n<select id=\"selSeason\">\n" +
    "</select>\n</div>\n" +
    "<div><input type=\"button\" onclick=\"handleInsertSeasonTeam();\" value=\"Insert\" /></div>\n" +
    "<div id=\"divIns\"></div>\n" +
    s"<input id=\"inpTeamId\" type=\"hidden\" value=\"$teamId\" />\n"
  , Seq("seasonteam"), Some(s"team/portal?id=$teamId"), Some("loadSeasonTeamOptions()"))

  def getHtmlSeasonTeamOptions(teamId: Int): String = DbManager.getStringFromStatement((stat: Statement) => {
    val seasonIdsWithTeam = new SeasonTeamManager(stat).getSeasonIdsForTeam(teamId)
    val seasons = new SeasonManager(stat).getList()

    val seasonsWithoutTeam = seasons.filterNot(s => seasonIdsWithTeam.contains(s.id))

    seasonsWithoutTeam.map(HtmlUtil.getSelectOption(_, None)).mkString
  })

  def getResultInsertSeasonTeam(seasonId: Int, teamId: Int): String = DbManager.getStringFromStatement((stat: Statement) => {
    new SeasonTeamManager(stat).insert(SeasonTeam(seasonId, teamId))

    "Insert successful"
  })
}
