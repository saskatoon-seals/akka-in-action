package codewars

import org.scalatest._
import scala.util.Random
import codewars.duplicateencoding.Solution._

class DuplicateEncodeSuite extends FunSpec {

  val basicExamples = Seq(
    ("din", "((("),
    ("recede", "()()()"),
    ("Success", ")())())"),
    ("(( @", "))((")
  )

  basicExamples.foreach { case (decoded, encoded) =>
    it(s"should return ${encoded} for input ${decoded}") {
      assert(duplicateEncode(decoded) == encoded)
    }
  }
}