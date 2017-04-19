import org.scalatest._

class TriangulatorSpec extends FlatSpec with Matchers {
  "triangulator" should "triangulate both points outside(different sides)" in {
    assert(
      Triangulator.triangulate(
        List(3.0, 1.8, 4.7),
        List(2.8, 4.2, 1.5),
        List(2.6, 3.0, 2.5)) === 5.5 +- 0.25)
  }

  it should "triangulate both points outside (same side)" in {
    assert(
      Triangulator.triangulate(
        List(3.5, 6.0, 3.6),
        List(3.8, 5.0, 2.1),
        List(3.4, 3.0, 3.3)) === 2.0 +- 0.25)
  }

  it should "triangulate both points, one inside, one outside, B, X, C, Y" in {
    assert(
      Triangulator.triangulate(
        List(3.5, 1.6, 2.0),
        List(4.2, 4.8, 1.9),
        List(2.9, 2.9, 2.9)) === 3.7 +- 0.25)
  }

  it should "triangulate both points, one inside, one outside, B, Y, C, X" in {
    assert(
      Triangulator.triangulate(
        List(4.2, 4.8, 1.9),
        List(3.5, 1.6, 2.0),
        List(2.9, 2.9, 2.9)) === 3.7 +- 0.25)
  }

  it should "triangulate both points, one inside, one outside, X, B, Y, C" in {
    assert(
      Triangulator.triangulate(
        List(3.1, 1.3, 3.5),
        List(2.9, 1.2, 1.3),
        List(2.5, 2.2, 2.4)) === 2.5 +- 0.25)
  }

  it should "triangulate both points, one inside, one outside, Y, B, X, C" in {
    assert(
      Triangulator.triangulate(
        List(2.9, 1.2, 1.3),
        List(3.1, 1.3, 3.5),
        List(2.5, 2.2, 2.4)) === 2.5 +- 0.25)
  }

  it should "triangulate both points, both inside" in {
    assert(
      Triangulator.triangulate(
        List(5.0, 2.0, 3.2),
        List(5.1, 2.8, 2.3),
        List(3.3, 3.0, 3.4)) === 1.5 +- 0.25)
  }

  it should "triangulate both points, outside, X shaped" in {
    assert(
      Triangulator.triangulate(
        List(1.8, 3.0, 3.7),
        List(2.1, 4.6, 3.3),
        List(2.5, 2.1, 2.1)) === 3.1 +- 0.25)
  }
}