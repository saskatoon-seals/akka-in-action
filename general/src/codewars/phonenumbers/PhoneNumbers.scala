package codewars.phonenumbers

object PhoneNumbers {
  def convertExt(input: Seq[Int]) = (
    Leaf("(" + input.take(3).mkString("") + ")"),
    input.drop(3)
  )

  def convertFirst(input: Seq[Int]) = (
    Leaf(input.take(3).mkString("")),
    input.drop(3)
  )

  def convertSecond(input: Seq[Int]) = (
    Leaf(input.take(4).mkString("")),
    input.drop(4)
  )

  def convert(input0: Seq[Int]): Tree = {
    val (ext, input1) = convertExt(input0)
    val (first, input2) = convertFirst(input1)
    val (second, _) = convertSecond(input2)

    val suffix = Node(first, second, "-")

    "".format(input0:_*)

    Node(ext, suffix, " ")
  }

  def createPhoneNumber(input: Seq[Int]): String =
    convert(input).display
}

trait Tree {
  def display: String
}

case class Leaf(value: String) extends Tree {
  override def display: String = value
}

case class Node(left: Tree, right: Tree, delimiter: String) extends Tree {
  override def display: String = left.display ++ delimiter ++ right.display
}
