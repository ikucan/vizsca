package ik.util.vctr

import scala.Array.canBuildFrom
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet

class gen_vctr_wrppr[T <: Any](v: Array[T])(implicit m: Manifest[T]) extends vctr[T](v) with qry[T] {

  override def apply(idx: Array[Boolean]): Array[T] = {
    chk(idx)
    var ss = new ArrayBuffer[T](v.size)
    for (i <- 0 until idx.size) if (idx(i)) ss += v(i)
    ss toArray
  }
  override def apply(idx: Array[Int]): Array[T] = idx map v
  override def apply(idx: Array[Int], dflt: T): Array[T] = idx map { i => if (i >= 0 && i < v.size) v(i) else dflt }

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
