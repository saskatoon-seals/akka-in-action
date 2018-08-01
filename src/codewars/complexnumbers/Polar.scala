package codewars.complexnumbers

//th is an angle in degrees
case class Polar(r: Double, th: Double) extends ComplexNumber[Polar] {
  import Math.{cos, sin, toRadians}

  def toPolar: Polar = this

  def toRectangular: Rectangular = Rectangular(
    r * cos(toRadians(th)),
    r * sin(toRadians(th))
  )

  def **(n: Int): Polar =
    Polar(Math.pow(r, n), (n * th) % 360)

  def *(other: Polar): Polar = (this.toRectangular * other.toRectangular).toPolar

  override def +(other: Polar): Polar = (this.toRectangular + other.toRectangular).toPolar
  override def -(other: Polar): Polar = (this.toRectangular - other.toRectangular).toPolar

  def abs: Double = toRectangular.abs

  override def equals(other: Any): Boolean = other match {
    case other: Polar => toRectangular.equals(other.toRectangular)
    case _ => false
  }
}
