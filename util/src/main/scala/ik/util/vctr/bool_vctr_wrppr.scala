package ik.util.vctr

import scala.Array.canBuildFrom
import scala.collection.mutable.ArrayBuffer

class bool_vctr_wrppr(v: Array[Boolean]) extends vctr[Boolean](v) with lgc[Boolean] {
  override def apply(idx: Array[Boolean]): Array[Boolean] = {
    chk(idx)
    var ss = new ArrayBuffer[Boolean](v.size)
    for (i <- 0 until idx.size) if (idx(i)) ss += v(i)
    ss toArray
  }

  override def apply(idx: Array[Int]): Array[Boolean] = idx map v
  override def apply(idx: Array[Int], dflt: Boolean): Array[Boolean] = idx map { i => if (i >= 0 && i < v.size) v(i) else dflt }

  override def unary_!(): Array[Boolean] = {
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = !v(i)
    rv
  }
  
  override def &&(v2: Array[Boolean]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) && v2(i)
    rv
  }
  override def ||(v2: Array[Boolean]): Array[Boolean] = {
    chk(v2)
    val rv = new Array[Boolean](v.size)
    for (i <- 0 until v.size) rv(i) = v(i) || v2(i)
    rv
  }
}
