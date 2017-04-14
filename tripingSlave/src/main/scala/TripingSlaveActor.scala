import akka.actor.{Actor, ActorRef, Terminated}
import akka.actor.Actor.Receive
import akka.event.Logging
import akka.util.Timeout

import scala.concurrent.duration._
import controllers.protocols.{TripingMasterActor, TripingSlaveActor}
import akka.pattern._

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.sys.process._
import scala.util

class TripingSlaveActor extends Actor {
  private val log = Logging(context.system, this)
  private val myIp = context.system.settings.config.getString("triping.myIp")
  private val masterUri = context.system.settings.config.getString("triping.tripingMaster")
  implicit val timeout = Timeout(5 seconds)

  self ! TripingSlaveActor.Enslave()

  def enslave():Unit = {
    val master:ActorRef = Await.result(context.actorSelection(masterUri).resolveOne, 5 seconds)
    context.watch(master)
    master ! TripingMasterActor.Enslave(myIp)
  }

  override def receive: Receive = {
    case TripingSlaveActor.Enslave() =>
      enslave()
    case TripingMasterActor.Enslaved() =>
      log.info("enslaved! oh yeah!")
      context.become(active)
    case Terminated(deadOne) =>
      log.warning("oh no! master lost! trying to become enslaved again")
      self ! TripingSlaveActor.Enslave()
  }

  def active:Receive = {
    case TripingSlaveActor.Ping(host) =>
      log.info("ping requested")
      val asker = sender()
      pingHost(host).onComplete(res => {
          log.info(s"ping succeeded with $res")
          asker ! TripingSlaveActor.Pinged(res.flatMap(identity))
        })

    case Terminated(deadOne) =>
      log.warning("oh no! master lost! trying to become enslaved again")
      context.become(receive)
      self ! TripingSlaveActor.Enslave()
  }

  def pingHost(host:String):Future[Try[Double]] = {
    Future.apply({
      val r = "rtt min/avg/max/mdev = .+?/(.+?)/.+".r
      val out:String = s"ping -4 -c4 $host".!!
      Try({
        val r(avg) = out.split("\n").last
        avg.toDouble
      })
    })
  }

}
