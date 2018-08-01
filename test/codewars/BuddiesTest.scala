package codewars

import org.scalatest._
import org.scalatest.Assertions._

import BuddiesTest._

import codewars.buddypairs.Buddies._

class BuddiesTest extends FlatSpec {
  it should "pass basic tests" in {
    testing(10, 50, "(48 75)")
    testing(1071625, 1103735, "(1081184 1331967)")
    testing(57345, 90061, "(62744 75495)")
    testing(2382, 3679, "Nothing")
  }

  it should "pass complex tests" in {
    testing(72146, 76845, "Nothing")
    testing(1168327, 1174073, "(1173704 1341495)")
  }
}

object BuddiesTest {
  private def testing(start: Long, limit: Long, expected: String): Unit = {
    println("start: " + start)
    println("limit: " + limit)
    val actual: String = buddy(start, limit)
    println("Expect: " + expected)
    assertResult(expected){actual}
  }
}

class BasicTest extends FunSuite {
  test("s calculates the sum of proper divisors") {
    assert(s(48) == 76)
    assert(s(75) == 49)
  }

  test("getBuddy returns buddy for a given number") {
    assert(getBuddy(48) == Some(75))
    assert(getBuddy(1081184) == Some(1331967))
  }

  test("getBuddy returns None when number doesn't have a buddy") {
    assert(getBuddy(49) == None)
  }

  test("finds divisor pairs") {
    assert(findDivisorPairs(48).toList == List((1, 48), (2, 24), (3, 16), (4, 12), (6, 8)))
    assert(findDivisorPairs(100).toList == List((1, 100), (2, 50), (4, 25), (5, 20), (10, 10)))
  }

  test("Calculates the Aliquot sum") {
    assert(s(48) == 76)
  }
}
