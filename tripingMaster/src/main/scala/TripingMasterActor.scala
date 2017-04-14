
import akka.actor.{Actor, ActorRef, Terminated}
import akka.event.Logging
import akka.pattern._
import akka.util.Timeout

import controllers.protocols.{TripingMasterActor, TripingSlaveActor}

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

import cats._
import cats.data._
import cats.implicits._

class TooFewSlavesError() extends RuntimeException("too few slaves")

class TripingMasterActor extends Actor {
  private val log = Logging(context.system, this)
  var slaves:Map[ActorRef, String] = Map.empty

  override def receive: Receive = {
    case TripingMasterActor.Enslave(ip) =>
      slaves = slaves.+((sender(), ip))
      log.info(s"got new slave ${sender()}, now slaves are ${slaves}")
      context.watch(sender())
      sender() ! TripingMasterActor.Enslaved()

    case Terminated(deadOne) =>
      slaves -= deadOne
      log.warning(s"slave ${deadOne} is dead, now slaves are ${slaves}")

    case TripingMasterActor.Triangulate(left, right) =>
      val asker = sender()
      if (slaves.size < 3) {
        log.warning("too few slaves!!!")
        asker ! TripingSlaveActor.Pinged(Failure(new TooFewSlavesError()))
      } else {
        implicit val timeout = Timeout(20 seconds)
        val selectedSlaves = slaves.take(3).toList
        def pingWithSlaves(host:String):Future[Either[Throwable, List[Double]]] = {
          log.warning(s"ping with slaves $host")
          Future.sequence(selectedSlaves.map(_._1).map(
            s => ask(s, TripingSlaveActor.Ping(host)).mapTo[TripingSlaveActor.Pinged].map(_.ping)))
            .map(_.map(x => Either.fromTry(x)).sequenceU)
            .recover({case ex => Left(ex)})
        }

        def pingSlaves:Future[Either[Throwable, List[Double]]] = {
          val pairs = selectedSlaves.init.zip(selectedSlaves.tail) ++ List((selectedSlaves.last, selectedSlaves.head))
          Future.sequence(
            pairs
              .map({case (s, t) => (s._1, t._2)})
              .map({ case (s, t) => ask(s, TripingSlaveActor.Ping(t)).mapTo[TripingSlaveActor.Pinged].map(_.ping) }))
            .map(_.map(x => Either.fromTry(x)).sequenceU)
        }

        (for {leftPings <- EitherT(pingWithSlaves(left))
             rightPings <- EitherT(pingWithSlaves(right))
             slavePings <- EitherT(pingSlaves)}
          yield triangulate(leftPings, rightPings, slavePings))
          .value.onComplete({
          case Failure(ex) => asker ! TripingMasterActor.Triangulated(Failure(ex))
          case Success(Left(ex)) => asker ! TripingMasterActor.Triangulated(Failure(ex))
          case Success(Right(ping)) => asker ! TripingMasterActor.Triangulated(Success(ping))
        })
      }
   }

  def triangulate(leftPings:List[Double], rightPings:List[Double], slavesPings:List[Double]):Double = {
    val List(ax, bx, cx) = leftPings
    val List(ay, by, cy) = rightPings
    val List(ab, bc, ca) = slavesPings
    ca
  }
}

