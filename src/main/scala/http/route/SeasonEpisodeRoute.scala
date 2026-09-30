package http.route

import RouteUtil.getStandardRoute
import http.html.SeasonEpisodeHtmlBuilder
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route

object SeasonEpisodeRoute {
  val route: Route = concat(
    path("seasonepisode" / "portal") {
      parameters("sid", "eid".?) { (sid, eid) =>
        getStandardRoute(SeasonEpisodeHtmlBuilder.getHtmlSeasonEpisodePortal(sid.toInt, eid.map(_.toInt)))
      }
    },
    path("seasonepisode" / "options") {
      parameters("sid", "eid".?) { (sid, eid) =>
        getStandardRoute(SeasonEpisodeHtmlBuilder.getSeasonEpisodeOptions(sid.toInt, eid.map(_.toInt)))
      }
    },
    path("seasonepisode" / "insert") {
      parameters("sid") { sid =>
        getStandardRoute(SeasonEpisodeHtmlBuilder.getResultInsertSeasonEpisodes(sid.toInt))
      }
    },
    path("seasonepisode" / "manage") {
      parameters("sid", "eid") { (sid, eid) =>
        getStandardRoute(SeasonEpisodeHtmlBuilder.getHtmlManageSeasonEpisode(sid.toInt, eid.toInt))
      }
    },
    path("seasonepisode" / "update") {
      parameters("sid", "eid", "t1id".?, "t2id".?, "s1".?, "s2".?, "dt".?) { (sid, eid, t1id, t2id, s1, s2, dt) =>
        getStandardRoute(SeasonEpisodeHtmlBuilder.getResultUpdateSeasonEpisode(sid.toInt, eid.toInt,
          t1id.map(_.toInt), t2id.map(_.toInt), s1.map(_.toInt), s2.map(_.toInt), dt))
      }
    },
    path("seasonepisode" / "table") {
      parameters("sid") { sid =>
        getStandardRoute(SeasonEpisodeHtmlBuilder.getSeasonEpisodeTableRows(sid.toInt))
      }
    }
  )
}
