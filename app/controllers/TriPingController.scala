package controllers

import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

class TriPingController extends  Controller {
  def main = Action { Ok(views.html.index("Your new application is ready.")) }
  def triping = Action.async({request => {
    Future.successful(Ok(views.html.index("What!!!!")))
  }})
}
