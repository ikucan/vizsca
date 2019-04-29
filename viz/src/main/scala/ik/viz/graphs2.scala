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
import java.text.DecimalFormat
import java.util.Arrays

/**
 * tile graph
 */
class tle_grph(sktch: p_sktch, val f: dscrt_foo2d_tle, c_strk: clr, ln_sz: Float, lt: ln_typ, txt: Boolean /*, c_fll: clr*/ ) extends p_area(sktch) with plot {
  var xyz = f.slct(N = 5000)
  var (dx, dy, plt_xoff, plt_yoff) = (.0, .0, .0, .0)
  var xx = Seq(0f, 0f)
  var yy = Seq(0f, 0f)
  var (xx_min, xx_max, yy_min, yy_max) = (0f, 0f, 0f, 0f)
  var tck_xx = Seq(0f, 0f)
  var tck_yy = Seq(0f, 0f)
  var lbl_xx = Seq("", "")
  var lbl_yy = Seq("", "")

  var cbck = (xix: Int, yix: Int) => {}

  /**
   *
   */
  override def bnds = (f.x_min, f.x_max, f.y_min, f.y_max)

  /**
   *
   */
  override def zoom(x0: Double, x1: Double, y0: Double, y1: Double) = {
    try {
      xyz = f.slct(x0 = x0, x1 = x1, y0 = y0, y1 = y1, N = 5000)
      val (x, y, z) = (xyz._1, xyz._2, xyz._3)
      dx = w().toDouble / (x1 - x0); dy = h().toDouble / (y1 - y0)
      plt_xoff = x0; plt_yoff = y0
      xx = x map { x => ((x - plt_xoff) * dx).toFloat + xoff() }
      yy = y map { y => (h() + yoff() - ((y - plt_yoff) * dy)).toFloat }
      xx_min = ((0 - plt_xoff) * dx).toFloat + xoff()
      xx_max = ((x.size - plt_xoff) * dx).toFloat + xoff()
      yy_min = (h() + yoff() - ((0 - plt_yoff) * dy)).toFloat
      yy_max = (h() + yoff() - ((y.size - plt_yoff) * dy)).toFloat

      // prepare axis lables
      val (nn_x_tcks, x_incr) = if (xx.size > n_x_tcks) (n_x_tcks, xx.size / n_x_tcks) else (xx.size - 1, 1)
      tck_xx = (0 until nn_x_tcks) map (i => (xx(i * x_incr) + xx(i * x_incr + 1)) / 2)
      lbl_xx = (0 until x.size - 1) map (i => f.x_lbl(x(i * x_incr).toInt))

      val (nn_y_tcks, y_incr) = if (yy.size > n_y_tcks) (n_y_tcks, yy.size / n_y_tcks) else (yy.size - 1, 1)
      tck_yy = (0 until nn_y_tcks) map (i => (yy(i * y_incr) + yy(i * y_incr + 1)) / 2)
      lbl_yy = (0 until nn_y_tcks) map (i => f.y_lbl(y(i * y_incr).toInt))
    } catch {
      case t: Throwable => t.printStackTrace
    }
  }

  override def clck(x: Double, y: Double) = {
    val xix = xx.toArray.lte(Array(x.toFloat))(0)
    val yix = yy.size - yy.toArray.reverse.lte(Array(y.toFloat))(0) - 2
    //println(xix, yix)
    if (xix >= 0 && yix >= 0 && xix < xx.size - 1 && yix < yy.size - 1) cbck(xix, yix)
  }

  private val n_x_tcks = 60
  private val n_y_tcks = 40
  def x_tcks(): () => Seq[Float] = () => tck_xx
  def y_tcks(): () => Seq[Float] = () => tck_yy
  def x_lbls(): () => Seq[String] = () => lbl_xx
  def y_lbls(): () => Seq[String] = () => lbl_yy

  override def x_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;
  //override def x_tcks(n: Int): Array[Double] = xyz._1;
  override def y_tcks(n: Int): Array[Double] = (0 until n) map (_.toDouble) toArray;

  // colour pallete
  private val N = 256
  private val pllt = mk_clr_plt
  private val z_incr = (f.z_max - f.z_min) / (N - 1)
  private def tle_clr(v: Double) = {
    //    // ===============================
    //    println("f min, max    :>> " + f.z_min + " -> " + f.z_max)
    //    println("f min, max    :>> " + f.z.flatMap(x => x).min + " -> " + f.z.flatMap(x => x).max)
    //    println("z incr        :>> " + z_incr)
    //    println("v             :>> " + v)
    //    println("min     index :>> " + ((f.z_min - f.z_min) / z_incr).toInt)
    //    println("max     index :>> " + ((f.z_max - f.z_min) / z_incr).toInt)
    //    println("MIN BREACH    :>> " + (v < f.z_min))
    //    println("MAX BREACH    :>> " + (v > f.z_max))
    //    println("pallete index :>> " + ((v - f.z_min) / z_incr).toInt)
    //    Thread.sleep(100)
    // ===============================
    pllt(((v - f.z_min) / z_incr).toInt)
  }
  private def mk_clr_plt = (0 until N) map (i => 0xff000000 | (i.toByte << 16).toInt | (N - 1 - i))
  //private def mk_clr_plt = (0 until N) map (i =>  0xff000000 | (N.toByte - i.toByte).toInt)

  private def mk_gs_plt = (0 until N) toArray

  override def draw_impl = if (visible || D) {
    val nf = new DecimalFormat("#.###")
    val bx = box
    if (visible) {
      p.stroke(c_strk.c)
      p.strokeWeight(ln_sz)

      var (x, y, z) = xyz

      if (x.size > 1 && y.size > 1)
        (0 until x.size - 1) foreach { ix =>
          val (x0, x1) = (max(bx._1, xx(ix)), min(bx._3, xx(ix + 1)))
          (0 until y.size - 1) foreach { iy =>
            val (y0, y1) = (min(bx._4, yy(iy)), min(bx._3, yy(iy + 1)))
            p.fill(tle_clr(z(x(ix).toInt)(y(iy).toInt).toFloat))
            p.rect(x0, y0, x1 - x0, y1 - y0)

            // if text is to be printed
            if (txt) {
              p.fill(0xffffddff)
              p.text(nf.format(z(x(ix).toInt)(y(iy).toInt)), x1 - 10, y0 - 10)
            }

          }
        }
    }
    if (D) {
      p.textAlign(PConstants.LEFT)
      p.fill(0)
      p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
      p.text("tile graph in debug mode", bx._1 + 50, bx._2 + 20)
      p.stroke(0x33, 0x55, 0x99)
      p.line(bx._1, bx._2, bx._3, bx._4)
      p.line(bx._1, bx._4, bx._3, bx._2)
    }
  }
}
