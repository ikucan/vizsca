package ik.util.vctr

import scala.Array.canBuildFrom
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet

class sym_vctr_wrppr(v: Array[Symbol]) extends vctr[Symbol](v) with qry[Symbol] {
  override def apply(idx: Array[Boolean]): Array[Symbol] = {
    chk(idx)
    var ss = new ArrayBuffer[Symbol](v.size)
    for (i <- 0 until idx.size) if (idx(i)) ss += v(i)
    ss toArray

  }

  override def apply(idx: Array[Int]): Array[Symbol] = idx map v
  override def apply(idx: Array[Int], dflt: Symbol): Array[Symbol] = idx map { i => if (i >= 0 && i < v.size) v(i) else dflt }

  override def dstnct =
    if (v.size > 0) {
      val (dst, all) = (ArrayBuffer(v(0)), HashSet(v(0)))
      var lst = v(0)
      for (i <- 1 until v.size) if (v(i) != lst && !all.contains(v(i))) { dst += v(i); lst = v(i); all += lst }
      dst.toArray
    } else new Array[Symbol](0)

  override def apply(foo: (Symbol) => Boolean): Array[Boolean] = v map foo

  override def fnd(o: Array[Symbol]): Array[Int] = throw new vct_err("find operation not supported on Symbol vectors")
  override def lte(o: Array[Symbol]): Array[Int] = throw new vct_err("lte operation not supported on String vectors")

  override def srch(v1: Symbol, v2: Symbol): (Int, Int) = throw new vct_err("search operation not supported")

}
