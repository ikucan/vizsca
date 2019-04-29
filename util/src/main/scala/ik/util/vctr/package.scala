package ik.util

/**
 * vector package object. inclusion initiates automatic array 2 vector type conversions
 */
package object vctr {

  implicit def mk_vctr_wrppr(v: Array[Int]) = new int_vctr_wrppr(v)
  implicit def mk_vctr_wrppr(v: Array[Double]) = new dbl_vctr_wrppr(v)
  implicit def mk_vctr_wrppr(v: Array[Float]) = new flt_vctr_wrppr(v)
  implicit def mk_vctr_wrppr(v: Array[Long]) = new lng_vctr_wrppr(v)
  implicit def mk_vctr_wrppr(v: Array[Symbol]) = new sym_vctr_wrppr(v)
  implicit def mk_vctr_wrppr(v: Array[String]) = new str_vctr_wrppr(v)
  implicit def mk_vctr_wrppr(v: Array[Boolean]) = new bool_vctr_wrppr(v)
  implicit def mk_vctr_wrppr[T](v: Array[T])(implicit m: Manifest[T], n: Numeric[T]) = new num_vctr_wrppr[T](v)

  implicit def int_vctr_2_dbl_vctr(v: Array[Int]) = v map (_.toDouble)
  implicit def int_vctr_2_lng_vctr(v: Array[Int]) = v map (_.toLong)
  implicit def lng_vctr_2_dbl_vctr(v: Array[Long]) = v map (_.toDouble)
  implicit def dbl_vctr_2_lng_vctr(v: Array[Double]) = v map (_.toLong)
}