package http.route

import RouteUtil.getStandardRoute
import http.html.SeasonTeamHtmlBuilder
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route

object SeasonTeamRoute {
  val route: Route = concat(
    path("seasonteam" / "portal") {
      parameters("tid") { teamId =>
        getStandardRoute(SeasonTeamHtmlBuilder.getHtmlSeasonTeamPortal(teamId.toInt))
      }
    },
    path("seasonteam" / "options") {
      parameters("tid") { teamId =>
        getStandardRoute(SeasonTeamHtmlBuilder.getHtmlSeasonTeamOptions(teamId.toInt))
      }
    },
    path("seasonteam" / "insert") {
      parameters("sid", "tid") { (seasonId, teamId) =>
        getStandardRoute(SeasonTeamHtmlBuilder.getResultInsertSeasonTeam(seasonId.toInt, teamId.toInt))
      }
    }
  )
}
