package ik.viz

import ik.util.ui.clr
import processing.core.PConstants

/**
 * trait describing shape of a point
 */
abstract class llbl(sktch: p_sktch, txt: String, sz: Float, angl: Int) extends p_drawn(sktch) {
  val x: () => Float
  val y: () => Float
  val txt_xoff = (0f /: txt) { (off, char) => off + (if (char.isUpper) sz / 3.2f else sz / 4f) }

  override def draw_impl = {
    p.textSize(sz);
    p.fill(0xff000022)
    p.smooth(2)
    if (angl != 0f) {
      p.pushMatrix
      p.rotate(-PConstants.PI * angl / 180f)
      p.text(txt, -y(), x())
      p.popMatrix
    } else {
      p.text(txt, x() + txt_xoff, y())
    }
    p.textSize(11);
  }
}

/**
 *  label catering for multiple lines
 *  not handling non-zero angles or justification yet
 */
abstract class txt_bx(sktch: p_sktch, txt: Array[String], sz: Array[Float], clr_txt: clr = clr(0xff000022), clr_bx: clr = clr(0xffcccccc), jstfy: Int = 0, angl: Int = 0) extends p_drawn(sktch) {
  val x: () => Float
  val y: () => Float

  if (txt.size != sz.size) throw new p_err("error. pass a line height for every line. " + txt.size + " lines passed but " + sz.size + " line heights passed.")
  val wdths = (0 until txt.size).toArray map { i => p.textSize(sz(i)); p.textWidth(txt(i)) }
  val y_off = (0 until txt.size).toArray map { i => if (i > 0) sz.slice(1, i + 1).sum + sz(i) / 4 else 0 }
  val w_mx = wdths.max
  val mrgn = 10

  override def draw_impl = {
    if (txt.size > 0) {
      val (xx, yy, ww, hh) = (x() - mrgn, y() - sz(0) - mrgn / 2, w_mx + 2 * mrgn, sz.sum + 2 * mrgn)

      p.fill(clr_bx.c)
      p.rect(xx, yy, ww, hh)
      p.fill(clr_txt.c)
      (0 until txt.size) foreach { i =>
        p.textAlign(0)
        p.textSize(sz(i));
        p.text(txt(i), x(), y() + y_off(i))
      }
      p.textSize(11);
    }
  }
}
