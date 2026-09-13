package http.route

import RouteUtil.getStandardRoute
import http.html.SeasonHtmlBuilder
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route

object SeasonRoute {
  val route: Route = concat(
    path("season" / "portal") {
      getStandardRoute(SeasonHtmlBuilder.getHtmlSeasonPortal)
    },
    path("season" / "options") {
      getStandardRoute(SeasonHtmlBuilder.getHtmlSeasonOptions)
    },
    path("season" / "manage") {
      parameters("id".?) { id =>
        getStandardRoute(SeasonHtmlBuilder.getHtmlManageSeason(id.map(_.toInt)))
      }
    },
    path("season" / "upsert") {
      parameters("nm", "id".?) { (nm, id) =>
        getStandardRoute(SeasonHtmlBuilder.getResultUpsertSeason(nm, id.map(_.toInt)))
      }
    }
  )
}
