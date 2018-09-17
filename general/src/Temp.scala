object Temp {
  /*
   * Elements of sequence are of different types but all of them are a Number supertype
   *
   * What is a difference:
   *  - def sum[A <: Number](seq: Seq[Number], zero: A)
   *
   * Can elements of sequence and a zero be of different sub-types? => YES
   *
   * Subtyping rules:
   *  - Seq[Double] is a subtype of Seq[Number] because of covariance [should be]
   *  - found: Seq[Double] required Seq[Number] ???
   *  - List[Double] is a subtype of Seq[Double]
   *  - List[Double] is not a subtype of Seq[Number] => PROBLEM!
   *
   * One of the working functions:
   *  - def sum[A](seq: Seq[A], zero: A)(implicit n: Numeric[A]): A
   *  - It has a problem that both zero and elements of zero must be of the same subtype
   *  - We want the same supertype but different subtypes
   *
   * I want to have a Seq[Double] and Int as inputs and Int as an output
   */
  def sum[A](seq: Seq[A], zero: A)(implicit op: Numeric[A]): A = {
    seq.foldRight(zero)(op.plus)
  }

  def sum2[A](seq: Seq[A])(zero: A)(implicit op: Numeric[A]): A = {
    seq.foldRight(zero)(op.plus)
  }

  //
//  def sum2[A <: C,B <: C, C](seq: Seq[A], zero: B)(implicit op: Numeric[C]): C = {
//    seq.foldRight(zero)(op.plus)
//  }

  def f[A](seq: Seq[A])(implicit n: Numeric[A]): A = n.plus(seq(0), seq(1))

  //a1 and a2 can be of different subtypes but both being a numeric supertype
  def mult[A](a1: A, a2: A)(implicit ev: Numeric[A]): A =
    ev.times(a1, a2)

  def main(args: Array[String]): Unit = {
    val seq = List(1, 2, 3.5)
    val seq2 = List(2, 8)
    val zero = 0
    val zero2 = 0.0

    /*
     * works:
     *  - sum[Double](Seq[Double], Int) => Int to Double cast (no loss of precision)
     *  - sum(Seq[Int], Int)
     */

    /*
     * doesn't work:
     *  - sum[Double](Seq[Int], Int) => Seq[Int] isn't a subtype of Seq[Double]
     *  - sum[Int](Seq[Int], Double) => Int isn't a subtype of Double (loss of precision)
     */

//    mult(zero, zero2)
//    f(seq2)
    println(
      sum[Double](seq, zero).toString
    )

    sum2(seq)(zero)
    //sum2(seq2)(zero2)
  }
}
