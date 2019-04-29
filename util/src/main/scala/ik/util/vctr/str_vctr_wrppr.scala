package ik.util.vctr

import scala.Array.canBuildFrom
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet

class str_vctr_wrppr(v: Array[String]) extends vctr[String](v) with qry[String] {

  override def apply(idx: Array[Boolean]): Array[String] = {
    chk(idx)
    var ss = new ArrayBuffer[String](v.size)
    for (i <- 0 until idx.size) if (idx(i)) ss += v(i)
    ss toArray
  }
  override def apply(idx: Array[Int]): Array[String] = idx map v
  override def apply(idx: Array[Int], dflt: String): Array[String] = idx map { i => if (i >= 0 && i < v.size) v(i) else dflt }
  override def dstnct =
    if (v.size > 0) {
      val (dst, all) = (ArrayBuffer(v(0)), HashSet(v(0)))
      var lst = v(0)
      for (i <- 1 until v.size) if (v(i) != lst && !all.contains(v(i))) { dst += v(i); lst = v(i); all += lst }
      dst.toArray
    } else new Array[String](0)

  override def apply(foo: (String) => Boolean): Array[Boolean] = v map foo

  override def fnd(o: Array[String]): Array[Int] = throw new vct_err("find operation not supported on String vectors")
  override def lte(o: Array[String]): Array[Int] = throw new vct_err("lte operation not supported on String vectors")

  override def srch(v1: String, v2: String): (Int, Int) = throw new vct_err("search operation not supported")
//  override def ssmpl(n: Int): Array[String] =
//    if (v.size > n) {
//      val rv = new Array[String](n)
//      rv(0) = v(0)
//      rv(n - 1) = v.last
//      val d = v.size.toDouble / n.toDouble
//      (1 until rv.size) foreach { i => rv(i) = v(math.round(i.toDouble * d).toInt) }
//      rv
//    } else v

}
