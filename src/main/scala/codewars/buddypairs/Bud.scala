package codewars.buddypairs

object Bud {
  //O(|start - limit| * sqrt(|start - limit|) = O(n * sqrt(n))
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

  def f(n: Long): Long = {
    s(n) - 1
  }

  //O(sqrt(n))
  def s(n: Long): Long = {
    Stream.range(1, Math.sqrt(n).ceil.toLong + 1)
        .filter(x => n % x == 0)
        .map(x => (x, n / x))
        .foldLeft(0: Long)({ case (sum, (x, y)) => if (x == y) x + sum else x + y + sum}) - n
  }
}