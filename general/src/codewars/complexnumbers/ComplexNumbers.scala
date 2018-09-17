package codewars.complexnumbers

object ComplexNumbers {
  val zero = Rectangular(0.0, 0.0)
  val one = Rectangular(1.0, 0)

  val maxN = 10000

  def f(x: Double, y: Double, eps: Double): Int = {
    val z0 = Rectangular(x, y)
    val const = one - z0

    findLimit(z0)() match {
      case Some(lim) => toStream(z0, const)
        .scanLeft(zero)(_ + _)
        .map(z => (z - lim).abs)
        .zipWithIndex
        .dropWhile{ case (diff, n) => diff >= eps && n <= maxN }
        .head
        ._2

      case None => -1
    }
  }

  def findLimit(z0: Rectangular)(const: Rectangular = one - z0): Option[Rectangular] = {
    val limit = toStream(z0, const)
      .take(maxN)
      .foldLeft(zero)(_ + _)

    if (limit.abs equals Double.NaN) None else Some(limit)
  }

  def toStream[CN <: ComplexNumber[CN]](z0: CN, const: CN): Stream[CN] = {
    Stream
      .from(1)
      .map(index => z0**index)
      .map(_ * const)
  }
}
