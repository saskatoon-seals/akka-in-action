package codewars.complexnumbers

trait ComplexNumber[CN <: ComplexNumber[CN]] { self: CN =>
  def toPolar: Polar
  def toRectangular: Rectangular

  //Type stays in it's domain (e.g. Polar stays Polar after squaring - doesn't turn into Rectangular)
  def **(n: Int): CN

  //Are these 3 functions necessary to define on a trait?
  def *(other: CN): CN
  def +(other: CN): CN
  def -(other: CN): CN

  def abs: Double
}
