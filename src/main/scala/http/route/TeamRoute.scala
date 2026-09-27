package http.route

import http.html.TeamHtmlBuilder
import RouteUtil.getStandardRoute
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route

object TeamRoute {
  val route: Route = concat(
    path("team" / "portal") {
      parameters("id".?) { id =>
        getStandardRoute(TeamHtmlBuilder.getHtmlTeamPortal(id.map(_.toInt)))
      }
    },
    path("team" / "options") {
      parameters("id".?) { id => 
        getStandardRoute(TeamHtmlBuilder.getHtmlTeamOptions(id.map(_.toInt)))
      }
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
