package ik.util.df

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import ik.util.vctr.mk_vctr_wrppr
import ik.util.vctr.vctr_arthmtc

/**
 * write compoenent of persistence
 */
trait frm_ops {
  f1: frm =>

  def +(f2: frm): frm = {
    val (cs1, ts1, cs2, ts2) = (f1.col_nms, f1.typs, f2.col_nms, f2.typs)
    val res = new frm()
    Await.result(Future { sym_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { str_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { bol_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { chr_cols.par map { a => (a._1, a._2 + f2.get_arg[Char](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { byt_cols.par map { a => (a._1, a._2 + f2.get_arg[Byte](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { sht_cols.par map { a => (a._1, a._2 + f2.get_arg[Short](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { int_cols.par map { a => (a._1, a._2 + f2.get_arg[Int](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { lng_cols.par map { a => (a._1, a._2 + f2.get_arg[Long](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { flt_cols.par map { a => (a._1, a._2 + f2.get_arg[Float](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { dbl_cols.par map { a => (a._1, a._2 + f2.get_arg[Double](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    res
  }
  def +(v2: Double): frm = {
    val res = new frm()
    Await.result(Future { sym_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { str_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { bol_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { chr_cols.par map { a => (a._1, a._2 + v2.toChar) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { byt_cols.par map { a => (a._1, a._2 + v2.toByte) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { sht_cols.par map { a => (a._1, a._2 + v2.toShort) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { int_cols.par map { a => (a._1, a._2 + v2.toInt) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { lng_cols.par map { a => (a._1, a._2 + v2.toLong) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { flt_cols.par map { a => (a._1, a._2 + v2.toFloat) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { dbl_cols.par map { a => (a._1, a._2 + v2.toDouble) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    res
  }
  def -(f2: frm): frm = {
    val (cs1, ts1, cs2, ts2) = (f1.col_nms, f1.typs, f2.col_nms, f2.typs)
    val res = new frm()
    Await.result(Future { sym_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { str_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { bol_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { chr_cols.par map { a => (a._1, a._2 - f2.get_arg[Char](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { byt_cols.par map { a => (a._1, a._2 - f2.get_arg[Byte](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { sht_cols.par map { a => (a._1, a._2 - f2.get_arg[Short](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { int_cols.par map { a => (a._1, a._2 - f2.get_arg[Int](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { lng_cols.par map { a => (a._1, a._2 - f2.get_arg[Long](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { flt_cols.par map { a => (a._1, a._2 - f2.get_arg[Float](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { dbl_cols.par map { a => (a._1, a._2 - f2.get_arg[Double](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    res
  }
  def -(v2: Double): frm = {
    val res = new frm()
    Await.result(Future { sym_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { str_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { bol_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { chr_cols.par map { a => (a._1, a._2 - v2.toChar) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { byt_cols.par map { a => (a._1, a._2 - v2.toByte) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { sht_cols.par map { a => (a._1, a._2 - v2.toShort) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { int_cols.par map { a => (a._1, a._2 - v2.toInt) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { lng_cols.par map { a => (a._1, a._2 - v2.toLong) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { flt_cols.par map { a => (a._1, a._2 - v2.toFloat) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { dbl_cols.par map { a => (a._1, a._2 - v2.toDouble) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    res
  }
  def *(f2: frm): frm = {
    val (cs1, ts1, cs2, ts2) = (f1.col_nms, f1.typs, f2.col_nms, f2.typs)
    val res = new frm()
    Await.result(Future { sym_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { str_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { bol_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { chr_cols.par map { a => (a._1, a._2 * f2.get_arg[Char](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { byt_cols.par map { a => (a._1, a._2 * f2.get_arg[Byte](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { sht_cols.par map { a => (a._1, a._2 * f2.get_arg[Short](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { int_cols.par map { a => (a._1, a._2 * f2.get_arg[Int](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { lng_cols.par map { a => (a._1, a._2 * f2.get_arg[Long](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { flt_cols.par map { a => (a._1, a._2 * f2.get_arg[Float](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { dbl_cols.par map { a => (a._1, a._2 * f2.get_arg[Double](a._1)) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    res
  }
  def *(v2: Double): frm = {
    val res = new frm()
    Await.result(Future { sym_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { str_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { bol_cols.par map { a => (a._1, a._2) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { chr_cols.par map { a => (a._1, a._2 * v2.toChar) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { byt_cols.par map { a => (a._1, a._2 * v2.toByte) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { sht_cols.par map { a => (a._1, a._2 * v2.toShort) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { int_cols.par map { a => (a._1, a._2 * v2.toInt) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { lng_cols.par map { a => (a._1, a._2 * v2.toLong) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { flt_cols.par map { a => (a._1, a._2 * v2.toFloat) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    Await.result(Future { dbl_cols.par map { a => (a._1, a._2 * v2.toDouble) } seq }, 1.hour) foreach { x => res.set_arg((x._1, x._2)) }
    res
  }

  def /(v2: Double): frm = *(1.0 / v2)
}
