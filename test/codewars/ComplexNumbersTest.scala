package codewars

import codewars.complexnumbers.{Polar, Rectangular}
import codewars.complexnumbers.ComplexNumbers._
import org.scalatest.FunSuite

class ComplexNumbersTest extends FunSuite {
//  test("calculate works correctly") {
//    val z = codewars.complexnumbers.Rectangular(0.3, 0.5)
//
//    assert(
//      (one - z) * (z + z**2 + z**3 + z**4 + z**5 + z**6 + z**7 + z**8 + z**9 + z**10 + zero)
//        == calculate(z, 10)(zero, one)
//    )
//  }

  test("converts polar to rectangular form") {
    //first quadrant
    assert(Polar(2.5, 20).toRectangular.toPolar == Polar(2.5, 20))

    //second quadrant
    assert(Polar(2.5, 120).toRectangular.toPolar == Polar(2.5, 120))

    //third quadrant
    assert(Polar(2.5, 195).toRectangular.toPolar == Polar(2.5, 195))

    //forth quadrant
    assert(Polar(2.5, 300).toRectangular.toPolar == Polar(2.5, 300))
  }

  test("squaring and multiplying numbers") {
    val rect = Rectangular(1.5, 2.7)

    assert(rect**2 == rect * rect)
    assert(rect**3 == rect * rect * rect)
    assert(rect**4 == rect * rect * rect * rect)
  }

  test("adding numbers") {
    val rect = Rectangular(1.5, 2.7)

    assert(rect + rect + rect == rect * Rectangular(3.0, 0))
  }

  test("subtracting numbers") {
    val rect = Rectangular(1.5, 2.7)

    assert((zero - rect) - rect - rect == rect * Rectangular(3.0, 0) * (zero - one))
  }

  test("f successfully found a limit") {
    assert(f(0.3, 0.5, 0.0001) == 17)

    assert(f(0.64, 0.75, 1e-12) == 1952)
  }

  test("f unable to found a limit") {
    assert(f(30, 5, 1e-4) == -1)
  }
}