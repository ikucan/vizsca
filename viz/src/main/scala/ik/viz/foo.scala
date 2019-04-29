package ik.viz

import scala.collection.mutable.ListBuffer

import ik.util.vctr.mk_vctr_wrppr

/**
 *  encapsulation of some function to be plotted
 */
trait foo {
  def x: Array[Double] // x
  def y: Array[Double] // y
  //val x_min, x_max, y_min, y_max: Double // domain range
  def x_min: Double // domain range
  def x_max: Double // domain range
  def y_min: Double // domain range
  def y_max: Double // domain range

  /**
   * select function values between
   */
  def slct(x0: Double = Double.MinValue, x1: Double = Double.MaxValue, N: Int = -1): (Array[Double], Array[Double])
  /**
   * compare two functions to see if they operate over precisely equal domains
   */
  def eql_dmn(othr: foo): Boolean
  /**
   * compare two functions to see if they deinfe precisely equal ranges
   */
  def eql_rng(othr: foo): Boolean
  /**
   * callback
   */
  var cbck: (Unit) => Unit = (Unit) => Unit
}

object foo {
  def co_slct(f1: foo, f2: foo, x0: Double = Double.MinValue, x1: Double = Double.MaxValue, y0: Double = Double.MinValue, y1: Double = Double.MaxValue, N: Int = -1): ((Array[Double], Array[Double]), (Array[Double], Array[Double])) = {
    val (xx1, yy1, xx2, yy2) = (f1.x, f1.y, f2.x, f2.y)
    if (xx1.size != xx2.size) throw new p_err("functions must be of same size.")
    // first filter all points in the area
    val f1_in = (0 until xx1.size) map (i => xx1(i) >= x0 && xx1(i) < x1 && yy1(i) > y0 && yy1(i) < y1) toArray
    val f2_in = (0 until xx2.size) map (i => xx2(i) >= x0 && xx2(i) < x1 && yy2(i) > y0 && yy2(i) < y1) toArray
    val one_in = f1_in || f2_in
    //val both_in = f1_in && f2_in
    // first select some data

    //((xx1(f1_in), yy1(f1_in)), (xx2(f1_in), yy2(f1_in)))
    ((xx1(one_in), yy1(one_in)), (xx2(one_in), yy2(one_in)))
  }

  def apply(x: Array[Double], y: Array[Double]) = new dscrt_foo(x, y)
  def dyn() = new dyn_foo(Array[Double](), Array[Double]())
  def dyn(x: Array[Double], y: Array[Double]) = new dyn_foo(x, y)
}

/**
 * encapsulation of a discrete funcion
 */
class dscrt_foo(override val x: Array[Double], override val y: Array[Double]) extends foo {
  if (x.size != y.size) throw new p_err("domain and range must have same number of values.(x has " + x.size + " and y has " + y.size)

  override val (x_min, x_max, y_min, y_max) = if (x.size == 0) (Double.NaN, Double.NaN, Double.NaN, Double.NaN) else (x.min, x.max, y.min, y.max)

  override def slct(x0: Double, x1: Double, N: Int = -1) = {

    // only sensible if the function has some elements
    if (x.size > 0) {
      //find all values within the range
      val idx = x >= x0 && x <= x1
      //println(">> " + idx.size)
      // prepend one point before interval and one point after the interval
      var (frst, lst, i) = (0, 0, 0)
      while (!idx(i) && i < idx.size - 1) i += 1
      if (i > 0) idx(i - 1) = true
      i = idx.size - 1
      while (i >= 0 && !idx(i)) i -= 1
      if (i < idx.size - 1) idx(i + 1) = true
 
      if (N > 0) (x(idx).ssmpl(N), y(idx).ssmpl(N))
      else (x(idx), y(idx))
    } else (x, y)
  }

  override def eql_dmn(othr: foo): Boolean = {
    if (othr.x.size != x.size) false
    else (true /: (0 until x.size))((ok, i) => ok && x(i) == othr.x(i))
  }
  override def eql_rng(othr: foo): Boolean = {
    if (othr.x.size != x.size) false
    else (true /: (0 until x.size))((ok, i) => ok && y(i) == othr.y(i))
  }
}

class vctr_foo(override val x: Array[Double]) extends foo {
  override lazy val y = Array[Double]()
  override val (x_min, x_max, y_min, y_max) = (if (x.isEmpty) Double.NaN else x.min, if (x.isEmpty) Double.NaN else x.max, Double.NaN, Double.NaN)

  override def slct(x0: Double, x1: Double, N: Int = -1) = {
    val idx = x >= x0 && x <= x1
    //println("index size:>> " + idx.size)
    if (N > 0) (x(idx).ssmpl(N), y)
    else (x(idx), y)
  }

  override def eql_dmn(othr: foo): Boolean = {
    if (othr.x.size != x.size) false
    else (true /: (0 until x.size))((ok, i) => ok && x(i) == othr.x(i))
  }

  override def eql_rng(othr: foo): Boolean = true
}

/**
 * encapsulation of a dynamic funcion
 */
class dyn_foo(xs: Array[Double], ys: Array[Double], var mx: Int = 0) extends foo {
  private var xx = (new ListBuffer[Double] ++ xs)
  private var yy = (new ListBuffer[Double] ++ ys)

  if (xx.size != yy.size) throw new p_err("domain and range must have same number of values.")

  override def x = xx.toArray
  override def y = yy.toArray

  override def x_min = xmn
  override def x_max = xmx
  override def y_min = ymn
  override def y_max = ymx

  def clr = { xx.clear; yy.clear; tch }

  def +=(x: Double, y: Double) = { xx += x; yy += y; if (mx > 0 && mx < xx.size) this.unary_-; tch }
  def ++=(x: Array[Double], y: Array[Double]) = { xx ++= x; yy ++= y; while (mx > 0 && mx < xx.size) this.unary_-; tch }

  def unary_- = { xx.remove(0); yy.remove(0) }

  def fltr_x(ffoo: (Double) => Boolean) = fltr_xx_yy((i: Int) => ffoo(xx(i)))
  def fltr_y(ffoo: (Double) => Boolean) = fltr_xx_yy((i: Int) => ffoo(yy(i)))

  private def fltr_xx_yy(ffoo: (Int) => Boolean) = {
    val (nxx, nyy) = (new ListBuffer[Double], new ListBuffer[Double])
    for (i <- 0 until xx.size) if (ffoo(i)) { nxx += xx(i); nyy += yy(i) }
    xx = nxx; yy = nyy
    tch
  }

  def coalesce_x(ffoo: (Double) => Boolean, dflt: Double) = {
    for (i <- 0 until xx.size) if (ffoo(xx(i))) xx(i) = dflt
    tch
  }

  private var (xmn, xmx, ymn, ymx) = (Double.NaN, Double.NaN, Double.NaN, Double.NaN)

  tch

  private def tch = if (xx.size > 0) { xmn = x.min; xmx = x.max; ymn = y.min; ymx = y.max; cbck(Unit) }

  override def slct(x0: Double, x1: Double, N: Int = -1) = {
    val idx = x >= x0 && x <= x1
    if (N > 0) (x(idx).ssmpl(N), y(idx).ssmpl(N))
    else (x(idx), y(idx))
  }

  override def eql_dmn(othr: foo): Boolean = {
    if (othr.x.size != x.size) false
    else (true /: (0 until x.size))((ok, i) => ok && x(i) == othr.x(i))
  }

  override def eql_rng(othr: foo): Boolean = {
    if (othr.x.size != x.size) false
    else (true /: (0 until x.size))((ok, i) => ok && y(i) == othr.y(i))
  }
}
