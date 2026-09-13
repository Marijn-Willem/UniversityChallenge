package http

import http.html.FrontHtmlBuilder
import http.route.RouteUtil.getStandardRoute
import http.route.{CityRoute, SeasonEpisodeRoute, SeasonRoute, SeasonTeamRoute, TeamRoute}
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
    pathPrefix("css") {
      getFromResourceDirectory("css")
    },
    pathPrefix("js") {
      getFromResourceDirectory("js")
    },
    path("") {
      getStandardRoute(FrontHtmlBuilder.getHtmlFrontPage)
    },
    CityRoute.route,
    TeamRoute.route,
    SeasonRoute.route,
    SeasonTeamRoute.route,
    SeasonEpisodeRoute.route
  )

  def main(args: Array[String]): Unit = {
    val behaviors = Behaviors.setup { ctx =>
      implicit val system: ActorSystem[Nothing] = ctx.system
      implicit val ec: ExecutionContext = system.executionContext

      val port = Try { System.getenv("UC_PORT").toInt }.toOption.getOrElse(8082)

      Http().newServerAt("127.0.0.1", port).bind(route)
        .map(_.addToCoordinatedShutdown(10.seconds))

      Behaviors.same
    }

    ActorSystem(behaviors, "main-system")
  }
}
