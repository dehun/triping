import akka.actor._

object Main extends App {
  val system = ActorSystem("triping")
  system.actorOf(Props[TripingSlaveActor], name = "tripingSlave")
  Console.println("triping slave is rising")
}
