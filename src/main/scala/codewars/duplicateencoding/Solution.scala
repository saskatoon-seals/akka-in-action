package codewars.duplicateencoding

object Solution {

  def duplicateEncode(word: String): String = {
    val lowercaseWord = word.toLowerCase()

    List.fill(10)("a").foldLeft("")(_ ++ _)

    val encoding = lowercaseWord
      .groupBy(identity)
      .mapValues{ value => if (value.length > 1) ')' else '(' }

    lowercaseWord map { c => encoding(c) }
  }
}