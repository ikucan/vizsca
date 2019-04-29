package ik.viz

import java.awt.Font
//import ik.util.mcro.hlpr.dmp
import java.text.DecimalFormat

import scala.math.max
import scala.math.min

import ik.util.log.loggd
import ik.util.m_brt
import ik.util.num.geom.dst
import processing.core.PConstants
import processing.core.PFont
import processing.core.PImage

/**
 * the axis class. draws a vertical or horizontal axis with major and minor ticks
 * @author i.kucan
 *
 */
class axis(sktch: p_sktch) extends p_drawn(sktch) {
  var vrtcl = false
  var yy = false
  var len = () => p.w
  var clr = p.color(0x33, 0x33, 0x66, 0xf0)
  var ovr = () => 8f
  var mjr_tck = () => 5
  var mnr_tck = () => 4
  var mjr_wght = () => 1
  var mnr_wght = () => max(mjr_wght() - 1, 1)
  var min_mnr_dst = () => 20
  var lbls: (Int) => Array[Double] = null //(n: Int) => (1 to n) map (_.toString) toList;
  var fnt_sz = () => 12
  var lbl_fnt = new PFont(new Font("Arial", Font.PLAIN, fnt_sz()), false)
  var lbl_frmt: (Double) => String = new DecimalFormat("#.0").format
  var lbl_clr = p.color(0x33, 0x33, 0x66, 0xf0)

  def rst = {
    lbl_fnt = new PFont(new Font("Arial", Font.PLAIN, fnt_sz()), false)
  }

  override def draw_impl = if (visible) {
    val dl = len() / (mjr_tck() - 1)
    val ddl = dl / (mnr_tck() + 1)
    p stroke clr
    p strokeWeight mjr_wght()
    if (vrtcl) {
      // draw in reverse y space (from bottom to top)
      p.line(xoff(), yoff() + ovr(), xoff(), yoff() - len() - ovr())
      (0 until mjr_tck()) foreach { n_mjr => p.line(xoff() - 3, yoff() - n_mjr * dl, xoff() + 4, yoff() - n_mjr * dl) }
      if (ddl > min_mnr_dst())
        (0 until (mjr_tck() - 1)) foreach { n_mjr =>
          (1 to mnr_tck()) foreach { n_mnr =>
            val y_mnr = yoff() - n_mjr * dl - n_mnr * ddl
            p.line(xoff() - 1, y_mnr, xoff() + 2, y_mnr)
          }
        }
    } else {
      // draw the axis line
      p.line(xoff() - ovr(), yoff(), xoff() + len() + ovr(), yoff())
      // draw the major ticks
      (0 until mjr_tck()) foreach { n_mjr => p.line(xoff() + n_mjr * dl, yoff() - 3, xoff() + n_mjr * dl, yoff() + 4) }
      // draw the minor ticks
      if (ddl > min_mnr_dst())
        (0 until (mjr_tck() - 1)) foreach { n_mjr =>
          (1 to mnr_tck()) foreach { n_mnr =>
            val x_mnr = xoff() + n_mjr * dl + n_mnr * ddl
            p.line(x_mnr, yoff() - 1, x_mnr, yoff() + 2)
          }
        }
    }
    // draw the labels
    if (lbls != null) {
      p.fill(lbl_clr)
      p.textFont(lbl_fnt)
      if (vrtcl) {
        p.pushMatrix
        p.translate(xoff() - 15, yoff())
        p.rotate(-PConstants.HALF_PI)
      }
      p.textAlign(if (vrtcl) { if (yy) PConstants.LEFT else PConstants.RIGHT } else PConstants.CENTER)
      val l_s = lbls(mjr_tck())
      if (vrtcl)
        //        if (yy) (0 until mjr_tck()) foreach { n => p.text(lbl_frmt(l_s(n)), xoff() + 15, yoff() - n * dl + fnt_sz() / 2) }
        //        else (0 until mjr_tck()) foreach { n => p.text(lbl_frmt(l_s(n)), xoff() - 15, yoff() - n * dl + fnt_sz() / 2) }
        (1 until mjr_tck() - 1) foreach { n =>
          val (x, y) = (n * dl + fnt_sz() / 2, 0)
          p.text(lbl_frmt(l_s(n)), x, y)
        }
      else
        (1 until mjr_tck() - 1) foreach { n => p.text(lbl_frmt(l_s(n)), xoff() + n * dl - 2, yoff() + 10 + fnt_sz()) }
      if (vrtcl) {
        p.popMatrix
      }
    }

    p strokeWeight 1
  }
}

class axxis(sktch: p_sktch) extends axis(sktch) {
  var tck_loc: () => Seq[Float] = () => Array[Float]()
  var rdy_lbls: () => Seq[String] = () => Array[String]()
  override def draw_impl = if (visible) {
    val dl = len() / (mjr_tck() - 1)
    val ddl = dl / (mnr_tck() + 1)
    p stroke clr
    p strokeWeight mjr_wght()
    val mjr_tcks = tck_loc()
    if (vrtcl) {
      // draw in reverse y space (from bottom to top)
      p.line(xoff(), yoff() + ovr(), xoff(), yoff() - len() - ovr())
      mjr_tcks foreach { xx => p.line(xoff() - 3, xx, xoff() + 4, xx) }
    } else {
      // draw the axis line
      p.line(xoff() - ovr(), yoff(), xoff() + len() + ovr(), yoff())
      mjr_tcks foreach { xx => p.line(xx, yoff() - 3, xx, yoff() + 4) }
      (0 until rdy_lbls().size) foreach { i =>
      }
    }
    //println(rdy_lbls().toList)
    val ls = rdy_lbls()
    if (ls.size > 0) {
      // draw the labels
      p.fill(0x33, 0x33, 0x55, 0x240)

      p.textAlign(if (vrtcl) { if (yy) PConstants.LEFT else PConstants.RIGHT } else PConstants.CENTER)

      if (vrtcl) (0 until ls.size) foreach { i => p.text(ls(i), xoff() - 15, mjr_tcks(i)) }
      else (0 until ls.size) foreach { i => p.text(ls(i), mjr_tcks(i), yoff() + 10 + fnt_sz()) }
    }

    p strokeWeight 1
  }
}

/**
 *
 */
class wtr_mrk(sktch: p_sktch, clr: Int = 0xe3e0e0) extends p_area(sktch, Int.MinValue) with loggd {
  var wh_p = (w().toInt, h().toInt)
  var img: PImage = null
  val (x0, x1, y0, y1) = (-2.8, 1.5, -.3, 2.3)
  override def draw_impl = if (visible || D) {
    val bx = box
    if (visible) {
      val wh = (w().toInt, h().toInt)
      if (wh != wh_p) {
        img = sktch.createImage(wh._1, wh._2, PConstants.RGB)
        //img.mask
        val mst = m_brt.gen(xdim = wh._1, ydim = wh._2, x0 = x0, x1 = x1, y0 = y0, y1 = y1, bse_clr = clr)
        for (i <- 0 until wh._1; j <- 0 until wh._2) img.set(i, j, mst(j)(i))
        img.updatePixels
        wh_p = wh
      }
      if (img != null)
        sktch.image(img, xoff(), yoff())
    }
    if (D) {
      p.fill(0x99, 0xbb, 0x66)
      p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
      p.text("rice paper in debug mode", bx._1 + 5, bx._2 + 15)
      p.stroke(0xff, 0x00, 0xff)
      p.line(bx._1, bx._2, bx._3, bx._2)
      p.line(bx._3, bx._2, bx._3, bx._4)
      p.line(bx._3, bx._4, bx._1, bx._4)
      p.line(bx._1, bx._4, bx._1, bx._2)
    }
  }
}

/**
 * a rectangular area with a grid
 * behavioural settings (defined as members)
 * n_hrz : function which defines how many horizontal lines in the grid (excluding top and bottom edge of the rectangle)
 * n_vrt : number of verticals, excluding edges
 * clr_hrz : colour of horizontal lines
 * clr_vrt : colour of vertical lines
 */
class grid(sktch: p_sktch) extends p_area(sktch) {
  var n_hrz = () => max(3f, h() / 130).asInstanceOf[Int]
  var n_vrt = () => max(2f, w() / 190).asInstanceOf[Int]
  var clr_hrz = p.color(0xbb, 0xbb, 0xcc, 0x45)
  var clr_vrt = p.color(0xaa, 0xaa, 0xbb, 0x45)
  var ln_wght = 1

  override def draw_impl = if (visible || D) {
    val bx = box
    if (visible) {
      val dx = (bx._3 - bx._1) / (n_vrt() + 1)
      val dy = (bx._4 - bx._2) / (n_hrz() + 1)
      p.stroke(clr_vrt)
      p.strokeWeight(ln_wght)
      (1 to n_vrt()) foreach { n => p.line(bx._1 + n * dx, bx._2, bx._1 + n * dx, bx._4) }
      p.stroke(clr_hrz)
      (1 to n_hrz()) foreach { n => p.line(bx._1, bx._2 + n * dy, bx._3, bx._2 + n * dy) }
    }
    if (D) {
      p.fill(0x99, 0xbb, 0x66)
      p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
      p.text("grid in debug mode", bx._1 + 5, bx._2 + 30)
      p.stroke(0xbb, 0xcc, 0x44)
      p.line(bx._1, bx._2, bx._3, bx._2)
      p.line(bx._3, bx._2, bx._3, bx._4)
      p.line(bx._3, bx._4, bx._1, bx._4)
      p.line(bx._1, bx._4, bx._1, bx._2)
    }
  }
}

/**
 * a viewport class. provides a window inside a frame. basically square within a square
 * viewports can be used for static edges around objects (say a border inside a window)
 * or as dynamic constructs, such as an area selected with a mouse
 * @author iztok
 */
class vw_prt(sktch: p_sktch) extends p_area(sktch) {
  var x_prt = () => 20f
  var y_prt = () => 20f
  var w_prt = () => w() - 2 * x_prt()
  var h_prt = () => h() - 2 * y_prt()
  var clr_frm = p.color(0xd7, 0xd7, 0xe0, 0x0f)
  var clr_prt = p.color(0xf9, 0xf4, 0xfa, 0xaa)

  override def draw_impl = if (visible || D) {
    // recalculate all the parameters into absolute cartesian space. we are basically dealing with
    // two squares, the inner and the outter so just evaluate the eight points efficiently
    val x0_o = xoff()
    val y0_o = yoff()
    val x1_o = x0_o + w()
    val y1_o = y0_o + h()

    val x0_i = x0_o + x_prt()
    val y0_i = y0_o + y_prt()
    val x1_i = x0_i + w_prt()
    val y1_i = y0_i + h_prt()

    if (smth) p.smooth()

    if (visible) {
      p.noStroke
      p.fill(clr_frm)
      p.quad(x0_o, y0_o, x1_o, y0_o, x1_o, y0_i, x0_o, y0_i)
      p.quad(x0_o, y0_i, x0_i, y0_i, x0_i, y1_i, x0_o, y1_i)
      p.quad(x0_o, y1_i, x1_o, y1_i, x1_o, y1_o, x0_o, y1_o)
      p.quad(x1_i, y0_i, x1_o, y0_i, x1_o, y1_i, x1_i, y1_i)
      p.fill(clr_prt)
      p.quad(x0_i, y0_i, x1_i, y0_i, x1_i, y1_i, x0_i, y1_i)
    }
    if (D) { // debug
      p.fill(0x99, 0xbb, 0x66)
      p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
      p.text("view port in debug mode", x0_o + 5, y0_o + 45)
      p.stroke(0xbb, 0xcc, 0x44)
      p.line(x0_o, (y1_i - y0_i) / 2 + y0_i, x1_o, (y1_i - y0_i) / 2 + y0_i)
      p.line(x0_o, y0_o, x1_o, y1_o)
      p.line(x0_i, y1_i, x1_i, y0_i)
      p.line(x0_o, y0_o, x1_o, y0_o)
      p.line(x1_o, y0_o, x1_o, y1_o)
      p.line(x1_o, y1_o, x0_o, y1_o)
      p.line(x0_o, y1_o, x0_o, y0_o)
      p.line(x0_i, y0_i, x1_i, y0_i)
      p.line(x1_i, y0_i, x1_i, y1_i)
      p.line(x1_i, y1_i, x0_i, y1_i)
      p.line(x0_i, y1_i, x0_i, y0_i)
    }
  }
}

/**
 * zoom pad - manages zooming and panning of the graph. keeps track of what the zoom ratio
 * is and changes it when user input is provided.
 * treating combined viewable space as a range (0,0) to (1,1) in (x,y) space
 * zoom pad provides the means to zoom and pan the complete navigable space (-1,-1) to (2, 2)
 * @author iztok
 *
 */
class zzoom_pad(sktch: p_sktch) extends p_area(sktch, Int.MaxValue) {
  var (clck_pt0, clck_pt1) = ((0f, 0f), (0f, 0f))
  var active = false
  var clr_frm = p.color(0xdd, 0xdd, 0xbb, 0xc0)
  var clr_box = p.color(0xbb, 0x88, 0x99, 0xc0)
  var frm_lne_wgth = 1

  // state of zoomedness
  val dflt = (-0.025f, 1.025f, -0.05f, 1.05f)
  var (zx0, zx1, zy0, zy1) = dflt
  var hst = List[(Float, Float, Float, Float)]()
  var ftr = List[(Float, Float, Float, Float)]()

  // behavioral settings
  private val N_UND = 100
  /**
   * notification of new state zoomedness.
   * clients should replace with their own callback
   */
  var zoom = (xw0: Double, xw1: Double, yw0: Double, yw1: Double) => {}
  /**
   * notification of a new click.
   * clients should replace with their own callback
   */
  var l_clck = (x: Double, y: Double) => {}
  var r_clck = (x: Double, y: Double) => {}
  var d_clck = (x: Double, y: Double) => {}

  /**
   * helper function to push old state and call zoom callback with new zoom params
   */
  private def set_zoom(xw0: Float, xw1: Float, yw0: Float, yw1: Float) = {
    hst = hst :+ ((zx0, zx1, zy0, zy1))
    zx0 = xw0; zx1 = xw1; zy0 = yw0; zy1 = yw1
    if (hst.size > N_UND) hst = hst.tail
    //println(hst.size)
    zoom(zx0, zx1, zy0, zy1)
  }

  /**
   * helper function to undo the last zoom change
   */
  private def und_zoom() =
    if (hst.size > 0) {
      val (xw0, xw1, yw0, yw1) = hst.last
      hst = hst.slice(0, hst.size - 1)
      zx0 = xw0; zx1 = xw1; zy0 = yw0; zy1 = yw1
      zoom(xw0, xw1, yw0, yw1)
    }

  override def mous_prsd = {
    clck_pt0 = project(p.mouseX, p.mouseY)
    active = false
  }

  override def mous_drgd = {
    clck_pt1 = project(p.mouseX, p.mouseY)
    if (key_dwn == 9) active = false // if <esc> drop the drag
    else active = dst(clck_pt0._1, clck_pt0._2, clck_pt1._1, clck_pt1._2) > 10
  }

  override def mous_rlsd = {
    if (active) {
      // note that y co-ords are reversed. max device y is min visual y. (device origin 
      // is at top left, visual origin at bottom left) 
      val x0 = (min(clck_pt0._1, clck_pt1._1) - xoff()) / w()
      val y0 = 1f - (max(clck_pt0._2, clck_pt1._2) - yoff()) / h()
      val x1 = (max(clck_pt0._1, clck_pt1._1) - xoff()) / w()
      val y1 = 1f - (min(clck_pt0._2, clck_pt1._2) - yoff()) / h()

      // calcualte new zoom-ratio
      val (dX, dY) = (zx1 - zx0, zy1 - zy0)
      val (zzx0, zzx1, zzy0, zzy1) = (zx0 + x0 * dX, zx0 + x1 * dX, zy0 + y0 * dY, zy0 + y1 * dY)
      set_zoom(zzx0, zzx1, zzy0, zzy1)
    }
    active = false
  }
  /**
   * capture a mouse clickes
   */
  override def mous_clckd = {
    val (xc, yc) = project(p.mouseX, p.mouseY)
    l_clck(xc, yc)
  }

  /**
   * accommodate movement by some amount
   * @param orient orientation: true if the move is to be horizontal false if vertical
   * @param dir direction: true if positive along the given axis false if negative
   * @param d delta as a fraction of current window size
   */
  def mv(orient: Boolean, dir: Boolean, d: Float) = {
    val (dzx, dzy) = (zx1 - zx0, zy1 - zy0)
    val (ddzx, ddzy) = (dzx * d, dzy * d)
    val (zzx0, zzx1, zzy0, zzy1) =
      // horizontal shift
      if (orient)
        if (dir) (zx0 - ddzx, zx1 - ddzx, zy0, zy1)
        else (zx0 + ddzx, zx1 + ddzx, zy0, zy1)
      // vertical shift 
      else if (dir) (zx0, zx1, zy0 - ddzy, zy1 - ddzy)
      else (zx0, zx1, zy0 + ddzy, zy1 + ddzy)

    if ((zzx0 + 0.00001f < zzx1) && (zzy0 + 0.00001f < zzy1) && (zzx0 > -1 + ddzx) && (zzx1 < 2 - ddzx) && (zzy0 > -2 - ddzy) && (zzy1 < 2 + ddzy))
      set_zoom(zzx0, zzx1, zzy0, zzy1)
  }

  /**
   * zoom in and out by some fraction
   */
  def zm(dir: Boolean, d: Float) = {
    val (dzx, dzy) = (zx1 - zx0, zy1 - zy0)
    val (ddzx, ddzy) = (dzx * d, dzy * d)
    val (zzx0, zzx1, zzy0, zzy1) =
      // horizontal shift
      if (dir)
        (zx0 + ddzx, zx1 - ddzx, zy0 + ddzy, zy1 - ddzy)
      else
        (zx0 - ddzx, zx1 + ddzx, zy0 - ddzy, zy1 + ddzy)

    if ((zzx0 + 0.00001f < zzx1) && (zzy0 + 0.00001f < zzy1) && (zzx0 > -1 + ddzx) && (zzx1 < 2 - ddzx) && (zzy0 > -2 - ddzy) && (zzy1 < 2 + ddzy))
      set_zoom(zzx0, zzx1, zzy0, zzy1)
  }

  /**
   * mouse event handler
   * TODO:>> Test this!! - it has been rewriten but not tested.
   */
  override def mous_wheel(dir_in: Boolean) = {
    // caluclate the current state of focus and rate of change
    if (ctrl_dwn)
      if (dir_in) mv(true, false, 0.2f)
      else mv(true, true, 0.2f)
    // vertical shift 
    else if (shft_dwn)
      if (dir_in) mv(false, false, 0.2f)
      else mv(false, true, 0.2f)
    // zoom in/out
    else if (dir_in) zm(true, 0.2f)
    else zm(false, 0.2f)
  }
  /**
   * key event handler
   */
  override def key_up(k: Int) = {
    super.key_up(k)

    if (ctrl_dwn && k == '1') set_zoom(-0.025f, 1.025f, -0.05f, 1.05f)
    else if (shft_dwn && ctrl_dwn && k == '=') zm(true, 0.5f)
    else if (shft_dwn && ctrl_dwn && k == '-') zm(false, 0.5f)
    else if (ctrl_dwn && k == '=') zm(true, 0.2f)
    else if (ctrl_dwn && k == '-') zm(false, 0.2f)
    else if (k == '=') zm(true, 0.1f)
    else if (k == '-') zm(false, 0.1f)
    else if (ctrl_dwn && k == 90) und_zoom()
    else if (shft_dwn && ctrl_dwn && k == 37) mv(true, false, 0.1f) // arrow left
    else if (shft_dwn && ctrl_dwn && k == 38) mv(false, true, 0.1f) // arrow up
    else if (shft_dwn && ctrl_dwn && k == 39) mv(true, true, 0.1f) // arrow right
    else if (shft_dwn && ctrl_dwn && k == 40) mv(false, false, 0.1f) // arrow down
    else if (ctrl_dwn && k == 37) mv(true, false, 0.05f) // arrow left
    else if (ctrl_dwn && k == 38) mv(false, true, 0.05f) // arrow up
    else if (ctrl_dwn && k == 39) mv(true, true, 0.05f) // arrow right
    else if (ctrl_dwn && k == 40) mv(false, false, 0.05f) // arrow down
    else if (shft_dwn && k == 37) mv(true, false, 0.01f) // arrow left
    else if (shft_dwn && k == 38) mv(false, true, 0.01f) // arrow up
    else if (shft_dwn && k == 39) mv(true, true, 0.01f) // arrow right
    else if (shft_dwn && k == 40) mv(false, false, 0.01f) // arrow down
    else if (k == 37) mv(true, false, 0.0025f) // arrow left
    else if (k == 38) mv(false, true, 0.0025f) // arrow up
    else if (k == 39) mv(true, true, 0.0025f) // arrow right
    else if (k == 40) mv(false, false, 0.0025f) // arrow down
  }

  override def draw_impl = if (visible || D) {
    val (x0_o, y0_o) = (xoff(), yoff())
    val (x1_o, y1_o) = (x0_o + w(), y0_o + h())
    val (x0_i, y0_i) = (min(clck_pt0._1, clck_pt1._1), min(clck_pt0._2, clck_pt1._2))
    val (x1_i, y1_i) = (max(clck_pt0._1, clck_pt1._1), max(clck_pt0._2, clck_pt1._2))

    if (visible && active) {
      p.noStroke
      p.fill(clr_frm)
      p.quad(x0_o, y0_o, x1_o, y0_o, x1_o, y0_i, x0_o, y0_i)
      p.quad(x0_o, y0_i, x0_i, y0_i, x0_i, y1_i, x0_o, y1_i)
      p.quad(x0_o, y1_i, x1_o, y1_i, x1_o, y1_o, x0_o, y1_o)
      p.quad(x1_i, y0_i, x1_o, y0_i, x1_o, y1_i, x1_i, y1_i)
      p.noFill
      p.stroke(clr_box)
      p.strokeWeight(frm_lne_wgth)
      p.quad(x0_i, y0_i, x1_i, y0_i, x1_i, y1_i, x0_i, y1_i)
    }

    if (D) {
      p.fill(0x99, 0xbb, 0x66)
      p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
      p.text("mouse pad in debug mode", x0_o + 5, y0_o + 60)
      p.stroke(0xbb, 0xcc, 0x44)
      p.line(x0_o, y0_o, x1_o, y1_o)
      p.line(x0_o, y0_o, x1_o, y0_o)
      p.line(x1_o, y0_o, x1_o, y1_o)
      p.line(x1_o, y1_o, x0_o, y1_o)
      p.line(x0_o, y1_o, x0_o, y0_o)
      if (active) {
        p.line(x0_i, y0_i, x1_i, y0_i)
        p.line(x1_i, y0_i, x1_i, y1_i)
        p.line(x1_i, y1_i, x0_i, y1_i)
        p.line(x0_i, y1_i, x0_i, y0_i)
        p.line(x0_i, y1_i, x1_i, y0_i)
      }
    }
  }
}
