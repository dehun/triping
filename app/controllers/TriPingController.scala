package controllers

import akka.actor.ActorSystem
import play.api.mvc.{Action, Controller}
import akka.pattern._
import akka.util.Timeout
import com.google.inject.Inject
import controllers.protocols.TripingMasterActor

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class TriPingController @Inject () (configuration: play.api.Configuration, val system:ActorSystem) extends Controller {
  implicit private val timeout = Timeout (30 seconds)
  private val masterUri = configuration.getString("triping.tripingMaster").get
  def main = Action { Ok(views.html.index("Your new application is ready.")) }
  def triping(left:String, right:String) = Action.async({request => {
    ask(system.actorSelection(masterUri), TripingMasterActor.Triangulate(left, right))
      .map(r => Ok(r.toString))
  }})
}
