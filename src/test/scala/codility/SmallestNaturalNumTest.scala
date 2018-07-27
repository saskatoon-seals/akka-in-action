package codility

import org.scalatest.FunSuite

import codility.SmallestNaturalNum.solution

class SmallestNaturalNumTest extends FunSuite {
  test("first") {
    val as = Array(1, 3, 6, 4, 1, 2)

    assert(solution(as) == 5)
  }

  test("second") {
    val as = Array(10)

    assert(solution(as) == 1)
  }

  test("third") {
    val as = Array(1, 2)

    assert(solution(as) == 3)
  }

  test("forth") {
    assert(solution(Array(-1, -3)) == 1)
  }

  test("fifth") {
    assert(solution(Array(4, 5, 6, 2)) == 1)
  }
}
