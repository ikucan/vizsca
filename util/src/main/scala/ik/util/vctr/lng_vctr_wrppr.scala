package ik.util.vctr

import java.util.Arrays

import scala.Array.canBuildFrom
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet

class lng_vctr_wrppr(v: Array[Long]) extends vctr[Long](v) with vctr_arthmtc[Long] with vctr_cmprsn[Long] with vctr_ops[Long] with vctr_stats[Long] with qry[Long] {

  override def apply(idx: Array[Boolean]): Array[Long] = {
    chk(idx)
    var ss = new ArrayBuffer[Long](v.size)
    for (i <- 0 until idx.size) if (idx(i)) ss += v(i)
    ss toArray

  }
  override def apply(idx: Array[Int]): Array[Long] = idx map v
  override def apply(idx: Array[Int], dflt: Long): Array[Long] = idx map { i => if (i >= 0 && i < v.size) v(i) else dflt }

  /**
   * basic algebra
   */
  override def +(v2: Array[Long]): Array[Long] = {
    chk(v2)
    val rv = new Array[Long](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) + v2(i)
    rv
  }

  override def +(v2: Long)(implicit m: Manifest[Long]): Array[Long] = {
    val rv = new Array[Long](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) + v2
    rv
  }

  override def -(v2: Array[Long]): Array[Long] = {
    chk(v2)
    val rv = new Array[Long](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) - v2(i)
    rv
  }

  override def -(v2: Long)(implicit m: Manifest[Long]): Array[Long] = {
    val rv = new Array[Long](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) - v2
    rv
  }

  override def *(v2: Array[Long]): Array[Long] = {
    chk(v2)
    val rv = new Array[Long](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) * v2(i)
    rv
  }

  override def *(v2: Long)(implicit m: Manifest[Long]): Array[Long] = {
    val rv = new Array[Long](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) * v2
    rv
  }

  override def /(v2: Array[Long]): Array[Long] = {
    chk(v2)
    val rv = new Array[Long](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) / v2(i)
    rv
  }

  override def /(v2: Long)(implicit m: Manifest[Long]): Array[Long] = {
    val rv = new Array[Long](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) / v2
    rv
  }

  /**
   * [in] equality
   */
  override def <(v2: Array[Long]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) < v2(i)
    rv
  }
  override def <(v2: Long)(implicit m: Manifest[Long]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) < v2
    rv
  }
  override def <=(v2: Array[Long]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) <= v2(i)
    rv
  }
  override def <=(v2: Long)(implicit m: Manifest[Long]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) <= v2
    rv
  }
  override def >(v2: Array[Long]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) > v2(i)
    rv
  }
  override def >(v2: Long)(implicit m: Manifest[Long]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) > v2
    rv
  }
  override def >=(v2: Array[Long]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v2(i)
    rv
  }
  override def >=(v2: Long)(implicit m: Manifest[Long]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v2
    rv
  }
  override def ===(v2: Array[Long]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) == v2(i)
    rv
  }
  override def ===(v2: Long)(implicit m: Manifest[Long]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) == v2
    rv
  }

  override def <|<(v1: Long, v2: Long)(implicit m: Manifest[Long]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) >= v1 && v(i) <= v2
    rv
  }

  override def abs: Array[Long] = v map math.abs
  override def log: Array[Long] = v map (x => math.log(x.toDouble).toLong)
  override def dff: Array[Long] = {
    val rv = new Array[Long](v.size - 1)
    for (i <- 1 until v.size) rv(i - 1) = v(i) - v(i - 1)
    rv
  }
  override def cumsum: Array[Long] = {
    val rv = new Array[Long](v.size)
    for (i <- 1 until v.size) rv(i) = v(i) + rv(i - 1)
    rv
  }

  override def sum: Long = {
    var sum = 0l
    for (i <- 0 until v.size) sum += v(i)
    sum
  }

  override def is_nan: Array[Boolean] = throw new vct_err("is NaN test not supported for the Long type.")

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
    } else new Array[Long](0)

  override def apply(foo: (Long) => Boolean): Array[Boolean] = v map foo

  override def fnd(o: Array[Long]): Array[Int] = o map (Arrays.binarySearch(v, _))
  override def lte(o: Array[Long]): Array[Int] = o map { s =>
    val i = Arrays.binarySearch(v, s)
    if (i < 0) {
      val ip = -i - 1
      if (ip == v.size) ip
      else ip - 1
    } else i
  }

  override def srch(v1: Long, v2: Long): (Int, Int) = throw new vct_err("search operation not supported")
  //  override def ssmpl(n: Int): Array[Long] =
  //    if (v.size > n) {
  //      val rv = new Array[Long](n)
  //      rv(0) = v(0)
  //      rv(n - 1) = v.last
  //      val d = v.size.toDouble / n.toDouble
  //      (1 until rv.size) foreach { i => rv(i) = v(math.round(i.toDouble * d).toInt) }
  //      rv
  //    } else v

}
