package codewars

import scala.annotation.tailrec

object JohnAndAnn {
  val ANN_TABLE0 = Map(0L -> 1L)
  val JOHN_TABLE0 = Map(0L -> 0L)
  val START = 1

  def sumAnn(n: Long): Long = {
    go(ANN_TABLE0, JOHN_TABLE0, START, n)
      .sum
  }

  def ann(n: Long): String = {
    go(ANN_TABLE0, JOHN_TABLE0, START, n)
      .mkString(", ")
  }

  def john(n: Long): String = {
    go(JOHN_TABLE0, ANN_TABLE0, START, n)
      .mkString(", ")
  }

  def sumJohn(n: Long): Long = {
    go(JOHN_TABLE0, ANN_TABLE0, START, n)
      .sum
  }

  @tailrec
  def go(t1: Map[Long, Long], t2: Map[Long, Long], i: Long, n: Long): List[Long] = {
    if (i >= n)
      t1.values.toList.sorted
    else {
      val (x, (iy, y)) = f(t1, t2, i)

      go(
        t1 + (i -> x),
        t2 + (iy -> y),
        i + 1,
        n
      )
    }
  }

  def f(t1: Map[Long, Long], t2: Map[Long, Long], n: Long): (Long, (Long, Long)) = {
    val t = t1.get(n-1).get

    val x = t2.getOrElse(
      t,
      f(t2, t1, t)._1
    )

    (n - x, (t, x))
  }
}
