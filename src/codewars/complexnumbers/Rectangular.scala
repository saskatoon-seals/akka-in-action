package codewars.complexnumbers

case class Rectangular(x: Double, y: Double) extends ComplexNumber[Rectangular] {
  import Math.{atan, pow, sqrt, toDegrees}

  def toPolar: Polar = {
    val r = sqrt(pow(x, 2) + pow(y, 2))
    val alpha = toDegrees(atan(y/x)).abs

    val theta =
      //first quadrant
      if (x > 0 && y > 0) alpha
      //second quadrant
      else if (x < 0 && y > 0) { 180 - alpha }
      //third quadrant
      else if(x < 0 && y < 0) { 180 + alpha }
      //forth quadrant
      else { 360 - alpha }

    Polar(r, theta)
  }

  def toRectangular: Rectangular = this

  def **(n: Int): Rectangular = (this.toPolar**n).toRectangular

  def *(other: Rectangular): Rectangular = Rectangular(
    x*other.x - y*other.y,
    x*other.y + y*other.x
  )

  def +(other: Rectangular): Rectangular = Rectangular(x + other.x, y + other.y)
  def -(other: Rectangular): Rectangular = Rectangular(x - other.x, y - other.y)

  def abs: Double = sqrt(pow(x, 2) + pow(y, 2))

  override def equals(other: Any): Boolean = other match {
    case other: Rectangular => (this - other).abs < 0.0001
    case _ => false
  }
}
