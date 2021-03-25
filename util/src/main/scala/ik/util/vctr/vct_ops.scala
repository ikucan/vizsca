package ik.util.vctr

/**
 * abstract ancestor of all vectors
 */
abstract class vctr[T](val v: Array[T]) {
  protected def chk[S](v2: Array[S]) = if (v.size != v2.size) throw new vct_err("other vector has mismatching size. expected : " + v.size + ", got " + v2.size)
  def apply(idx: Array[Boolean]): Array[T]
  def apply(idx: Array[Int]): Array[T]
  def apply(idx: Array[Int], fill: T): Array[T]

}

/**
 * trait providing arithmetic operations over vectors
 */
trait vctr_arthmtc[T] {
  self: vctr[T] =>
  def +(v2: Array[T]): Array[T]
  def +(v2: T)(implicit m: Manifest[T]): Array[T]

  def -(v2: Array[T]): Array[T]
  def -(v2: T)(implicit m: Manifest[T]): Array[T]

  def *(v2: Array[T]): Array[T]
  def *(v2: T)(implicit m: Manifest[T]): Array[T]

  def /(v2: Array[T]): Array[T]
  def /(v2: T)(implicit m: Manifest[T]): Array[T]
}

/**
 * trait providing comparison operations over vectors
 */
trait vctr_cmprsn[T] {
  self: vctr[T] =>
  def >(v2: Array[T]): Array[Boolean]
  def >(v2: T)(implicit m: Manifest[T]): Array[Boolean]

  def >=(v2: Array[T]): Array[Boolean]
  def >=(v2: T)(implicit m: Manifest[T]): Array[Boolean]

  def <(v2: Array[T]): Array[Boolean]
  def <(v2: T)(implicit m: Manifest[T]): Array[Boolean]

  def <=(v2: Array[T]): Array[Boolean]
  def <=(v2: T)(implicit m: Manifest[T]): Array[Boolean]

  /**
   * errhm. equality as '==' cannot be overriden
   */
  def ===(v2: Array[T]): Array[Boolean]
  def ===(v2: T)(implicit m: Manifest[T]): Array[Boolean]

  /**
   * between operator. return true where value of a vector is on interval [v1, v2]
   */
  def <|<(v1: T, v2: T)(implicit m: Manifest[T]): Array[Boolean]
}

/**
 * trait providing summary statistics over vectors
 */
trait vctr_stats[T] {
  self: vctr[T] =>
  /**
   * sum of all elements in the vector
   */
  def sum: T
  /**
   * mean value of the elements
   */
  def mean: Double
  /**
   * true if the vector is monotonically increasing
   */
  def mntnc: Boolean
}

/**
 * trait providing vector operations/transformations over vectors
 */
trait vctr_ops[T] {
  self: vctr[T] =>
  def abs: Array[T]
  def log: Array[T]
  def dff: Array[T]
  def is_nan: Array[Boolean]
  def unary_-- : Array[T] = dff
  def cumsum: Array[T]
  def unary_++ : Array[T] = cumsum
  //def forward_fll : Array[T]
}

/**
 * trait providing logic operations over vectors. primarily indended
 * for vectors of booleans
 */
trait lgc[T] {
  self: vctr[T] =>
  /**
   * unary not operation
   */
  def unary_!(): Array[T]
  /**
   * a logical and operation
   */
  def &&(v2: Array[T]): Array[T]
  /**
   * a logical or operation
   */
  def ||(v2: Array[T]): Array[T]
}

/**
 * trait providing comparison operations over vectors
 */
trait qry[T] {
  self: vctr[T] =>
  /**
   * list all distinct values of a vector. two elements are distinct
   * if <e1> == <e2> is not true
   * guarantees to return distinct elements in the first found order
   *
   */
  def dstnct: Array[T]
  /**
   * create an index vector where the index is true if the
   * passed function returns true for the corresponding element
   */
  def apply(foo: (T) => Boolean): Array[Boolean]

  /**
   * find location of some elements in the vector. if multiple instances of an element are present in the vector
   * only one arbitrary index is returned
   * @param o vector, whose elements' indices are to be found
   * @return index which specifies the location of each element from o in the vector or -1 if the element of o is not found in the vector
   */
  def fnd(o: Array[T]): Array[Int]
  def fnd_frst(v: T): Int = throw new vct_err("method not implemented")
  def fnd_lst(v: T): Int = throw new vct_err("method not implemented")

  /**
   * find the index
   */
  def lte(o: Array[T]): Array[Int]

  /**
   * search for index of the values v1 in vector v
   * if there is no such value in vector v the following
   * scenarios are possible:
   *    1. index returned is the index of the first value greater than the index value
   *    2. if 1. results in an index outside the vector. the index is trimmed to last element of the vector
   */
  def srch(v1: T, v2: T): (Int, Int)
  /**
   * sub-sample n equidistant elements from the array
   */
  def ssmpl(n: Int)(implicit m: Manifest[T]): Array[T] =
    if (v.size > n) {
      val rv = new Array[T](n)
      rv(0) = v(0)
      rv(n - 1) = v.last
      val d = v.size.toDouble / n.toDouble
      (1 until rv.size) foreach { i => rv(i) = v(math.round(i.toDouble * d).toInt) }
      rv
    } else v

  /**
   * find the index of sub-sampled n equidistant elements from the array
   */
  def ssmpl_idx(n: Int): Array[Int] =
    if (v.size > n) {
      val rv = new Array[Int](n)
      rv(0) = 0
      rv(n - 1) = v.size - 1
      val d = v.size.toDouble / n.toDouble
      (1 until rv.size) foreach { i => rv(i) = math.round(i.toDouble * d).toInt }
      rv
    } else (0 until v.size) toArray

}
