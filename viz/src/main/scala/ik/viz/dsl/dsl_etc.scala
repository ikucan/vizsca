package ik.viz.dsl

import java.awt.image.BufferedImage

import scala.math.min

import ik.util.ui.clr
import ik.viz.axis
import ik.viz.axxis
import ik.viz.dscrt_foo
import ik.viz.grid
import ik.viz.line_grph
import ik.viz.llbl
import ik.viz.p_area
import ik.viz.p_err
import ik.viz.p_sktch
import ik.viz.seg_area
import ik.viz.seg_plt
import ik.viz.tick_grph
import ik.viz.tle_grph
import ik.viz.vctr_foo
import ik.viz.vlne_plt
import ik.viz.vw_prt
import ik.viz.wtr_mrk
import ik.viz.zzoom_pad
import ik.viz._

class plt_hlpr {
  self: plt =>

  class plt_sktch extends p_sktch {
    var mrgn = () => min(h / 10, 60f)
    var ax_mrgn = () => min(h / 5, 100f)
    var ax_gap = () => 10f
    var yy: Boolean = false
    var w_mrk: Option[wtr_mrk] = None
    override def on_rsz = rdrw

    override def key_up(k: Int) = {
      super.key_up(k)
      if (ctrl_dwn && k == 67) ik.util.img.img2clp(get().getNative().asInstanceOf[BufferedImage])
      else if (ctrl_dwn && k == 83) ik.util.img.sv(get().getNative().asInstanceOf[BufferedImage], this.frame)
      else {
        //println(k)
      }
    }
  }

  def dflt_sktch = new plt_sktch {}

  // margin area around the plot
  def mk_dflt_frm = new vw_prt(sktch) {
    xoff = p.xoff
    yoff = p.yoff
    x_prt = sktch.ax_mrgn
    y_prt = sktch.mrgn
    clr_frm = p.color(0xe0, 0xd7, 0xe0, 0x7f)
    w_prt = () => w() - (sktch.ax_mrgn() + sktch.mrgn())
    h_prt = () => h() - (y_prt() + sktch.ax_mrgn())
  }

  // the grid
  def mk_dflt_grd = new grid(sktch) { set_drwbl_box(this) }

  // mouse pad
  def mk_mous_pd = new zzoom_pad(sktch) {
    set_drwbl_box(this)
    zoom = plots.zoom
    l_clck = (x: Double, y: Double) => plots.clck(x, y)

  }

  // the x axis
  def mk_dflt_x_axs = new axis(sktch) {
    val (dxoff, dyoff, dw, dh) = drwbl_box
    xoff = dxoff
    yoff = () => dyoff() + dh() + sktch.ax_gap()
    len = frm.get.w_prt
    mjr_tck = () =>
      grd match {
        case Some(g) => g.n_vrt() + 2
        case _       => (dw() / 100).toInt
      }
    smth = true
    lbls = plots.x_tcks
  }

  // xxaxis. heatmap axis
  def mk_dflt_xx_axs = new axxis(sktch) {
    val (dxoff, dyoff, dw, dh) = drwbl_box
    xoff = dxoff
    yoff = () => dyoff() + dh() + sktch.ax_gap()
    len = frm.get.w_prt
    mjr_tck = () =>
      grd match {
        case Some(g) => g.n_vrt() + 2
        case _       => (dw() / 100).toInt
      }
    smth = true
  }
  //
  def mk_dflt_y_axs = new axis(sktch) {
    vrtcl = true
    //    xoff = () => grd.get.xoff() - sktch.ax_gap()
    //    yoff = () => frm.get.yoff() + frm.get.y_prt() + frm.get.h_prt()
    val (dxoff, dyoff, dw, dh) = drwbl_box
    xoff = () => dxoff() - sktch.ax_gap()
    yoff = () => dyoff() + dh()

    len = frm.get.h_prt
    mjr_tck = () =>
      grd match {
        case Some(g) => g.n_hrz() + 2
        case _       => (dh() / 100).toInt
      }
    smth = true
    lbls = plots.y_tcks
  }
  def mk_dflt_yy_axs = new axxis(sktch) {
    vrtcl = true
    val (dxoff, dyoff, dw, dh) = drwbl_box
    xoff = () => dxoff() - sktch.ax_gap()
    yoff = () => dyoff() + dh()

    len = frm.get.h_prt
    mjr_tck = () =>
      grd match {
        case Some(g) => g.n_hrz() + 2
        case _       => (dh() / 100).toInt
      }
    smth = true
  }

  protected def rdrw() = if (mous_pd != null) plots.zoom(mous_pd.zx0, mous_pd.zx1, mous_pd.zy0, mous_pd.zy1)

  protected def add_wmrk(m: wmrk) = {
    rm_wmrk
    sktch.w_mrk = Some(new wtr_mrk(sktch))
  }

  protected def sv_plt(s: sve) = {
    Thread.sleep(s.tw * 1000l)
    //sktch.tsks += { () => sktch.stop; sktch.save(s.fn); sktch.start }
    sktch.tsks add { () => sktch.stop; sktch.save(s.fn); sktch.start }
  }
  protected def cls_plt = {
    //sktch.tsks += { () => sktch.stop; sktch.destroy }
    sktch.tsks add { () => sktch.stop; sktch.destroy }
  }

  protected def rm_wmrk =
    sktch.w_mrk match {
      case Some(wm) => wm.dtch
      case None     =>
    }

  def add_lne(ln: lne) = {
    val foo = new dscrt_foo(ln.x, ln.y)
    plots += new line_grph(sktch, foo, ln.ln, ln.strk, ln.fill, ln.sz) {
      set_drwbl_box(this)
    }
  }
  def add_lne(ln: lne_foo) = {
    plots += new line_grph(sktch, ln.f, ln.ln, ln.strk, ln.fill, ln.sz) {
      set_drwbl_box(this)
    }
  }
  def add_tck(ln: tck) = {
    val foo = new dscrt_foo(ln.x, ln.y)
    plots += new tick_grph(sktch, foo, ln.ln, ln.strk, ln.fill, ln.sz) {
      set_drwbl_box(this)
    }
  }
  def add_tck(tf: tck_foo) = {
    plots += new tick_grph(sktch, tf.f, tf.ln, tf.strk, tf.fill, tf.sz) {
      set_drwbl_box(this)
    }
  }
  
  def add_sctr(sc: sctr2) = {
    val foo = new dscrt_foo(sc.x, sc.y)
    plots += new sctr_plt2(sktch, foo, sc.shp, sc.strk, sc.fill, sc.sz, sc.ln_sz) {
      set_drwbl_box(this)
    }
  }

  def add_lbls(l: lbls2) = {
    val foo = new dscrt_foo(l.x, l.y)
    //class sctr_lbls2(sktch: p_sktch, f: foo, l: Array[String], sz: Array[Float], clr: Array[clr], fnt: Array[clr]) extends p_area(sktch) with plot {
    plots += new sctr_lbls2(sktch, foo, l.l, l.sz, l.xoff, l.yoff, l.clr, l.fnt) {
      set_drwbl_box(this)
    }
  }
  def add_vsg(vl: vsg) {
    val f1 = new dscrt_foo(vl.x, vl.y0)
    val f2 = new dscrt_foo(vl.x, vl.y1)
    plots += new seg_plt(sktch, f1, f2, vl.ln, vl.strk, vl.fill, vl.sz) {
      set_drwbl_box(this)
    }
  }
  def add_hsg(hs: hsg) {
    val f1 = new dscrt_foo(hs.x0, hs.y)
    val f2 = new dscrt_foo(hs.x1, hs.y)
    plots += new seg_plt(sktch, f1, f2, hs.ln, hs.strk, hs.fill, hs.sz) {
      set_drwbl_box(this)
    }
  }
  def add_ln_seg(hs: ln_seg_foo) {
    plots += new seg_plt(sktch, hs.f1, hs.f2, hs.ln, hs.strk, hs.fill, hs.sz) {
      set_drwbl_box(this)
    }
  }
  def add_a_seg(as: a_seg_foo) = {
    plots += new seg_area(sktch, as.f1, as.f2, as.strk, as.fill, as.sz) {
      set_drwbl_box(this)
    }
  }
  def add_hm(hm: ht_foo) = {
    x_ax.dtch
    y_ax.dtch
    val tg = new tle_grph(sktch, hm.f, hm.strk, hm.ln_sz, hm.lt, hm.txt) {
      set_drwbl_box(this)
      cbck = hm.cbck
    }
    val xxs = mk_dflt_xx_axs
    val yxs = mk_dflt_yy_axs
    xxs.tck_loc = tg.x_tcks()
    yxs.tck_loc = tg.y_tcks()
    xxs.rdy_lbls = tg.x_lbls()
    yxs.rdy_lbls = tg.y_lbls()

    plots += tg
  }
  def add_vln(vl: vln) {
    val f = new vctr_foo(vl.x)
    plots += new vlne_plt(sktch, f, vl.ln, vl.strk, vl.fill, vl.sz) {
      set_drwbl_box(this)
    }
  }
  def add_vln(vl: vln_foo) {
    plots += new vlne_plt(sktch, vl.f, vl.ln, vl.strk, vl.fill, vl.sz) {
      set_drwbl_box(this)
    }
  }
  def add_hln(hl: hln) {
    val f = new vctr_foo(hl.y)
    plots += new hlne_plt(sktch, f, hl.ln, hl.strk, hl.fill, hl.sz) {
      set_drwbl_box(this)
    }
  }
  def set_x_axs(ax: x_axs) {
    ax.frmt match {
      case Some(f) => x_ax.lbl_frmt = f
      case _       =>
    }
    ax.fnt_sz match {
      case Some(fsz) =>
        x_ax.fnt_sz = fsz
        x_ax.rst
      case _ =>
    }
  }

  def set_y_axs(ax: y_axs) {
    ax.frmt match {
      case Some(f) => y_ax.lbl_frmt = f
      case _       =>
    }
    ax.fnt_sz match {
      case Some(fsz) =>
        y_ax.fnt_sz = fsz
        y_ax.rst
      case _ =>
    }
  }

  def set_ttl(t: ttl) {
    new llbl(sktch, t.txt, t.sz, t.angl) {
      override val x: () => Float = () => sktch.w / 2f
      override val y: () => Float = () => 30f
    }
  }
  def set_x_lbl(x: x_lbl) {
    new llbl(sktch, x.txt, x.sz, 0) {
      override val x: () => Float = () => sktch.w / 2f
      override val y: () => Float = () => sktch.h - 30f
    }
  }
  def set_y_lbl(y: y_lbl) {
    new llbl(sktch, y.txt, y.sz, 90) {
      override val x: () => Float = () => 30f
      override val y: () => Float = () => sktch.h / 2f
    }
  }
  def set_grd(g: grd) {
    if (!g.x_on) this.grd.get.clr_vrt = clr(0x00ffffff)()
    if (!g.y_on) this.grd.get.clr_hrz = clr(0x00ffffff)()
  }

  def set_tbx(tb: tbx) {
    new txt_bx(sktch, tb.lns, tb.sz) {
      override val x: () => Float = () => sktch.w / 2f
      override val y: () => Float = () => 50f
    }
  }

  protected def drwbl_box = {
    frm match {
      case Some(m) =>
        val xoff = () => m.x_prt() + m.xoff()
        val yoff = () => m.y_prt() + m.yoff()
        val w = frm.get.w_prt
        val h = frm.get.h_prt
        (xoff, yoff, w, h)
      case None => throw new p_err("unsupported functionality - sketches without viewport frame.")
    }
  }

  protected def set_drwbl_box(a: p_area) = {
    frm match {
      case Some(m) =>
        a.xoff = () => m.x_prt() + m.xoff()
        a.yoff = () => m.y_prt() + m.yoff()
        a.w = frm.get.w_prt
        a.h = frm.get.h_prt
      case None => throw new p_err("unsupported functionality - sketches without viewport frame.")
    }
  }

}
