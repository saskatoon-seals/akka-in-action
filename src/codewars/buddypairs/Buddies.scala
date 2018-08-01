package codewars.buddypairs

object Buddies {
  def buddy(start: Long, limit: Long): String = {
    val result = Stream.range(start, limit + 1)
      .map(n => getBuddy(n) map((n, _)))
      .find(!_.isEmpty)
      .flatten

    result map { case (n, m) => "(%d %d)".format(n, m) } getOrElse("Nothing")
  }

  def getBuddy(n: Long): Option[Long] = {
    val m = f(n)

    if (m > n && n == f(m)) Some(m) else None
  }

  def s(n: Long): Long = {
    findDivisorPairs(n)
      .foldLeft(0: Long)({ case (sum, (a, b)) => if (a == b) a + sum else a + b + sum }) - n
  }

  def f(n: Long): Long = {
    s(n) - 1
  }

  def findDivisorPairs(n: Long): Stream[(Long, Long)] = {
    Stream.range(1, Math.sqrt(n).ceil.toLong + 1)
      .filter(x => n % x == 0)
      .map(x => (x, n / x))
      .takeWhile({ case (a, b) => a <= b })
  }
}