package ik.util.vctr

import scala.Array.canBuildFrom
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet

class num_vctr_wrppr[T: Numeric](v: Array[T])(implicit m: Manifest[T], n: Numeric[T], o: Ordering[T]) extends vctr[T](v) with vctr_arthmtc[T] with vctr_cmprsn[T] with qry[T] {

  override def apply(idx: Array[Boolean]): Array[T] = {
    chk(idx)
    var ss = new ArrayBuffer[T](v.size)
    for (i <- 0 until idx.size) if (idx(i)) ss += v(i)
    ss toArray
  }
  override def apply(idx: Array[Int]): Array[T] = idx map v
  override def apply(idx: Array[Int], dflt: T): Array[T] = idx map { i => if (i >= 0 && i < v.size) v(i) else dflt }

  override def +(v2: Array[T]): Array[T] = {
    chk(v2)
    val rv = m.newArray(v.size)
    for (i <- 0 until v.size) rv(i) = n.plus(v(i), v2(i))
    rv
  }
  override def +(v2: T)(implicit m: Manifest[T]): Array[T] = {
    val rv = m.newArray(v.size)
    for (i <- 0 until v.size) rv(i) = n.plus(v(i), v2)
    rv
  }
  override def -(v2: Array[T]): Array[T] = {
    chk(v2)
    val rv = m.newArray(v.size)
    for (i <- 0 until v.size) rv(i) = n.minus(v(i), v2(i))
    rv
  }
  override def -(v2: T)(implicit m: Manifest[T]): Array[T] = {
    val rv = m.newArray(v.size)
    for (i <- 0 until v.size) rv(i) = n.minus(v(i), v2)
    rv
  }
  override def *(v2: Array[T]): Array[T] = {
    chk(v2)
    val rv = m.newArray(v.size)
    for (i <- 0 until v.size) rv(i) = n.times(v(i), v2(i))
    rv
  }
  override def *(v2: T)(implicit m: Manifest[T]): Array[T] = {
    val rv = m.newArray(v.size)
    for (i <- 0 until v.size) rv(i) = n.times(v(i), v2)
    rv
  }
  override def /(v2: Array[T]): Array[T] = throw new vct_err("division operation not supported")
  override def /(v2: T)(implicit m: Manifest[T]): Array[T] = throw new vct_err("division operation not supported")

  override def <(v2: Array[T]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = o.lt(v(i), v2(i))
    rv
  }
  override def <(v2: T)(implicit m: Manifest[T]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = o.lt(v(i), v2)
    rv
  }
  override def <=(v2: Array[T]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = o.lteq(v(i), v2(i))
    rv
  }
  override def <=(v2: T)(implicit m: Manifest[T]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = o.lteq(v(i), v2)
    rv
  }
  override def >(v2: Array[T]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = o.gt(v(i), v2(i))
    rv
  }
  override def >(v2: T)(implicit m: Manifest[T]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = o.gt(v(i), v2)
    rv
  }
  override def >=(v2: Array[T]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = o.gteq(v(i), v2(i))
    rv
  }
  override def >=(v2: T)(implicit m: Manifest[T]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = o.gteq(v(i), v2)
    rv
  }
  override def ===(v2: Array[T]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = o.equiv(v(i), v2(i))
    rv
  }
  override def ===(v2: T)(implicit m: Manifest[T]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = o.equiv(v(i), v2)
    rv
  }
  override def <|<(v1: T, v2: T)(implicit m: Manifest[T]): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = o.gteq(v(i), v1) && o.gteq(v(i), v2)
    rv
  }

  /**
   * query trait
   */
  override def dstnct: Array[T] =
    if (v.size > 0) {
      val (dst, all) = (ArrayBuffer(v(0)), HashSet[T](v(0)))
      var lst = v(0)
      for (i <- 1 until v.size) if (v(i) != lst && !all.contains(v(i))) { dst += v(i); lst = v(i); all += lst }
      dst.toArray
    } else new Array[T](0)

  override def apply(foo: (T) => Boolean): Array[Boolean] = v map foo

  override def fnd(o: Array[T]): Array[Int] = throw new vct_err("find operation not supported on generic vectors")
  override def lte(o: Array[T]): Array[Int] = throw new vct_err("lte operation not supported on generic vectors")

  override def srch(v1: T, v2: T): (Int, Int) = throw new vct_err("search operation not supported")
//  override def ssmpl(n: Int): Array[T] =
//    if (v.size > n) {
//      val rv = new Array[T](n)
//      rv(0) = v(0)
//      rv(n - 1) = v.last
//      val d = v.size.toDouble / n.toDouble
//      (1 until rv.size) foreach { i => rv(i) = v(math.round(i.toDouble * d).toInt) }
//      rv
//    } else v

}
