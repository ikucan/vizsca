package ik.util.vctr

import java.util.Arrays

import scala.Array.canBuildFrom
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet

class int_vctr_wrppr(v: Array[Int]) extends vctr[Int](v) with vctr_arthmtc[Int] with vctr_cmprsn[Int] with vctr_ops[Int] with vctr_stats[Int] with qry[Int] {

  override def apply(idx: Array[Boolean]): Array[Int] = {
    chk(idx)
    var ss = new ArrayBuffer[Int](v.size)
    for (i <- 0 until idx.size) if (idx(i)) ss += v(i)
    ss toArray

  }
  override def apply(idx: Array[Int]): Array[Int] = idx map v
  override def apply(idx: Array[Int], dflt: Int): Array[Int] = idx map { i => if (i >= 0 && i < v.size) v(i) else dflt }
  /**
   * basic algebra
   */
  override def +(v2: Array[Int]): Array[Int] = {
    chk(v2)
    val rv = new Array[Int](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) + v2(i)
    rv
  }

  override def +(v2: Int)(implicit m: Manifest[Int]): Array[Int] = {
    val rv = new Array[Int](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) + v2
    rv
  }
  override def -(v2: Array[Int]): Array[Int] = {
    chk(v2)
    val rv = new Array[Int](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) - v2(i)
    rv
  }

  override def -(v2: Int)(implicit m: Manifest[Int]): Array[Int] = {
    val rv = new Array[Int](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) - v2
    rv
  }

  override def *(v2: Array[Int]): Array[Int] = {
    chk(v2)
    val rv = new Array[Int](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) * v2(i)
    rv
  }

  override def *(v2: Int)(implicit m: Manifest[Int]): Array[Int] = {
    val rv = new Array[Int](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) * v2
    rv
  }

  override def /(v2: Array[Int]): Array[Int] = {
    chk(v2)
    val rv = new Array[Int](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) / v2(i)
    rv
  }

  override def /(v2: Int)(implicit m: Manifest[Int]): Array[Int] = {
    val rv = new Array[Int](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) / v2
    rv
  }
  /**
   * [in] equality
   */

  override def <(v2: Array[Int]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) < v2(i)
    rv
  }
  override def <(v2: Int)(implicit m: Manifest[Int]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) < v2
    rv
  }
  override def <=(v2: Array[Int]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) <= v2(i)
    rv
  }
  override def <=(v2: Int)(implicit m: Manifest[Int]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) <= v2
    rv
  }
  override def >(v2: Array[Int]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) > v2(i)
    rv
  }
  override def >(v2: Int)(implicit m: Manifest[Int]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) > v2
    rv
  }
  override def >=(v2: Array[Int]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v2(i)
    rv
  }
  override def >=(v2: Int)(implicit m: Manifest[Int]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v2
    rv
  }
  override def ===(v2: Array[Int]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) == v2(i)
    rv
  }
  override def ===(v2: Int)(implicit m: Manifest[Int]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) == v2
    rv
  }
  override def <|<(v1: Int, v2: Int)(implicit m: Manifest[Int]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v1 && v(i) <= v2
    rv
  }

  /**
   * ops
   */
  override def abs: Array[Int] = v map math.abs
  override def log: Array[Int] = v map (x => math.log(x.toDouble).toInt)

  override def dff: Array[Int] = {
    val rv = new Array[Int](v.size - 1)
    for (i <- 1 until v.size) rv(i - 1) = v(i) - v(i - 1)
    rv
  }

  override def cumsum: Array[Int] = {
    val rv = new Array[Int](v.size)
    for (i <- 1 until v.size) rv(i) = v(i) + rv(i - 1)
    rv
  }

  /**
   * transformations
   */
  override def sum: Int = {
    var sum = 0
    for (i <- 0 until v.size) sum += v(i)
    sum
  }

  override def mean: Double = sum.toDouble / v.size.toDouble

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
  override def dstnct =
    if (v.size > 0) {
      val (dst, all) = (ArrayBuffer(v(0)), HashSet(v(0)))
      var lst = v(0)
      for (i <- 1 until v.size) if (v(i) != lst && !all.contains(v(i))) { dst += v(i); lst = v(i); all += lst }
      dst.toArray
    } else new Array[Int](0)

  override def apply(foo: (Int) => Boolean): Array[Boolean] = v map foo

  override def fnd(o: Array[Int]): Array[Int] = o map (Arrays.binarySearch(v, _))
  override def lte(o: Array[Int]): Array[Int] = o map { s =>
    val i = Arrays.binarySearch(v, s)
    if (i < 0) {
      val ip = -i - 1
      if (ip == v.size) ip
      else ip - 1
    } else i
  }

  override def srch(v1: Int, v2: Int): (Int, Int) = throw new vct_err("search operation not supported")
//  override def ssmpl(n: Int): Array[Int] =
//    if (v.size > n) {
//      val rv = new Array[Int](n)
//      rv(0) = v(0)
//      rv(n - 1) = v.last
//      val d = v.size.toDouble / n.toDouble
//      (1 until rv.size) foreach { i => rv(i) = v(math.round(i.toDouble * d).toInt) }
//      rv
//    } else v

}
