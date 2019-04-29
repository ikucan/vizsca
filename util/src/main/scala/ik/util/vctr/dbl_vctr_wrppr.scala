package ik.util.vctr

import java.util.Arrays
import java.util.Arrays.binarySearch

import scala.Array.canBuildFrom
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet

class dbl_vctr_wrppr(v: Array[Double]) extends vctr[Double](v) with vctr_arthmtc[Double] with vctr_cmprsn[Double] with vctr_ops[Double] with vctr_stats[Double] with qry[Double] {
  override def apply(idx: Array[Boolean]): Array[Double] = {
    chk(idx)
    var ss = new ArrayBuffer[Double](v.size)
    for (i <- 0 until idx.size) if (idx(i)) ss += v(i)
    ss toArray
  }
  override def apply(idx: Array[Int]): Array[Double] = idx map v
  override def apply(idx: Array[Int], dflt: Double): Array[Double] = idx map { i => if (i >= 0 && i < v.size) v(i) else dflt }

  /**
   * basic algebra
   */
  override def +(v2: Array[Double]) = {
    chk(v2)
    val rv = new Array[Double](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) + v2(i)
    rv
  }
  override def +(v2: Double)(implicit m: Manifest[Double]) = {
    val rv = new Array[Double](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) + v2
    rv
  }

  override def -(v2: Array[Double]): Array[Double] = {
    chk(v2)
    val rv = new Array[Double](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) - v2(i)
    rv
  }

  override def -(v2: Double)(implicit m: Manifest[Double]) = {
    val rv = new Array[Double](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) - v2
    rv
  }

  override def *(v2: Array[Double]): Array[Double] = {
    chk(v2)
    val rv = new Array[Double](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) * v2(i)
    rv
  }

  override def *(v2: Double)(implicit m: Manifest[Double]) = {
    val rv = new Array[Double](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) * v2
    rv
  }

  override def /(v2: Array[Double]): Array[Double] = {
    chk(v2)
    val rv = new Array[Double](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) / v2(i)
    rv
  }

  override def /(v2: Double)(implicit m: Manifest[Double]) = {
    val rv = new Array[Double](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) / v2
    rv
  }

  /**
   * [in] equality
   */
  override def <(v2: Array[Double]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) < v2(i)
    rv
  }
  override def <(v2: Double)(implicit m: Manifest[Double]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) < v2
    rv
  }
  override def <=(v2: Array[Double]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) <= v2(i)
    rv
  }
  override def <=(v2: Double)(implicit m: Manifest[Double]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) <= v2
    rv
  }
  override def >(v2: Array[Double]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) > v2(i)
    rv
  }
  override def >(v2: Double)(implicit m: Manifest[Double]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) > v2
    rv
  }
  override def >=(v2: Array[Double]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v2(i)
    rv
  }
  override def >=(v2: Double)(implicit m: Manifest[Double]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v2
    rv
  }
  override def ===(v2: Array[Double]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) == v2(i)
    rv
  }
  override def ===(v2: Double)(implicit m: Manifest[Double]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) == v2
    rv
  }
  override def <|<(v1: Double, v2: Double)(implicit m: Manifest[Double]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v1 && v(i) <= v2
    rv
  }
  override def abs: Array[Double] = v map math.abs
  override def log: Array[Double] = v map math.log
  override def dff: Array[Double] = {
    val rv = new Array[Double](v.size - 1)
    for (i <- 1 until v.size) rv(i - 1) = v(i) - v(i - 1)
    rv
  }
  override def cumsum: Array[Double] = {
    val rv = new Array[Double](v.size)
    for (i <- 1 until v.size) rv(i) = v(i) + rv(i - 1)
    rv
  }

  /**
   * summary trait
   */
  override def sum: Double = {
    var sum = 0.0
    for (i <- 0 until v.size) sum += v(i)
    sum
  }

  override def mean: Double = sum / v.size.toDouble

  override lazy val mntnc: Boolean = {
    var (m, i) = (true, 1)
    while (m && i < v.size)
      if (v(i - 1) > v(i)) m = false
      else i += 1
    m
  }

  /**
   * query trait
   */
  override def dstnct: Array[Double] =
    if (v.size > 0) {
      val (dst, all) = (ArrayBuffer(v(0)), HashSet(v(0)))
      var lst = v(0)
      for (i <- 1 until v.size) if (v(i) != lst && !all.contains(v(i))) { dst += v(i); lst = v(i); all += lst }
      dst.toArray
    } else new Array[Double](0)

  override def apply(foo: (Double) => Boolean): Array[Boolean] = v map foo

  override def fnd(o: Array[Double]): Array[Int] = o map (Arrays.binarySearch(v, _))
  override def lte(o: Array[Double]): Array[Int] = o map { s =>
    val i = Arrays.binarySearch(v, s)
    if (i < 0) {
      val ip = -i - 1
      if (ip == v.size) ip
      else ip - 1
    } else i
  }

  override def srch(v1: Double, v2: Double): (Int, Int) = {
    import java.util.Arrays.binarySearch
    var (i0, i1) = (binarySearch(v, v1), binarySearch(v, v2))
    if (i0 < 0) i0 = -i0 - 1
    if (i1 < 0) i1 = -i1 - 1
    i0 = math.min(i0, v.size - 1)
    i1 = math.min(i1, v.size - 1)
    (i0, i1)
  }
  //  override def ssmpl(n: Int): Array[Double] = {
  //    if (v.size > n) {
  //      val rv = new Array[Double](n)
  //      rv(0) = v(0)
  //      rv(n - 1) = v.last
  //      val d = v.size.toDouble / n.toDouble
  //      (1 until rv.size) foreach { i => rv(i) = v(math.round(i.toDouble * d).toInt) }
  //      rv
  //    } else v
  //  }

}
