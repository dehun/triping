package controllers

object protocols {
  object TripingSlaveActor {
    case class Enslave()
    case class Ping(host:String)
    case class Pinged(ping:Either[String, Double])
  }

  object TripingMasterActor {
    case class Enslave()
    case class Enslaved()

    case class Triangulate(left:String, right:String)
    case class Triangulated(result:Either[String, Double])
  }
}
