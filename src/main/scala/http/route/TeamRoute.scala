package http.route

import http.html.TeamHtmlBuilder
import RouteUtil.getStandardRoute
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route

object TeamRoute {
  val route: Route = concat(
    path("team" / "portal") {
      getStandardRoute(TeamHtmlBuilder.getHtmlTeamPortal)
    },
    path("team" / "options") {
      getStandardRoute(TeamHtmlBuilder.getHtmlTeamOptions)
    },
    path("team" / "manage") {
      parameters("id".?) { id =>
        getStandardRoute(TeamHtmlBuilder.getHtmlManageTeam(id.map(_.toInt)))
      }
    },
    path("team" / "upsert") {
      parameters("nm", "cid", "id".?) { (name, cid, id) =>
        getStandardRoute(TeamHtmlBuilder.getResultUpsertTeam(name, cid.toInt, id.map(_.toInt)))
      }
    }
  )
}
