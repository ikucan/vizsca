package ik.viz

import processing.core.PConstants
import processing.opengl.PGraphicsOpenGL
import javax.media.opengl.GL2
import javax.media.opengl.GL
import ik.util.num._

/**
 * trait describing a line type
 */
trait ln_typ {
  // compare for equality in pixel space - equal if within a hundredth :)
  def eq(v1: Float, v2: Float) = math.abs(v1 - v2) < 0.01f
  def drw(a: p_area, x1: Double, y1: Double, x2: Double, y2: Double, sz: Double) = {}
}

class - extends ln_typ {
  override def drw(a: p_area, x1: Double, y1: Double, x2: Double, y2: Double, sz: Double) = {
    val (xx1, yy1, xx2, yy2) = if (x1 > x2) (x2.toFloat, y2.toFloat, x1.toFloat, y1.toFloat) else (x1.toFloat, y1.toFloat, x2.toFloat, y2.toFloat)
    val p = a.get_p

    //println(xx1, yy1, xx2, yy2)
    /**
     * NOTE:>> ybx1 is the top edge and smaller than ybx2
     */
    val (xbx1, ybx1, xbx2, ybx2) = a.box
    p.strokeWeight(sz.toFloat)
    // check for horizontal lines
    if (eq(yy1, yy2)) {
      if (yy1 >= ybx1 && yy1 <= ybx2) p.line(rng_lmt(xx1, xbx1, xbx2), yy1, rng_lmt(xx2, xbx1, xbx2), yy2)
    } // vertical line
    else if (eq(xx1, xx2)) {
      if (xx1 >= xbx1 && xx1 <= xbx2) p.line(xx1, rng_lmt(yy1, ybx1, ybx2), xx2, rng_lmt(yy2, ybx1, ybx2))
    } else {
      /**
       * to have any chance of being drawn a line must start before box ends
       * or end after the box starts. x is easier as it is ordered (L to R)
       */
      val x_X_box = (xx1 <= xbx2) && (xx2 >= xbx1)
      /**
       * same for y
       */
      val y_lt_box = (yy1 < ybx1) && (yy2 < ybx1)
      val y_gt_box = (yy1 > ybx2) && (yy2 > ybx2)

      if (x_X_box && !y_lt_box && !y_gt_box) {
        // all relevant intercepts sorted by x coord
        val icpt = a.box_intrsct(xx1, yy1, xx2, yy2) filter { p => p._1 > xx1 && p._1 < xx2 } sortWith (_._1 < _._1)
        val p1 = if (a.in((xbx1, ybx1, xbx2, ybx2), xx1, yy1)) (xx1, yy1) else icpt.head
        val p2 = if (a.in((xbx1, ybx1, xbx2, ybx2), xx2, yy2)) (xx2, yy2) else icpt.last
        p.line(p1._1, p1._2, p2._1, p2._2)
      }
    }
  }
}

//TODO:>> these two need to be re-written with the new implementation of the drw function
//class *(stp: Int) extends ln_typ {
//  override def drw(p: p_sktch, x1: Double, y1: Double, x2: Double, y2: Double, sz: Double) = {
//    val alph = math.atan((y2 - y1) / (x2 - x1))
//    val (dx, dy) = (stp * math.cos(alph), stp * math.sin(alph))
//    var (x, y) = (x1, y1)
//    while (x < x2) {
//      val (xn, yn) = (x + dx, y + dy)
//      p.ellipseMode(PConstants.CENTER)
//      p.ellipse(xn.toFloat, yn.toFloat, sz.toFloat, sz.toFloat)
//      //p.ellipse(xn.toFloat, yn.toFloat, sz.toFloat, sz.toFloat)
//      x = xn; y = yn
//    }
//  }
//}
//class --(stp: Int, gp: Int) extends ln_typ {
//  override def drw(p: p_sktch, x1: Double, y1: Double, x2: Double, y2: Double, sz: Double) = {
//    val (xx1, xx2) = if (x2 > x1) (x1, x2) else (x2, x1)
//    val (yy1, yy2) = if (y2 > y1) (y1, y2) else (y2, y1)
//    /**
//     * if segment is vertical
//     */
//    if (xx1 == xx2) {
//      var (x, y) = (xx1, yy1)
//      while (y < yy2) {
//        val yn = y + stp
//        if (yn < yy2) p.line(x.toFloat, y.toFloat, x.toFloat, yn.toFloat)
//        y = yn + gp
//      }
//    } else {
//      val alph = math.atan((yy2 - yy1) / (xx2 - xx1))
//
//      val (xln, yln, xgp, ygp) = (stp * math.cos(alph), stp * math.sin(alph), gp * math.cos(alph), gp * math.sin(alph))
//      var (x, y) = (xx1, yy1)
//      while (x < xx2) {
//        val (xn, yn) = (x + xln, y + yln)
//        if (xn <= xx2) p.line(x.toFloat, y.toFloat, xn.toFloat, yn.toFloat)
//        else p.line(x.toFloat, y.toFloat, xx2.toFloat, yy2.toFloat)
//        x = xn + xgp; y = yn + ygp
//      }
//    }
//  }
//}

/**
 * a shape factory
 */
object ln_typ {
  def - = new -
  //def * = new *(5)
  //def *(stp: Int = 5) = new *(stp)
  //def -- = new --(2, 5)
  //def --(stp: Int = 2, gp: Int = 5) = new --(stp, gp)
  /**
   * some placeholders
   */
  def -*- = new -
  def --*-- = new -
  def ->-> = new -
  def -> = new -
}
