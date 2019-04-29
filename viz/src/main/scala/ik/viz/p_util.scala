package ik.viz

import scala.math.max
import scala.math.min
import ik.util.num._

trait p_util {
  self: p_area =>

  // define boxed bounds of the area 
  def box = (xoff(), yoff(), xoff() + w(), yoff() + h())

  /**
   * define the four lines of the box expressed in Ax + By = C format
   */
  def box_edgs = {
    val (xbx1, ybx1, xbx2, ybx2) = box
    // first vertical line
    val A1 = ybx2 - ybx1
    val B1 = 0f
    val C1 = A1 * xbx1
    // first horizontal line
    val A2 = 0f
    val B2 = xbx1 - xbx2
    val C2 = B2 * ybx1
    // second vertical line
    val A3 = ybx2 - ybx1
    val B3 = 0f
    val C3 = A3 * xbx2
    // second horizontal line
    val A4 = 0f
    val B4 = xbx1 - xbx2
    val C4 = B4 * ybx2
    // return the four lines as tuples
    ((A1, B1, C1), (A2, B2, C2), (A3, B3, C3), (A4, B4, C4))
  }

  // box properties 
  def box_top(bx: (Float, Float, Float, Float)) = bx._2
  def box_btm(bx: (Float, Float, Float, Float)) = bx._4
  def box_lft(bx: (Float, Float, Float, Float)) = bx._1
  def box_rgh(bx: (Float, Float, Float, Float)) = bx._3
  def box_wdth(bx: (Float, Float, Float, Float)) = bx._3 - bx._1
  def box_hght(bx: (Float, Float, Float, Float)) = bx._4 - bx._2
  def box_xmid(bx: (Float, Float, Float, Float)) = box_wdth(bx) / 2.0f
  def box_ymid(bx: (Float, Float, Float, Float)) = box_hght(bx) / 2.0f

  // box partitioning functions
  // left half of a given box
  def l_hlf(): (Float, Float, Float, Float) = l_hlf(box)
  def l_hlf(bx: (Float, Float, Float, Float)) = (bx._1, bx._2, box_xmid(bx), bx._4)
  // right half
  def r_hlf(): (Float, Float, Float, Float) = r_hlf(box)
  def r_hlf(bx: (Float, Float, Float, Float)) = (box_xmid(bx), bx._2, bx._3, bx._4)
  // top half
  def t_hlf(): (Float, Float, Float, Float) = t_hlf(box)
  def t_hlf(bx: (Float, Float, Float, Float)) = (bx._1, bx._2, bx._3, box_ymid(bx))
  // bottom half
  def b_hlf(): (Float, Float, Float, Float) = b_hlf(box)
  def b_hlf(bx: (Float, Float, Float, Float)) = (bx._1, box_ymid(bx), bx._3, bx._4)

  // trim from box
  def r_trim(bx: (Float, Float, Float, Float), w: Float) = (bx._1, bx._2, max(bx._1, bx._3 - w), bx._4)
  def l_trim(bx: (Float, Float, Float, Float), w: Float) = (min(bx._1 + w, bx._3), bx._2, bx._3, bx._4)
  def t_trim(bx: (Float, Float, Float, Float), h: Float) = (bx._1, min(bx._2 + h, bx._4), bx._3, bx._4)
  def b_trim(bx: (Float, Float, Float, Float), h: Float) = (bx._1, bx._2, bx._3, max(bx._2, bx._4 - h))
  def trim(bx: (Float, Float, Float, Float), l: Float = 0f, r: Float = 0f, t: Float = 0f, b: Float = 0f) =
    (min(bx._1 + l, bx._3 - r), min(bx._2 + t, bx._4 - b), max(bx._1 + l, bx._3 - r), max(bx._2 + t, bx._4 - b))

  def h_in(bx: (Float, Float, Float, Float), x: Float) = x >= bx._1 && x <= bx._3
  def v_in(bx: (Float, Float, Float, Float), y: Float) = y >= bx._2 && y <= bx._4
  def in(bx: (Float, Float, Float, Float), x: Float, y: Float) = x >= bx._1 && x <= bx._3 && y >= bx._2 && y <= bx._4

  // subset a strip from box 
  def l_strip(bx: (Float, Float, Float, Float), w: Float) = (bx._1, bx._2, min(bx._1 + w, bx._3), bx._4)
  def r_strip(bx: (Float, Float, Float, Float), w: Float) = (max(bx._1, bx._3 - w), bx._2, bx._3, bx._4)

  // split a box into 4 guadrants: top left, tr, br and bl
  def qdrnts(): List[(Float, Float, Float, Float)] = qdrnts(box)
  def qdrnts(bx: (Float, Float, Float, Float)) = List(t_hlf(l_hlf), t_hlf(r_hlf), b_hlf(r_hlf), b_hlf(l_hlf))

  def box_stack(b: (Float, Float, Float, Float), n: Int, spcr: Float = 10f): List[(Float, Float, Float, Float)] = {
    if (n <= 1) List(b)
    else {
      val h = max((box_hght(b) - (n - 1) * spcr) / n, 0)
      (0 until n).toList map { x => (b._1, b._2 + x * (h + spcr), b._3, b._2 + x * (h + spcr) + h) }
    }
  }

  def box_row(b: (Float, Float, Float, Float), n: Int, spcr: Float = 10f): List[(Float, Float, Float, Float)] = {
    if (n <= 1) List(b)
    else {
      val w = max((box_wdth(b) - (n - 1) * spcr) / n, 0)
      (0 until n).toList map { x => (b._1 + x * (w + spcr), b._2, b._1 + x * (w + spcr) + w, b._4) }
    }
  }

  def box_grid(b: (Float, Float, Float, Float), n_rows: Int, n_cols: Int, spcr: Float = .0f) = box_stack(b, n_rows, spcr) map (box_row(_, n_cols, spcr))

  // draw a line frame around the box
  def box_frm(): Unit = box_frm(box)
  def box_frm(bx: (Float, Float, Float, Float)) = {
    p.line(bx._1, bx._2, bx._3, bx._2)
    p.line(bx._3, bx._2, bx._3, bx._4)
    p.line(bx._3, bx._4, bx._1, bx._4)
    p.line(bx._1, bx._4, bx._1, bx._2)
  }

  // fill and line the box
  def box_fll(): Unit = box_fll(box)
  def box_fll(bx: (Float, Float, Float, Float)) = p.rect(bx._1, bx._2, bx._3 - bx._1, bx._4 - bx._2)

  def box_txt(t: String, bx: (Float, Float, Float, Float)) = p.text(t, bx._1, bx._2, bx._3 - bx._1, bx._4 - bx._2)

  // -------------------------------------------------
  // geometry functions, perhaps extract to geom class
  // -------------------------------------------------
  /**
   * find an intersect between a line and the edge of the area
   * RESTRICT to intersections ON the actual edge segments not lines
   * containing the edge segments
   */
  def box_intrsct(x1: Float, y1: Float, x2: Float, y2: Float) = {
    // sort the segment so xx1 is to the left
    val (xx1, yy1, xx2, yy2) = if (x1 > x2) (x2, y2, x1, y1) else (x1, y1, x2, y2)
    // parametrise as a line equation
    val A = yy2 - yy1
    val B = xx1 - xx2
    val C = A * xx1 + B * yy1

    /**
     * get box edges
     */
    val (v1, h1, v2, h2) = box_edgs
    val (xbx1, ybx1, xbx2, ybx2) = box
    /**
     * find intersection of the line with each edge line and filter by edge segment
     */
    intrsct(A, B, C)(v1, h1, v2, h2) filter { pi => pi._1 > xbx1 - 1 && pi._1 < xbx2 + 1 && pi._2 > ybx1 - 1 && pi._2 < ybx2 + 1 }
  }

  /**
   * find all intersects between a line a an a polygon represented by a number of lines
   * each line is represented by A, B, C as in the format Ax+ By = C
   */
  def intrsct(A: Float, B: Float, C: Float)(plygon: (Float, Float, Float)*) = {
    /**
     * helper - compare a line from the plygon with the line examined
     */
    def isct(AA: Float, BB: Float, CC: Float): Option[(Float, Float)] = {
      val det = A * BB - AA * B
      if (det != 0) {
        val x = (BB * C - B * CC) / det
        val y = (A * CC - AA * C) / det
        Some(x, y)
      } else None
    }

    /**
     * compute the intersect between the line and each line of the intersect, filter out parallel lines
     */
    plygon map { plg_ln => isct(plg_ln._1, plg_ln._2, plg_ln._3) } filter (_ match { case Some(x) => true; case None => false }) map (_.get)
  }
}
