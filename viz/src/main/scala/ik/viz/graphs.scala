package ik.viz

import java.awt.Font

import scala.language.postfixOps
import scala.collection.mutable.HashSet
import scala.math.max
import scala.math.min

import ik.util.num.rng_lmt
import ik.util.ui.clr
import ik.util.vctr._

import processing.core.PConstants
import processing.core.PFont

//import ik.util.mcro.hlpr.dmp

/**
 * a plot
 */
trait plot {
  def bnds: (Double, Double, Double, Double)
  def zoom(x0: Double, x1: Double, y0: Double, y1: Double)
  def clck(x0: Double, x1: Double) = {}
  def x_tcks(n: Int): Array[Double]
  def y_tcks(n: Int): Array[Double]
  var chngd: () => (Unit) = () => Unit
}

/**
 * line plot. vanilla x-y plot
 */
class line_grph(sktch: p_sktch, val f: foo, lt: ln_typ, c_strk: clr, c_fll: clr, sz: Float) extends p_area(sktch) with plot {
  var xy = f.slct(N = 5000)
  var (dx, dy, plt_xoff, plt_yoff) = (.0, .0, .0, .0)

  override def bnds = (f.x_min, f.x_max, f.y_min, f.y_max)
  override def zoom(x0: Double, x1: Double, y0: Double, y1: Double) = {
    xy = f.slct(x0 = x0, x1 = x1, N = 5000)
    dx = w().toDouble / (x1 - x0); dy = h().toDouble / (y1 - y0)
    plt_xoff = x0; plt_yoff = y0
  }
  override def x_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;
  override def y_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;

  override def draw_impl = if (visible || D) {
    val bx = box
    if (visible) {
      p.stroke(c_strk.c)
      p.fill(c_strk.c)
      p.strokeWeight(sz)
      val (xs, ys) = (xy._1, xy._2)
      (1 until xs.size) foreach { i =>
        val (xx1, yy1) = (((xs(i - 1) - plt_xoff) * dx).toFloat + xoff(), h() + yoff() - ((ys(i - 1) - plt_yoff) * dy).toFloat)
        val (xx2, yy2) = (((xs(i) - plt_xoff) * dx).toFloat + xoff(), h() + yoff() - ((ys(i) - plt_yoff) * dy).toFloat)
        lt.drw(this, xx1, yy1, xx2, yy2, sz) // horizontal component
      }
      p.strokeWeight(1)
    }
    if (D) {
      p.textAlign(PConstants.LEFT)
      p.fill(0)
      p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
      p.text("line graph in debug mode", bx._1 + 50, bx._2 + 20)
      p.stroke(0x33, 0x55, 0x99)
      p.line(bx._1, bx._2, bx._3, bx._4)
      p.line(bx._1, bx._4, bx._3, bx._2)
    }
  }
}

/**
 * the tick line plot. draw a line in tick "style"
 */
class tick_grph(sktch: p_sktch, val f: foo, lt: ln_typ, c_strk: clr, c_fll: clr, sz: Float) extends p_area(sktch) with plot {
  var xy = f.slct(N = 5000)
  var (dx, dy, plt_xoff, plt_yoff) = (.0, .0, .0, .0)

  override def bnds = (f.x_min, f.x_max, f.y_min, f.y_max)
  override def zoom(x0: Double, x1: Double, y0: Double, y1: Double) = {
    xy = f.slct(x0 = x0, x1 = x1, N = 5000)
    dx = w().toDouble / (x1 - x0); dy = h().toDouble / (y1 - y0)
    plt_xoff = x0; plt_yoff = y0
  }
  override def x_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;
  override def y_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;

  override def draw_impl = if (visible || D) {
    val bx = box
    if (visible) {
      p.stroke(c_strk.c)
      p.fill(c_strk.c)
      p.strokeWeight(sz)
      val (xs, ys) = (xy._1, xy._2)
      (1 until xs.size) foreach { i =>
        /**
         * split the line between p1, p2 into two orthogonal lines.
         * i.e. compute an intermediate point: (x2, y1)
         */
        val (xx1, yy1) = (((xs(i - 1) - plt_xoff) * dx).toFloat + xoff(), h() + yoff() - ((ys(i - 1) - plt_yoff) * dy).toFloat)
        val (xx2, yy2) = (((xs(i) - plt_xoff) * dx).toFloat + xoff(), h() + yoff() - ((ys(i) - plt_yoff) * dy).toFloat)
        lt.drw(this, xx1, yy1, xx2, yy1, sz) // horizontal component
        lt.drw(this, xx2, yy1, xx2, yy2, sz) // vertical component

      }
      p.strokeWeight(1)
    }
    if (D) {
      p.textAlign(PConstants.LEFT)
      p.fill(0)
      p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
      p.text("tick graph in debug mode", bx._1 + 50, bx._2 + 20)
      p.stroke(0x33, 0x55, 0x99)
      p.line(bx._1, bx._2, bx._3, bx._4)
      p.line(bx._1, bx._4, bx._3, bx._2)
    }
  }
}

/**
 * TODO:>> this should replace the tick plot above....
 * need to add the three new features : vertical component first, options on whether
 * to actually draw the horizontal and vertical components respectively
 */
class mnnhtn(sktch: p_sktch, val f: foo, lt: ln_typ, c_strk: clr, c_fll: clr, sz: Float, v_frst: Boolean, h_drw: Boolean, v_drw: Boolean) extends p_area(sktch) with plot {
  var xy = f.slct(N = 5000)
  var (dx, dy, plt_xoff, plt_yoff) = (.0, .0, .0, .0)

  override def bnds = (f.x_min, f.x_max, f.y_min, f.y_max)
  override def zoom(x0: Double, x1: Double, y0: Double, y1: Double) = {
    xy = f.slct(x0 = x0, x1 = x1, N = 5000)
    dx = w().toDouble / (x1 - x0); dy = h().toDouble / (y1 - y0)
    plt_xoff = x0; plt_yoff = y0
  }
  override def x_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;
  override def y_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;

  override def draw_impl = if (visible || D) {
    val bx = box
    if (visible) {
      p.stroke(c_strk.c)
      p.fill(c_strk.c)
      p.strokeWeight(sz)
      val (xs, ys) = (xy._1, xy._2)
      (1 until xs.size) foreach { i =>

        var (plt_x1, plt_y1) = (((xs(i - 1) - plt_xoff) * dx).toFloat + xoff(), h() + yoff() - ((ys(i - 1) - plt_yoff) * dy).toFloat)
        var (plt_x2, plt_y2) = (((xs(i) - plt_xoff) * dx).toFloat + xoff(), h() + yoff() - ((ys(i) - plt_yoff) * dy).toFloat)
        if (plt_y1 > bx._2 && plt_y1 < bx._4 && plt_y2 > bx._2 && plt_y2 < bx._4) {
          lt.drw(this, plt_x1, plt_y1, plt_x2, plt_y1, sz) // horizontal component
          lt.drw(this, plt_x2, plt_y1, plt_x2, plt_y2, sz) // vertical component
        }

      }
      p.strokeWeight(1)
    }
    if (D) {
      p.textAlign(PConstants.LEFT)
      p.fill(0)
      p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
      p.text("tick graph in debug mode", bx._1 + 50, bx._2 + 20)
      p.stroke(0x33, 0x55, 0x99)
      p.line(bx._1, bx._2, bx._3, bx._4)
      p.line(bx._1, bx._4, bx._3, bx._2)
    }
  }
}

/**
 * draw a line segments between two functions
 */
class seg_plt(sktch: p_sktch, val f1: foo, val f2: foo, lt: ln_typ, c_strk: clr, c_fll: clr, sz: Float) extends p_area(sktch) with plot {

  var (xy1, xy2) = (f1.slct(N = 5000), f2.slct(N = 5000))
  var (dx, dy, plt_xoff, plt_yoff) = (.0, .0, .0, .0)

  override def bnds = (min(f1.x_min, f2.x_min), max(f1.x_max, f2.x_max), min(f1.y_min, f2.y_min), max(f1.y_max, f2.y_max))
  override def zoom(x0: Double, x1: Double, y0: Double, y1: Double) = {
    val (xxy1, xxy2) = foo.co_slct(f1, f2, x0, x1, y0, y1, N = 5000)

    //if (xxy1._1.size != xxy2._1.size) throw new p_err("error. seg_area needs functions of equal size")
    assert(xxy1._1.size == xxy2._1.size, "error. seg_area needs functions of equal size")

    dx = w().toDouble / (x1 - x0); dy = h().toDouble / (y1 - y0)
    plt_xoff = x0; plt_yoff = y0
    xy1 = xxy1; xy2 = xxy2
  }
  override def x_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;
  override def y_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;

  override def draw_impl = if (visible || D) {
    import ik.util.num._
    val bx = box
    if (visible) {
      p.stroke(c_strk.c)
      p.fill(c_strk.c)
      p.strokeWeight(sz)
      val (x1s, x2s, y1s, y2s) = (xy1._1, xy2._1, xy1._2, xy2._2)

      (0 until x1s.size) foreach { i =>
        val plt_x1 = ((x1s(i) - plt_xoff) * dx).toFloat + xoff()
        val plt_x2 = ((x2s(i) - plt_xoff) * dx).toFloat + xoff()
        val plt_y1 = h() + yoff() - ((y1s(i) - plt_yoff) * dy).toFloat
        val plt_y2 = h() + yoff() - ((y2s(i) - plt_yoff) * dy).toFloat
        lt.drw(this, plt_x1, plt_y1, plt_x2, plt_y2, sz)
      }
      //p.strokeWeight(1)
    }
  }
}

/**
 * draw area tiles between two functions
 */
class seg_area(sktch: p_sktch, val f1: foo, val f2: foo, c_strk: clr, c_fll: clr, sz: Float) extends p_area(sktch) with plot {
  //abstract function. implemntation provides to draw the segement between (x0, x1, y0, y1)

  var (xy1, xy2) = (f1.slct(N = 5000), f2.slct(N = 5000))
  var (dx, dy, plt_xoff, plt_yoff) = (.0, .0, .0, .0)

  override def bnds = (min(f1.x_min, f2.x_min), max(f1.x_max, f2.x_max), min(f1.y_min, f2.y_min), max(f1.y_max, f2.y_max))
  override def zoom(x0: Double, x1: Double, y0: Double, y1: Double) = {

    /**
     * do a manual select as indices need to be lined up across both functions
     */
    val idx = (f1.x >= x0 || f2.x >= x0) && (f1.x <= x1 || f2.x <= x1)
    xy1 = (f1.x(idx), f1.y(idx))
    xy2 = (f2.x(idx), f2.y(idx))

    //if (xy1._1.size != xy1._2.size) throw new p_err("error. seg_area needs functions of equal size")
    assert(xy1._1.size == xy1._2.size, "error. seg_area needs functions of equal size")

    dx = w().toDouble / (x1 - x0); dy = h().toDouble / (y1 - y0)
    plt_xoff = x0; plt_yoff = y0

  }
  override def x_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;
  override def y_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;

  override def draw_impl = if (visible || D) {
    import ik.util.num._
    val bx = box
    if (visible) {
      p.stroke(c_strk.c)
      p.fill(c_strk.c)
      p.strokeWeight(sz)
      val (x1s, x2s, y1s, y2s) = (xy1._1, xy2._1, xy1._2, xy2._2)

      (0 until x1s.size) foreach { i =>
        p.stroke(c_strk.c)
        p.fill(c_strk.c)
        p.strokeWeight(sz)
        val plt_x1 = rng_lmt(((x1s(i) - plt_xoff) * dx).toFloat + xoff(), bx._1, bx._3)
        val plt_x2 = rng_lmt(((x2s(i) - plt_xoff) * dx).toFloat + xoff(), bx._1, bx._3)
        val plt_y1 = rng_lmt(h() + yoff() - ((y1s(i) - plt_yoff) * dy).toFloat, bx._2, bx._4)
        val plt_y2 = rng_lmt(h() + yoff() - ((y2s(i) - plt_yoff) * dy).toFloat, bx._2, bx._4)
        p.rect(plt_x1, plt_y1, plt_x2 - plt_x1, plt_y2 - plt_y1, 1)

        if (D) {
          p.stroke(clr.blk())
          p.line(plt_x1, plt_y1, plt_x2, plt_y2)
          p.line(plt_x1, plt_y2, plt_x2, plt_y1)
        }
      }
      p.strokeWeight(1)
    }
  }
}

/**
 * vertical segmentation plot. vertical lines are drawn through the plot on provided x co-ordinates
 */
class vlne_plt(sktch: p_sktch, val f: foo, lt: ln_typ, c_strk: clr, c_fll: clr, sz: Float) extends p_area(sktch) with plot {
  var xy = f.slct(N = 5000)

  var (dx, plt_xoff) = (.0, .0)
  override def bnds = (f.x_min, f.x_max, f.y_min, f.y_max)
  override def zoom(x0: Double, x1: Double, y0: Double, y1: Double) = {
    xy = f.slct(x0 = x0, x1 = x1, N = 5000)
    dx = w().toDouble / (x1 - x0)
    plt_xoff = x0;
  }
  override def x_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray
  override def y_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray

  override def draw_impl = if (visible || D) {
    val bx = box
    if (visible) {
      p.stroke(c_strk.c)
      p.fill(c_fll.c)
      val xs, ys = xy._1
      (0 until xs.size) foreach { i =>
        val plt_x = ((xs(i) - plt_xoff) * dx).toFloat + xoff()
        lt.drw(this, plt_x, bx._2, plt_x, bx._4, sz)
      }
    }
  }
}

/**
 * horizontal segmentation plot. horizontal lines are drawn through the plot on provided y co-ordinates
 * note! :
 */
class hlne_plt(sktch: p_sktch, val f: foo, lt: ln_typ, c_strk: clr, c_fll: clr, sz: Float) extends p_area(sktch) with plot {

  var xy = f.slct(N = 5000)

  var (dy, plt_yoff) = (.0, .0)
  override def bnds = (f.y_min, f.y_max, f.x_min, f.x_max)
  override def zoom(x0: Double, x1: Double, y0: Double, y1: Double) = {
    xy = f.slct(x0 = y0, x1 = y1, N = 5000)
    dy = h().toDouble / (y1 - y0)
    plt_yoff = y0;
  }

  override def x_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray
  override def y_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray

  override def draw_impl = if (visible || D) {
    val bx = box
    if (visible) {
      p.stroke(c_strk.c)
      p.fill(c_fll.c)
      val ys = xy._1
      (0 until ys.size) foreach { i =>
        //val plt_y = ((ys(i) - plt_yoff) * dy).toFloat + yoff()
        val plt_y = h() + yoff() - ((ys(i) - plt_yoff) * dy).toFloat
        lt.drw(this, bx._1, plt_y, bx._3, plt_y, sz)
      }
    }
  }
}

/**
 * a scatter plot widget.
 *
 */
//@deprecated("migration", "sctr_plt2")
//class sctr_plt(sktch: p_sktch, val f: foo, s: shape, c_strk: clr, c_fll: clr, sz: Float) extends p_area(sktch) with plot {
//
//  f.cbck = _ => { chngd() }
//
//  var xy = f.slct(N = 5000)
//
//  var (dx, dy, plt_xoff, plt_yoff) = (.0, .0, .0, .0)
//  override def bnds = (f.x_min, f.x_max, f.y_min, f.y_max)
//  override def zoom(x0: Double, x1: Double, y0: Double, y1: Double) = {
//    xy = f.slct(x0 = x0, x1 = x1, N = 5000)
//    dx = w().toDouble / (x1 - x0); dy = h().toDouble / (y1 - y0)
//    plt_xoff = x0; plt_yoff = y0
//  }
//  override def x_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;
//  override def y_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;
//
//  override def draw_impl = if (visible || D) {
//    val bx = box
//    if (visible) {
//      p.stroke(c_strk.c)
//      //p.strokeWeight(5)
//      p.fill(c_fll.c)
//      val (xs, ys) = (xy._1, xy._2)
//      (0 until xs.size) foreach { i =>
//        val (plt_x, plt_y) = (((xs(i) - plt_xoff) * dx).toFloat + xoff(), h() + yoff() - ((ys(i) - plt_yoff) * dy).toFloat)
//        if (in(bx, plt_x, plt_y)) s.drw(p, plt_x, plt_y, sz)
//      }
//    }
//    if (D) {
//      p.textAlign(PConstants.LEFT)
//      p.fill(0)
//      p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
//      p.text("scatter plot in debug mode", bx._1 + 50, bx._2 + 35)
//      p.stroke(0x55, 0x33, 0x99)
//      p.line(bx._1, bx._2, bx._3, bx._4)
//      p.line(bx._1, bx._4, bx._3, bx._2)
//    }
//  }
//}

class sctr_plt2(sktch: p_sktch, f: foo, s: Array[shp2], c_strk: Array[clr], c_fll: Array[clr], sz: Array[Float], ln_sz: Array[Float]) extends p_area(sktch) with plot {
  f.cbck = _ => { chngd() }
  var N = 5000
  var idx = (0 until f.x.size).toArray.ssmpl(N)
  var (dx, dy, plt_xoff, plt_yoff) = (.0, .0, .0, .0)
  override def bnds = (f.x_min, f.x_max, f.y_min, f.y_max)
  override def zoom(x0: Double, x1: Double, y0: Double, y1: Double) = {
    /**
     * find the indices of the area to be plotted
     * subsample if required
     */
    idx = {
      var (i0, i1) = f.x.srch(x0, x1)
      val idx = (i0 to i1).toArray
      if (idx.size > N) idx.ssmpl(N)
      else idx
    }
    /**
     * compute scaling factors and offsets
     */
    dx = w().toDouble / (x1 - x0)
    dy = h().toDouble / (y1 - y0)
    plt_xoff = x0; plt_yoff = y0
  }
  override def x_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;
  override def y_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;

  override def draw_impl = {
    val bx = box
    if (visible) {
      val (xs, ys) = (f.x, f.y)
      idx foreach { i =>
        p.stroke(c_strk(i % c_strk.size).c)
        p.fill(c_fll(i % c_fll.size).c)
        val (plt_x, plt_y) = (((xs(i) - plt_xoff) * dx).toFloat + xoff(), h() + yoff() - ((ys(i) - plt_yoff) * dy).toFloat)
        if (in(bx, plt_x, plt_y)) s(i % s.size).drw(p, plt_x, plt_y, sz(i % sz.size), ln_sz(i % ln_sz.size))
      }
    }
    if (D) drw_dbg
  }

  protected def drw_dbg = {
    val bx = box
    p.textAlign(PConstants.LEFT)
    p.fill(0)
    p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
    p.text("scatter plot in debug mode", bx._1 + 50, bx._2 + 35)
    p.stroke(0x55, 0x33, 0x99)
    p.line(bx._1, bx._2, bx._3, bx._4)
    p.line(bx._1, bx._4, bx._3, bx._2)
  }
}

/**
 * a scatterplot like drawing of labels - plot some labels at some co-ordinates
 */
class sctr_lbls2(sktch: p_sktch, f: foo, l: Array[String], sz: Array[Int], lbl_xoff: Array[Int], lbl_yoff: Array[Int], clr: Array[clr], fnt: String) extends p_area(sktch) with plot {
  f.cbck = _ => { chngd() }
  var N = 1000
  var idx = (0 until f.x.size).toArray.ssmpl(N)
  var (dx, dy, plt_xoff, plt_yoff) = (.0, .0, .0, .0)
  override def bnds = (f.x_min, f.x_max, f.y_min, f.y_max)
  override def zoom(x0: Double, x1: Double, y0: Double, y1: Double) = {
    /**
     * find the indices of the area to be plotted
     * subsample if required
     */
    idx = {
      var (i0, i1) = f.x.srch(x0, x1)
      val idx = (i0 to i1).toArray
      if (idx.size > N) idx.ssmpl(N)
      else idx
    }
    /**
     * compute scaling factors and offsets
     */
    dx = w().toDouble / (x1 - x0)
    dy = h().toDouble / (y1 - y0)
    plt_xoff = x0; plt_yoff = y0
  }
  override def x_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;
  override def y_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;

  override def draw_impl = {
    val bx = box
    if (visible) {
      val (xs, ys) = (f.x, f.y)

      p.textAlign(PConstants.CENTER)

      idx foreach { i =>
        p.fill(clr(i % clr.size).c)
        p.textFont(new PFont(new Font(fnt, Font.PLAIN, sz(i % sz.size)), false))
        val (plt_x, plt_y) = (((xs(i) - plt_xoff) * dx).toFloat + xoff() + lbl_xoff(i % lbl_xoff.size), h() + yoff() - ((ys(i) - plt_yoff) * dy).toFloat - lbl_yoff(i % lbl_yoff.size))
        if (in(bx, plt_x, plt_y)) p.text(l(i % l.size), plt_x, plt_y)
      }
    }
    if (D) drw_dbg
  }

  protected def drw_dbg = {
    val bx = box
    p.textAlign(PConstants.LEFT)
    p.fill(0)
    p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
    p.text("scatter plot in debug mode", bx._1 + 50, bx._2 + 35)
    p.stroke(0x55, 0x33, 0x99)
    p.line(bx._1, bx._2, bx._3, bx._4)
    p.line(bx._1, bx._4, bx._3, bx._2)
  }
}

/**
 * a container for a multitude of plots. it is itself a plot as it extends
 * the plot trait
 *
 *   +------+     +--------+
 *   | plt  |<--<>|  plts  |
 *   |      |<i---|        |
 *   +------+     +--------+
 *   @author iztok
 */
class plot_lst extends plot {
  private val plts = new HashSet[plot]
  /**
   * union of domains and ranges of all plotted functions. can only
   * change when a plot (with a function) is added or removed
   */
  private var (x_min, x_max, y_min, y_max) = (.0, .0, .0, .0)
  /**
   * currently selected window (a subset of above). changes when
   * a zoom window is changed
   */
  private var (w_x_min, w_x_max, w_y_min, w_y_max) = (.0, .0, .0, .0)
  /**
   * give bounds of this (composed) plot
   */
  override def bnds = (x_min, x_max, y_min, y_max)
  /**
   * zoom the plot to bounds specified by
   */
  override def zoom(xw0: Double, xw1: Double, yw0: Double, yw1: Double) = {
    //dmp(">> zooming to : ", xw0, xw1, yw0, yw1)
    w2c(xw0, xw1, yw0, yw1)
    plts foreach { p => p.zoom(w_x_min, w_x_max, w_y_min, w_y_max) }
  }
  /**
   * click on a plot
   */
  override def clck(x: Double, y: Double) = plts foreach (_.clck(x, y))
  /**
   * provide tick values to be used as labels along the x-axis
   */
  override def x_tcks(n: Int): Array[Double] = {
    val dx = (w_x_max - w_x_min) / (n - 1)
    (0 until n) map (_.toDouble * dx + w_x_min) toArray
  }
  /**
   * provide tick values to be used as labels along the y-axis
   */
  override def y_tcks(n: Int): Array[Double] = {
    val dy = (w_y_max - w_y_min) / (n - 1)
    (0 until n) map (_.toDouble * dy + w_y_min) toArray
  }
  /**
   * add a plot to the group and adjust new bounds accordingly
   */
  def +=(p: plot) = { plts += p; scn; p.chngd = chngd }
  /**
   * remove a plot from the group (+ adjust bounds)
   */
  def -=(p: plot) = { plts -= p; scn }

  /**
   *  scan all plots for boundary adjust as necessary
   */
  def scn = {
    x_min = Double.MaxValue; x_max = Double.MinValue; y_min = Double.MaxValue; y_max = Double.MinValue
    plts foreach { p =>
      val (f_x_min, f_x_max, f_y_min, f_y_max) = p.bnds
      //dmp("plots scan - adding foo     : ", f_x_min, f_x_max, f_y_min, f_y_max)
      if (!f_x_min.isNaN) x_min = min(x_min, f_x_min)
      if (!f_x_max.isNaN) x_max = max(x_max, f_x_max)
      if (!f_y_min.isNaN) y_min = min(y_min, f_y_min)
      if (!f_y_max.isNaN) y_max = max(y_max, f_y_max)
    }
    //dmp("plots scan - combined bounds: ", x_min, x_max, y_min, y_max)

  }

  /**
   * transform window co-ordinates (0-1) into cartesian (x,y)
   */
  private def w2c(xw0: Double, xw1: Double, yw0: Double, yw1: Double) = {
    //dmp("w2c - selected window: ", xw0, xw1, yw0, yw1)
    //dmp("w2c - selected window: ", x_min, xmax, ymin, ymax)
    val (dx, dy) = (x_max - x_min, y_max - y_min)
    w_x_min = x_min + xw0 * dx; w_x_max = x_min + xw1 * dx; w_y_min = y_min + yw0 * dy; w_y_max = y_min + yw1 * dy

    //dmp("w2c - selected window: ", w_x_min, w_x_max, w_y_min, w_y_max)
  }
}
