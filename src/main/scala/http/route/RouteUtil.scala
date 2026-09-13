package http.route

import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity}
import org.apache.pekko.http.scaladsl.server.Directives.complete
import org.apache.pekko.http.scaladsl.server.StandardRoute

object RouteUtil {
  def getStandardRoute(html: String): StandardRoute = complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html))
}
