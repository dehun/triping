case class DoubleWithExp(d:Double) {
  def **(x:Double):Double = Math.pow(d, x)
}

object DoubleWithExp {
  implicit def doubleWithExp(x: Double): DoubleWithExp = new DoubleWithExp(x)
}

object Triangulator {
  def triangulate(leftPings:List[Double], rightPings:List[Double], slavesPings:List[Double]):Double = {
    import DoubleWithExp._
    val List(ax, bx, cx) = leftPings
    val List(ay, by, cy) = rightPings
    val List(ab, bc, ca) = slavesPings

    val cosBAC = (ab**2 + ca**2 - bc**2) / (2 * ab * ca)
    val BAC = Math.acos(cosBAC)
    val cosBAX = (ab**2 + ax**2 - bx**2) / (2 * ab * ax)
    val BAX = Math.acos(cosBAX)
    val cosCAY = (ca**2 + ay**2 - cy**2) / (2 * ca * ay)
    val CAY = Math.acos(cosCAY)
    val cosXAC = (ax**2 + ca**2 - cx**2) / (2 * ax * ca)
    val XAC = Math.acos(cosXAC)

    val XAY = BAC - CAY + (if (BAC < XAC) XAC else (-XAC))
    val cosXAY = Math.cos(XAY)
    val xy = Math.sqrt(ax**2 + ay**2 - 2 * ax * ay * cosXAY)
    xy
  }
}
