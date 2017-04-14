package controllers

import scala.util.Try

object protocols {
  object TripingSlaveActor {
    case class Enslave()
    case class Ping(host:String)
    case class Pinged(ping:Try[Double])
  }

  object TripingMasterActor {
    case class Enslave(ip:String)
    case class Enslaved()

    case class Triangulate(left:String, right:String)
    case class Triangulated(result:Try[Double])
  }
}
