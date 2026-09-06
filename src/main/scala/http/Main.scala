package http

import http.html.{CityHtmlBuilder, FrontHtmlBuilder}
import org.apache.pekko
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity}
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.StandardRoute

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
    path("city" / "portal") {
      getStandardRoute(CityHtmlBuilder.getHtmlCityPortal)
    },
    path("city" / "options") {
      getStandardRoute(CityHtmlBuilder.getCityOptions)
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

  private def getStandardRoute(html: String): StandardRoute = complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html))
}
