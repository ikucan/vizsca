package ik.viz.dsl

import java.awt.Dimension
import java.awt.Point
import scala.math._
import scala.swing.Frame
import ik.viz._
import javax.swing.JFrame
import ik.util.ui.clr
import java.text.SimpleDateFormat
import java.awt.Font
import scala.swing.Panel
import scala.swing.BorderPanel

import ik.util.vctr._
import java.text.DecimalFormat

//class plt(val frume: Frame, val pnl: Panel) extends plt_hlpr {
class plt extends plt_hlpr {

  // the main 
  val sktch = dflt_sktch

  // the grid
  val plots = new plot_lst {
    chngd = () => {
      scn
      zoom(-0.05, 1.05, -0.05, 1.05)
    }
  }

  // margin area around the plot
  val frm = Option(mk_dflt_frm)

  // the grid
  val grd = Option(mk_dflt_grd)

  // axes (fars, may be changed
  var x_ax = mk_dflt_x_axs
  var y_ax = mk_dflt_y_axs

  val mous_pd = mk_mous_pd

  sktch.init

  def +(nf: ftr) = {
    nf match {
      case ln: lne        => add_lne(ln)
      case lf: lne_foo    => add_lne(lf)
      case sc: sctr2      => add_sctr(sc)
      case lb: lbls2      => add_lbls(lb)
      case vl: vln        => add_vln(vl)
      case vf: vln_foo    => add_vln(vf)
      case hl: hln        => add_hln(hl)
      case ls: ln_seg_foo => add_ln_seg(ls)
      case vs: vsg        => add_vsg(vs)
      case hs: hsg        => add_hsg(hs)
      case tc: tck        => add_tck(tc)
      case tf: tck_foo    => add_tck(tf)
      case as: a_seg_foo  => add_a_seg(as)
      case hm: ht_foo     => add_hm(hm)
      case xx: x_axs      => set_x_axs(xx)
      case yy: y_axs      => set_y_axs(yy)
      case t: ttl         => set_ttl(t)
      case x: x_lbl       => set_x_lbl(x)
      case y: y_lbl       => set_y_lbl(y)
      case tb: tbx        => set_tbx(tb)
      case m: wmrk        => add_wmrk(m)
      case g: grd         => set_grd(g)
      case d: dbg         => sktch.prts foreach { p => p.D = d.d }
      case sv: sve        => sv_plt(sv)
      case c: cls         => cls_plt
      case _              =>
    }
    rdrw
    this
  }

  def -(nf: ftr) = {
    nf match {
      case m: wmrk => rm_wmrk
      case d: dbg  => sktch.prts foreach { p => p.D = false }
      case _       =>
    }
    rdrw
    this
  }

}

object plt {

  def apply(): plt = apply(10, 10, 500, 400)

  def apply(x: Int, y: Int, w: Int, h: Int): plt = {
    val pnl = new BorderPanel() {}
    val f = new Frame {
      title = "viz plot"
      visible = true
      size = new Dimension(w, h)
      location = new Point(x, y)
      peer.setMinimumSize(new Dimension(w, h))
      peer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      peer.setContentPane(pnl.peer)
    }
    new plt {
      try {
        pnl.peer.add(sktch)
      } catch {
        case x: Throwable => // ignore. the check post addition check in scala swing checks for an added component to be a JComponnet not an awt.Component //x.printStackTrace
      }
      f.size = new Dimension(w + 1, h)
    }
  }

  def apply(pnl: Panel): plt =
    new plt {
      try {
        pnl.peer.add(sktch)
      } catch {
        case x: Throwable => //x.printStackTrace// ignore. the check in scala swing 
      }
    }

}

/**
 * features of the dsl. constructs to be used at high level for manipulating the plot
 */
trait ftr
/**
 * watermark feature
 */
case class wmrk(clr: Int = 0xe3e0e0) extends ftr

/**
 * save feature. save the plot file
 */
case class sve(val fn: String, val tw: Long = 0) extends ftr
/**
 * close feature. destroy the plot and release all resources
 */
case class cls() extends ftr

/**
 * location feature. will move the plot
 */
case class grd(val x_on: Boolean, val y_on: Boolean) extends ftr

/**
 * debug feature. will move the plot
 */
case class dbg(val d: Boolean) extends ftr
/**
 * plot helper types. the line graph type
 */
case class lne(val x: Array[Double], val y: Array[Double], val ln: ln_typ, val strk: clr, val fill: clr, val sz: Float) extends ftr
case class lne_foo(val f: foo, val ln: ln_typ, val strk: clr, val fill: clr, val sz: Float) extends ftr
object lne {
  def apply(x: Array[Double], y: Array[Double], strk: clr = clr.blu, fill: clr = clr.grn, typ: ln_typ = ln_typ.-, sz: Float = 1) = new lne(x, y, typ, strk, fill, sz)
  def apply(f: foo, strk: clr, sz: Float) = new lne_foo(f, ln_typ.-, strk, strk, sz)
  def apply(f: foo, strk: clr, typ: ln_typ, sz: Float) = new lne_foo(f, typ, strk, strk, sz)
  def apply(f: foo, strk: clr, fill: clr, typ: ln_typ, sz: Float) = new lne_foo(f, typ, strk, fill, sz)
}
case class tck(val x: Array[Double], val y: Array[Double], val ln: ln_typ, val strk: clr, val fill: clr, val sz: Float) extends ftr
case class tck_foo(val f: foo, val ln: ln_typ, val strk: clr, val fill: clr, val sz: Float) extends ftr
object tck {
  def apply(x: Array[Double], y: Array[Double], strk: clr = clr.blu, fill: clr = clr.grn, typ: ln_typ = ln_typ.-, sz: Float = 1) = new tck(x, y, typ, strk, fill, sz)
  def apply(f: foo, strk: clr, sz: Float) = new tck_foo(f, ln_typ.-, strk, strk, sz)
  def apply(f: foo, strk: clr, typ: ln_typ, sz: Float) = new tck_foo(f, typ, strk, strk, sz)
  def apply(f: foo, strk: clr, fill: clr, typ: ln_typ, sz: Float) = new tck_foo(f, typ, strk, fill, sz)
}

/**
 * scatter
 * encapsulation classes for different cases of parametrisation
 * this is painful but all overloaded constructors provided for user's convenience...
 */
case class sctr2(val x: Array[Double], val y: Array[Double], val shp: Array[shp2], val strk: Array[clr], val fill: Array[clr], val sz: Array[Float], val ln_sz: Array[Float]) extends ftr
object sctr2 {
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: Array[clr], shp: Array[shp2], sz: Array[Float], ln_sz: Array[Float]) = new sctr2(x, y, shp, strk, fll, sz, ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: Array[clr], shp: Array[shp2], sz: Array[Float], ln_sz: Float) = new sctr2(x, y, shp, strk, fll, sz, Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: Array[clr], shp: Array[shp2], sz: Float, ln_sz: Array[Float]) = new sctr2(x, y, shp, strk, fll, Array(sz), ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: Array[clr], shp: Array[shp2], sz: Float, ln_sz: Float) = new sctr2(x, y, shp, strk, fll, Array(sz), Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: Array[clr], shp: shp2, sz: Array[Float], ln_sz: Array[Float]) = new sctr2(x, y, Array(shp), strk, fll, sz, ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: Array[clr], shp: shp2, sz: Array[Float], ln_sz: Float) = new sctr2(x, y, Array(shp), strk, fll, sz, Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: Array[clr], shp: shp2, sz: Float, ln_sz: Array[Float]) = new sctr2(x, y, Array(shp), strk, fll, Array(sz), ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: Array[clr], shp: shp2, sz: Float, ln_sz: Float) = new sctr2(x, y, Array(shp), strk, fll, Array(sz), Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: clr, shp: Array[shp2], sz: Array[Float], ln_sz: Array[Float]) = new sctr2(x, y, shp, strk, Array(fll), sz, ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: clr, shp: Array[shp2], sz: Array[Float], ln_sz: Float) = new sctr2(x, y, shp, strk, Array(fll), sz, Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: clr, shp: Array[shp2], sz: Float, ln_sz: Array[Float]) = new sctr2(x, y, shp, strk, Array(fll), Array(sz), ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: clr, shp: Array[shp2], sz: Float, ln_sz: Float) = new sctr2(x, y, shp, strk, Array(fll), Array(sz), Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: clr, shp: shp2, sz: Array[Float], ln_sz: Array[Float]) = new sctr2(x, y, Array(shp), strk, Array(fll), sz, ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: clr, shp: shp2, sz: Array[Float], ln_sz: Float) = new sctr2(x, y, Array(shp), strk, Array(fll), sz, Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: clr, shp: shp2, sz: Float, ln_sz: Array[Float]) = new sctr2(x, y, Array(shp), strk, Array(fll), Array(sz), ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: Array[clr], fll: clr, shp: shp2, sz: Float, ln_sz: Float) = new sctr2(x, y, Array(shp), strk, Array(fll), Array(sz), Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: Array[clr], shp: Array[shp2], sz: Array[Float], ln_sz: Array[Float]) = new sctr2(x, y, shp, Array(strk), fll, sz, ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: Array[clr], shp: Array[shp2], sz: Array[Float], ln_sz: Float) = new sctr2(x, y, shp, Array(strk), fll, sz, Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: Array[clr], shp: Array[shp2], sz: Float, ln_sz: Array[Float]) = new sctr2(x, y, shp, Array(strk), fll, Array(sz), ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: Array[clr], shp: Array[shp2], sz: Float, ln_sz: Float) = new sctr2(x, y, shp, Array(strk), fll, Array(sz), Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: Array[clr], shp: shp2, sz: Array[Float], ln_sz: Array[Float]) = new sctr2(x, y, Array(shp), Array(strk), fll, sz, ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: Array[clr], shp: shp2, sz: Array[Float], ln_sz: Float) = new sctr2(x, y, Array(shp), Array(strk), fll, sz, Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: Array[clr], shp: shp2, sz: Float, ln_sz: Array[Float]) = new sctr2(x, y, Array(shp), Array(strk), fll, Array(sz), ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: Array[clr], shp: shp2, sz: Float, ln_sz: Float) = new sctr2(x, y, Array(shp), Array(strk), fll, Array(sz), Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: clr, shp: Array[shp2], sz: Array[Float], ln_sz: Array[Float]) = new sctr2(x, y, shp, Array(strk), Array(fll), sz, ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: clr, shp: Array[shp2], sz: Array[Float], ln_sz: Float) = new sctr2(x, y, shp, Array(strk), Array(fll), sz, Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: clr, shp: Array[shp2], sz: Float, ln_sz: Array[Float]) = new sctr2(x, y, shp, Array(strk), Array(fll), Array(sz), ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: clr, shp: Array[shp2], sz: Float, ln_sz: Float) = new sctr2(x, y, shp, Array(strk), Array(fll), Array(sz), Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: clr, shp: shp2, sz: Array[Float], ln_sz: Array[Float]) = new sctr2(x, y, Array(shp), Array(strk), Array(fll), sz, ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: clr, shp: shp2, sz: Array[Float], ln_sz: Float) = new sctr2(x, y, Array(shp), Array(strk), Array(fll), sz, Array(ln_sz))
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: clr, shp: shp2, sz: Float, ln_sz: Array[Float]) = new sctr2(x, y, Array(shp), Array(strk), Array(fll), Array(sz), ln_sz)
  def apply(x: Array[Double], y: Array[Double], strk: clr, fll: clr, shp: shp2, sz: Float, ln_sz: Float) = new sctr2(x, y, Array(shp), Array(strk), Array(fll), Array(sz), Array(ln_sz))
}

/**
 * scatter
 * encapsulation classes for different cases of parametrisation
 */
case class lbls2(val x: Array[Double], val y: Array[Double], val l: Array[String], val sz: Array[Int], val xoff: Array[Int], val yoff: Array[Int], val clr: Array[clr], fnt: String) extends ftr

object lbls {
  def apply(x: Array[Double], y: Array[Double], l: Array[String], sz: Array[Int], xoff: Array[Int], yoff: Array[Int], clr: Array[clr], fnt: String) = new lbls2(x, y, l, sz, xoff, yoff, clr, fnt)
  def apply(x: Array[Double], y: Array[Double], l: Array[String], sz: Int, xoff: Int = 0, yoff: Int = 0, clr: clr = 0, fnt: String = "Arial") = new lbls2(x, y, l, Array(sz), Array(xoff), Array(yoff), Array(clr), fnt)
}

/**
 * plot helper types. the line graph type
 */
case class vsg(val x: Array[Double], val y0: Array[Double], val y1: Array[Double], val ln: ln_typ, val strk: clr, val fill: clr, val sz: Float) extends ftr
object vsg {
  def apply(x: Array[Double], y0: Array[Double], y1: Array[Double], strk: clr = clr.blu, fill: clr = clr.grn, typ: ln_typ = ln_typ.-, sz: Float = 1) = new vsg(x, y0, y1, typ, strk, fill, sz)
}
case class hsg(val y: Array[Double], val x0: Array[Double], val x1: Array[Double], val ln: ln_typ, val strk: clr, val fill: clr, val sz: Float) extends ftr
object hsg {
  def apply(y: Array[Double], x0: Array[Double], x1: Array[Double], strk: clr = clr.blu, fill: clr = clr.grn, typ: ln_typ = ln_typ.-, sz: Float = 1) = new hsg(y, x0, x1, typ, strk, fill, sz)
}
/**
 * area segments: draw a rectangle using f1(i) and f2(i) as diagonally oposite vertices
 */
case class ln_seg_foo(val f1: foo, val f2: foo, val strk: clr, val fill: clr, val ln: ln_typ, val sz: Float) extends ftr
object ln_seg {
  def apply(f1: foo, f2: foo, strk: clr, typ: ln_typ, sz: Float) = new ln_seg_foo(f1, f2, strk, strk, typ, sz)
  def apply(x0: Array[Double], y0: Array[Double], x1: Array[Double], y1: Array[Double], strk: clr = clr.blu, fill: clr = clr.grn, typ: ln_typ = ln_typ.-, sz: Float = 1) = new ln_seg_foo(foo(x0, y0), foo(x1, y1), strk, fill, typ, sz)
  //@deprecated
  //def hrz(y: Array[Double], x0: Array[Double], x1: Array[Double], strk: clr = clr.blu, fill: clr = clr.grn, typ: ln_typ = ln_typ.-, sz: Float = 1) = new ln_seg_foo(foo(x0, y), foo(x1, y), strk, fill, typ, sz)
  //@deprecated
  //def vrt(x: Array[Double], y0: Array[Double], y1: Array[Double], strk: clr = clr.blu, fill: clr = clr.grn, typ: ln_typ = ln_typ.-, sz: Float = 1) = new ln_seg_foo(foo(x, y0), foo(x, y1), strk, fill, typ, sz)
}

/**
 * area segments: draw a rectangle using f1(i) and f2(i) as diagonally oposite vertices
 */
case class a_seg_foo(val f1: foo, val f2: foo, val strk: clr, val fill: clr, val sz: Float) extends ftr
object a_seg {
  def apply(f1: foo, f2: foo, strk: clr) = new a_seg_foo(f1, f2, strk, strk, 1)
  def apply(x1: Array[Double], y1: Array[Double], x2: Array[Double], y2: Array[Double], strk: clr) = new a_seg_foo(foo(x1, y1), foo(x2, y2), strk, strk, 1)
  def apply(f1: foo, f2: foo, strk: clr, sz: Float) = new a_seg_foo(f1, f2, strk, strk, sz)
  def apply(x1: Array[Double], y1: Array[Double], x2: Array[Double], y2: Array[Double], strk: clr, sz: Float) = new a_seg_foo(foo(x1, y1), foo(x2, y2), strk, strk, sz)
}

case class vln(val x: Array[Double], val ln: ln_typ, val strk: clr, val fill: clr, val sz: Float) extends ftr
case class vln_foo(val f: foo, val ln: ln_typ, val strk: clr, val fill: clr, val sz: Float) extends ftr
object vln {
  def apply(x: Array[Double], strk: clr = clr.blu, fill: clr = clr.grn, typ: ln_typ = ln_typ.-, sz: Float = 1) = new vln(x, typ, strk, fill, sz)
  def apply(f: foo, strk: clr, typ: ln_typ, sz: Float) = new vln_foo(f, ln_typ.-, strk, strk, sz)
  def apply(f: foo, strk: clr, fill: clr, typ: ln_typ, sz: Float) = new vln_foo(f, typ, strk, fill, sz)
}

case class hln(val y: Array[Double], val ln: ln_typ, val strk: clr, val fill: clr, val sz: Float) extends ftr
//case class hln_foo(val f: foo, val ln: ln_typ, val strk: clr, val fill: clr, val sz: Float) extends ftr
object hln {
  def apply(y: Array[Double], strk: clr = clr.blu, fill: clr = clr.grn, typ: ln_typ = ln_typ.-, sz: Float = 1) = new hln(y, typ, strk, fill, sz)
}

case class ht_foo(val f: dscrt_foo2d_tle, strk: clr = clr.blu, ln_sz: Float, lt: ln_typ, txt: Boolean, var cbck: (Int, Int) => Unit = (_, _) => {}) extends ftr
object heat {
  def apply(f: Array[Array[Double]], strk: clr = clr.wht, ln_sz: Float = 1f, lt: ln_typ = ln_typ.-, txt: Boolean = false) = new ht_foo(new dscrt_foo2d_tle(f), strk, ln_sz, lt, txt)
  def apply(x: Array[String], y: Array[String], z: Array[Array[Double]]) = new ht_foo(new dscrt_foo2d_tle(x, y, z), clr.wht, 1f, ln_typ.-, true)
  def apply(x: Array[Double], y: Array[Double], z: Array[Array[Double]]) = new ht_foo(new dscrt_foo2d_tle(x map (_.toString), y map (_.toString), z), clr.wht, 1f, ln_typ.-, true)
  def apply(x: Array[Double], y: Array[Double], z: Array[Array[Double]], cbck: (Int, Int) => Unit) = new ht_foo(new dscrt_foo2d_tle(x map (_.toString), y map (_.toString), z), clr.wht, 1f, ln_typ.-, true, cbck)
  def apply(x: Array[Double], y: Array[Double], z: Array[Double], cbck: (Int, Int) => Unit) = {
    val xy_idx = (0 until x.size) map (i => (x(i) -> y(i)) -> i) toMap
    val (xs, ys) = (x.dstnct, y.dstnct)
    val z_2d = xs map { x => ys map { y => z(xy_idx(x -> y)) } }
    new ht_foo(new dscrt_foo2d_tle(xs map (_.toString), ys map (_.toString), z_2d), clr.wht, 1f, ln_typ.-, true, cbck)
  }
  //TODO:>> elliminate common functionality
  def apply(x: Array[Double], y: Array[Double], z: Array[Double]) = {
    val xy_idx = (0 until x.size) map (i => (x(i) -> y(i)) -> i) toMap
    val (xs, ys) = (x.dstnct, y.dstnct)
    val z_2d = xs map { x => ys map { y => z(xy_idx(x -> y)) } }
    new ht_foo(new dscrt_foo2d_tle(xs map (_.toString), ys map (_.toString), z_2d), clr.wht, 1f, ln_typ.-, true)
  }
}

/**
 * axis formatter objects
 */
class x_axs(val fnt_sz: Option[() => Int] = None, val frmt: Option[(Double) => String]) extends ftr
object x_axs {
  def apply(frmt: (Double) => String) = new x_axs(frmt = Some(frmt))
  def apply(sz: Int, frmt: String) = new x_axs(fnt_sz = Some(() => sz), frmt = Some(new DecimalFormat(frmt).format))
}

object x_axs_dttm {
  def apply() = new x_axs(frmt = Some(new SimpleDateFormat("HH:mm:ss.SSS\nyyyy.MM.dd").format))
  def apply(sz: Int) = new x_axs(fnt_sz = Some(() => sz), frmt = Some(new SimpleDateFormat("HH:mm:ss.SSS\nyyyy.MM.dd").format))
}
object x_axs_dt {
  def apply() = new x_axs(frmt = Some(new SimpleDateFormat("yyyy.MM.dd").format))
  def apply(sz: Int) = new x_axs(fnt_sz = Some(() => sz), frmt = Some(new SimpleDateFormat("yyyy.MM.dd").format))
}
object x_axs_tm {
  def apply() = new x_axs(frmt = Some(new SimpleDateFormat("HH:mm:ss.SSS").format))
  def apply(sz: Int) = new x_axs(fnt_sz = Some(() => sz), frmt = Some(new SimpleDateFormat("HH:mm:ss.SSS").format))
  def apply(sz: Int, frmt: String) = new x_axs(fnt_sz = Some(() => sz), frmt = Some(new SimpleDateFormat(frmt).format))
}
class y_axs(val fnt_sz: Option[() => Int] = None, val frmt: Option[(Double) => String]) extends ftr
object y_axs {
  def apply(frmt: (Double) => String) = new y_axs(frmt = Some(frmt))
  def apply(frmt: String) = new y_axs(frmt = Some(new DecimalFormat(frmt).format))
  def apply(sz: Int, frmt: String) = new y_axs(fnt_sz = Some(() => sz), frmt = Some(new DecimalFormat(frmt).format))
}

class lbl(val txt: String, val angl: Int, val sz: Int) extends ftr

class ttl(txt: String, angl: Int, sz: Int) extends lbl(txt, angl, sz)
object ttl {
  def apply(t: String, sz: Int = 20) = new ttl(t, 0, sz)
}
class x_lbl(txt: String, angl: Int, sz: Int) extends lbl(txt, angl, sz)
object x_lbl {
  def apply(t: String) = new x_lbl(t, 0, 16)
}
class y_lbl(txt: String, angl: Int, sz: Int) extends lbl(txt, angl, sz)
object y_lbl {
  def apply(t: String) = new y_lbl(t, 90, 16)
}

class tbx(val lns: Array[String], val sz: Array[Float]) extends ftr
object tbx {
  def apply(t: Array[String]): tbx = apply(t, 16)
  def apply(t: Array[String], sz: Int): tbx = apply(t, t map (x => sz))
  def apply(t: Array[String], sz: Array[Int]): tbx = new tbx(t, sz map (_.toFloat))
}

/**
 * implicit conversion helper functions
 */
object implicits {
  implicit def c1(d: Array[Int]): Array[Double] = d map (_.toDouble)
  implicit def c2(d: Array[Long]): Array[Double] = d map (_.toDouble)
  implicit def c3(d: Array[Float]): Array[Double] = d map (_.toDouble)
}
