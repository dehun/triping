
import akka.actor.{Actor, ActorRef, Terminated}
import akka.event.Logging
import akka.pattern._
import akka.util.Timeout
import controllers.protocols.{TripingMasterActor, TripingSlaveActor}

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import scalaz._, Scalaz._

class TripingMasterActor extends Actor {
  private val log = Logging(context.system, this)
  var slaves:Set[ActorRef] = Set.empty[ActorRef]

  override def receive: Receive = {
    case TripingMasterActor.Enslave() =>
      slaves = slaves.+(sender())
      log.info(s"got new slave ${sender()}, now slaves are ${slaves}")
      context.watch(sender())
      sender() ! TripingMasterActor.Enslaved()

    case Terminated(deadOne) =>
      slaves -= deadOne
      log.warning(s"slave ${deadOne} is dead, now slaves are ${slaves}")

    case TripingMasterActor.Triangulate(left, right) =>
      val asker = sender()
      if (slaves.size < 0) {
        log.warning("too few slaves!!!")
        asker ! TripingSlaveActor.Pinged(Left("too few slaves"))
      } else {
        implicit val timeout = Timeout(5 seconds)
        def pingWithSlaves(host:String):Future[List[Either[String, Double]]] = Future.sequence(slaves.toList.map(
          s => ask(s, TripingSlaveActor.Ping(host)).mapTo[Either[String, Double]]))
        for {leftPingsE <- pingWithSlaves(left)
             rightPingsE <- pingWithSlaves(right) } {
          val avgPing = leftPingsE.sequenceU.flatMap(leftPings =>
            rightPingsE.sequenceU.map(rightPings => (leftPings ++ rightPings).max))
           asker ! TripingMasterActor.Triangulated(avgPing)
        }
      }
   }
}

