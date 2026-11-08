package http

import http.html.FrontHtmlBuilder
import http.route.RouteUtil.getStandardRoute
import http.route.{CityRoute, SeasonEpisodeRoute, SeasonRoute, SeasonTeamRoute, TeamRoute}
import util.EnvironmentUtil.getEnvironmentVariable
import org.apache.pekko
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Directives.*

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.util.Try

object Main {
  private val route = concat(
    pathPrefix("css") { getFromResourceDirectory("css") },
    pathPrefix("js") { getFromResourceDirectory("js") },
    path("") { getStandardRoute(FrontHtmlBuilder.getHtmlFrontPage) },
    CityRoute.route,
    TeamRoute.route,
    SeasonRoute.route,
    SeasonTeamRoute.route,
    SeasonEpisodeRoute.route
  )

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "main-system")
    implicit val ec: ExecutionContext = system.executionContext

    val host = getEnvironmentVariable("UC_HOST", "127.0.0.1")
    val port = Try { getEnvironmentVariable("UC_PORT", "8082").toInt }.getOrElse(8082)

    Http().newServerAt(host, port).bind(route)
      .map(_.addToCoordinatedShutdown(10.seconds))
  }
}
