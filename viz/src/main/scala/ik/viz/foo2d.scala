package ik.viz

import ik.util.vctr._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

/**
 *  encapsulation of some function to be plotted
 */
trait foo2d {
  def x: Array[Double] // x
  def y: Array[Double] // y
  def z: Array[Array[Double]] // z
  //val x_min, x_max, y_min, y_max: Double // domain range
  def x_min: Double // domain range
  def x_max: Double // domain range
  def y_min: Double // domain range
  def y_max: Double // domain range
  def z_min: Double // domain range
  def z_max: Double // domain range

  /**
   * select function values between
   */
  def slct(x0: Double = Double.MinValue, x1: Double = Double.MaxValue, y0: Double = Double.MinValue, y1: Double = Double.MaxValue, N: Int = -1): (Array[Double], Array[Double], Array[Array[Double]])
  /**
   * compare two functions to see if they operate over precisely equal domains
   */
  def eql_dmn(othr: foo2d): Boolean
  /**
   * compare two functions to see if they deinfe precisely equal ranges
   */
  def eql_rng(othr: foo2d): Boolean
  /**
   * callback
   */
  var cbck: (Unit) => Unit = (Unit) => Unit
}

object foo2d {
  def apply(x: Array[Double], y: Array[Double], z: Array[Array[Double]]) = new dscrt_foo2d(x, y, z)
}

/**
 * encapsulation of a two dimensional discrete funcion
 */
class dscrt_foo2d(override val x: Array[Double], override val y: Array[Double], override val z: Array[Array[Double]]) extends foo2d {
  // if (x.size != y.size) throw new p_err("domain and range must have same number of values.")

  override val (x_min, x_max, y_min, y_max) = (x.min, x.max, y.min, y.max)
  override val (z_min, z_max) = {
    var (mn, mx) = (Double.MaxValue, Double.MinValue)
    z foreach { _ foreach { x => if (x < mn) mn = x else if (x > mx) mx = x } }
    (mn, mx)
  }

  override def slct(x0: Double, x1: Double, y0: Double, y1: Double, N: Int): (Array[Double], Array[Double], Array[Array[Double]]) = {
    val xidx = x >= x0 && x <= x1
    val yidx = y >= y0 && y <= y1
    //val x_subst = new gen_vctr_wrppr(z).apply(xidx)
    (x(xidx), y(yidx), new gen_vctr_wrppr(z).apply(xidx) map (_(yidx)))
    //    // no subsampling
    //    if (N > 0) (x(xidx).ssmpl(N), y(xidx).ssmpl(N))
    //    else (x(xidx), y(xidx))
  }

  override def eql_dmn(othr: foo2d): Boolean = false

  override def eql_rng(othr: foo2d): Boolean = false
}

class dscrt_foo2d_tle(val x_lbl: Array[String], val y_lbl: Array[String], override val z: Array[Array[Double]]) extends foo2d {

  def this(z: Array[Array[Double]]) = this((0 to z.size) map (_.toString) toArray, (0 to z(0).size) map (_.toString) toArray, z)
  // if (x.size != y.size) throw new p_err("domain and range must have same number of values.")
  override val x: Array[Double] = (0 to z.size) toArray
  override val y: Array[Double] = (0 to z(0).size) toArray

  override val (x_min, x_max, y_min, y_max) = (x.min, x.max, y.min, y.max)
  override val (z_min, z_max) = (z.flatMap(x => x).min, z.flatMap(x => x).max)

  override def slct(x0: Double, x1: Double, y0: Double, y1: Double, N: Int): (Array[Double], Array[Double], Array[Array[Double]]) = {
    val xidx = x >= x0 && x <= x1
    val yidx = y >= y0 && y <= y1
    //val x_subst = new gen_vctr_wrppr(z).apply(xidx)
    (x(xidx), y(yidx), z) //new gen_vctr_wrppr(z).apply(xidx) map (_(yidx)))
    //    // no subsampling
    //    if (N > 0) (x(xidx).ssmpl(N), y(xidx).ssmpl(N))
    //    else (x(xidx), y(xidx))
  }

  override def eql_dmn(othr: foo2d): Boolean = false

  override def eql_rng(othr: foo2d): Boolean = false
}
