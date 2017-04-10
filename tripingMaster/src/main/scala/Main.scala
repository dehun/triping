import akka.actor._

object Main extends App {
  val system = ActorSystem("triping")
  system.actorOf(Props[TripingMasterActor], name = "tripingMaster")
  Console.println("triping master is rising")
}
