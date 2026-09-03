package http.html

import http.html.HtmlUtil

object FrontHtmlBuilder {
  def getHtmlFrontPage: String = HtmlUtil.withHtmlTemplate(_ => {
    def getPortalLink(entity: String): String = s"<a href=\"${entity.toLowerCase}/portal\">$entity</a><br/>"

    val entities = Seq("City")

    s"<div>${entities.map(getPortalLink).mkString("\n")}</div>\n"
  })
}
