package codewars

abstract class Solutions {
  def repeatStr(times: Int, str: String): String = str * times

  def isAlphabetic(s: String): Boolean = {
    if (s.length <= 1) true
    else {
      val l = s.toList
      val result = l zip (l.tail) find { case (c1, c2) => c1 > c2 }

      if (result.isEmpty) true else false
    }
  }
}
