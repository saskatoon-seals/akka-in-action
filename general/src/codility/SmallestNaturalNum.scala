package codility

object SmallestNaturalNum {
  def solution(a: Array[Int]): Int = {
    val positiveAs = a.sorted.filter(_ > 0)

    //positiveAs(0) > 1 case was missed when solving in real-time
    if (positiveAs.length == 0 || positiveAs(0) > 1) 1
    else {
      val result = positiveAs zip { positiveAs.tail } find { case (a1, a2) => (a2 - a1) > 1 }

      result map { case (a1, _) => a1 + 1} getOrElse { positiveAs.last + 1 }
    }
  }
}
