package codewars

import codewars.BudTest._
import codewars.buddypairs.Bud._
import org.scalatest.Assertions._
import org.scalatest._

class BudTest extends FlatSpec {
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

object BudTest {
  private def testing(start: Long, limit: Long, expected: String): Unit = {
    println("start: " + start)
    println("limit: " + limit)
    val actual: String = buddy(start, limit)
    println("Expect: " + expected)
    assertResult(expected){actual}
  }
}