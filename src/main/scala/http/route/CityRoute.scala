package http.route

import http.html.CityHtmlBuilder
import http.route.RouteUtil.getStandardRoute
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route

object CityRoute {
  val route: Route = concat(
    path("city" / "portal") {
      getStandardRoute(CityHtmlBuilder.getHtmlCityPortal)
    },
    path("city" / "options") {
      getStandardRoute(CityHtmlBuilder.getHtmlCityOptions)
    },
    path("city" / "manage") {
      parameters("id".?) { id =>
        getStandardRoute(CityHtmlBuilder.getHtmlManageCity(id.map(_.toInt)))
      }
    },
    path("city" / "upsert") {
      parameters("nm", "id".?) { (name, id) =>
        getStandardRoute(CityHtmlBuilder.getResultUpsertCity(name, id.map(_.toInt)))
      }
    }
  )
}
