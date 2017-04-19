case class DoubleWithExp(d:Double) {
  def **(x:Double):Double = Math.pow(d, x)
  def =~=(r:Double):Boolean = Math.abs(d - r) < 0.1
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
    val BAC = Math.acos((ab**2 + ca**2 - bc**2) / (2 * ab * ca))
    val BAX = Math.acos((ab**2 + ax**2 - bx**2) / (2 * ab * ax))
    val XAC = Math.acos((ax**2 + ca**2 - cx**2) / (2 * ax * ca))
    val BAY = Math.acos((ab**2 + ay**2 - by**2) / (2 * ab * ay))
    val YAC = Math.acos((ay**2 + ca**2 - cy**2) / (2 * ay * ca))
    // first lets determine triangles ABC and XAY composition (do they intersect)
    val isXInsideBAC = BAX + XAC =~= BAC
    val isYInsideBAC = BAY + YAC =~= BAC

    val XAY:Double = {
      if (!isXInsideBAC && !isYInsideBAC) {
        if ((BAC + YAC =~= BAY) && (BAC + XAC =~= BAX)) { // B, C, [Y | X]
          if (BAY < BAX) { BAX - BAY } // B, C, Y, X
          else { BAY - BAX } // B, C, X, Y
        } else if ((BAC + BAY =~= YAC) && (BAC + BAX =~= XAC)) { // [Y | X], B, C
          if (BAY < BAX) { XAC - YAC } // X, Y, B, C
          else { YAC - XAC } // Y, X, B, C
        } else { Math.min(BAX, XAC) + BAC + Math.min(BAY, YAC) }
      } else if (isXInsideBAC && isYInsideBAC) { // solve case when both are inside
        if (BAX < BAY) { // X is closer to AB then Y, order is B, X, Y, C
          BAC - BAX - YAC
        } else { // Y is closer to AB then X, order is B, Y, X, C
          BAC - BAY - XAC
        }
      } else { // one is inside, one outside
        if (isXInsideBAC) {
          if (BAY > YAC) { BAY - BAX } // B, X, C, Y
          else { YAC - XAC } // Y, B, X, C
        } else { // if (isYInsideBAC)
          if (BAX > XAC) { BAX - BAY} // B, Y, C, X
          else { XAC - YAC} // X, B, Y, C
        }
      }
    }

    val xy = Math.sqrt(ax**2 + ay**2 - 2 * ax * ay * Math.cos(XAY))
    xy
  }
}
