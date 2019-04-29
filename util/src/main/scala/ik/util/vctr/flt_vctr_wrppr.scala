package ik.util.vctr

import java.util.Arrays
import java.util.Arrays.binarySearch

import scala.Array.canBuildFrom
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet

class flt_vctr_wrppr(v: Array[Float]) extends vctr[Float](v) with vctr_arthmtc[Float] with vctr_cmprsn[Float] with vctr_ops[Float] with vctr_stats[Float] with qry[Float] {
  override def apply(idx: Array[Boolean]): Array[Float] = {
    chk(idx)
    var ss = new ArrayBuffer[Float](v.size)
    for (i <- 0 until idx.size) if (idx(i)) ss += v(i)
    ss toArray
  }

  override def apply(idx: Array[Int]): Array[Float] = idx map v
  override def apply(idx: Array[Int], dflt: Float): Array[Float] = idx map { i => if (i >= 0 && i < v.size) v(i) else dflt }

  /**
   * basic algebra
   */
  override def +(v2: Array[Float]) = {
    chk(v2)
    val rv = new Array[Float](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) + v2(i)
    rv
  }
  override def +(v2: Float)(implicit m: Manifest[Float]) = {
    val rv = new Array[Float](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) + v2
    rv
  }

  override def -(v2: Array[Float]): Array[Float] = {
    chk(v2)
    val rv = new Array[Float](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) - v2(i)
    rv
  }

  override def -(v2: Float)(implicit m: Manifest[Float]) = {
    val rv = new Array[Float](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) - v2
    rv
  }

  override def *(v2: Array[Float]): Array[Float] = {
    chk(v2)
    val rv = new Array[Float](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) * v2(i)
    rv
  }

  override def *(v2: Float)(implicit m: Manifest[Float]) = {
    val rv = new Array[Float](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) * v2
    rv
  }

  override def /(v2: Array[Float]): Array[Float] = {
    chk(v2)
    val rv = new Array[Float](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) / v2(i)
    rv
  }

  override def /(v2: Float)(implicit m: Manifest[Float]) = {
    val rv = new Array[Float](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) / v2
    rv
  }

  /**
   * [in] equality
   */
  override def <(v2: Array[Float]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) < v2(i)
    rv
  }
  override def <(v2: Float)(implicit m: Manifest[Float]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) < v2
    rv
  }
  override def <=(v2: Array[Float]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) <= v2(i)
    rv
  }
  override def <=(v2: Float)(implicit m: Manifest[Float]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) <= v2
    rv
  }
  override def >(v2: Array[Float]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) > v2(i)
    rv
  }
  override def >(v2: Float)(implicit m: Manifest[Float]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) > v2
    rv
  }
  override def >=(v2: Array[Float]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v2(i)
    rv
  }
  override def >=(v2: Float)(implicit m: Manifest[Float]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v2
    rv
  }
  override def ===(v2: Array[Float]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) == v2(i)
    rv
  }
  override def ===(v2: Float)(implicit m: Manifest[Float]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) == v2
    rv
  }
  override def <|<(v1: Float, v2: Float)(implicit m: Manifest[Float]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v1 && v(i) <= v2
    rv
  }

  override def abs: Array[Float] = v map math.abs
  override def log: Array[Float] = v map (x => math.log(x.toDouble).toFloat)
  override def dff: Array[Float] = {
    val rv = new Array[Float](v.size - 1)
    for (i <- 1 until v.size) rv(i - 1) = v(i) - v(i - 1)
    rv
  }
  override def cumsum: Array[Float] = {
    val rv = new Array[Float](v.size)
    for (i <- 1 until v.size) rv(i) = v(i) + rv(i - 1)
    rv
  }
  /**
   * summary trait
   */
  override def sum: Float = {
    var sum = 0f
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
  override def dstnct: Array[Float] =
    if (v.size > 0) {
      val (dst, all) = (ArrayBuffer(v(0)), HashSet(v(0)))
      var lst = v(0)
      for (i <- 1 until v.size) if (v(i) != lst && !all.contains(v(i))) { dst += v(i); lst = v(i); all += lst }
      dst.toArray
    } else new Array[Float](0)

  override def apply(foo: (Float) => Boolean): Array[Boolean] = v map foo

  override def fnd(o: Array[Float]): Array[Int] = o map (Arrays.binarySearch(v, _))
  override def lte(o: Array[Float]): Array[Int] = o map { s =>
    val i = Arrays.binarySearch(v, s)
    if (i < 0) {
      val ip = -i - 1
      if (ip == v.size) ip
      else ip - 1
    } else i
  }

  override def srch(v1: Float, v2: Float): (Int, Int) = {
    import java.util.Arrays.binarySearch
    var (i0, i1) = (binarySearch(v, v1), binarySearch(v, v2))
    if (i0 < 0) i0 = -i0 - 1
    if (i1 < 0) i1 = -i1 - 1
    i0 = math.min(i0, v.size - 1)
    i1 = math.min(i1, v.size - 1)
    (i0, i1)
  }
//  override def ssmpl(n: Int): Array[Float] =
//    if (v.size > n) {
//      val rv = new Array[Float](n)
//      rv(0) = v(0)
//      rv(n - 1) = v.last
//      val d = v.size.toDouble / n.toDouble
//      (1 until rv.size) foreach { i => rv(i) = v(math.round(i.toDouble * d).toInt) }
//      rv
//    } else v

}
