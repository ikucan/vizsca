package ik.viz

import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.util.concurrent.ConcurrentLinkedQueue

import scala.collection.JavaConverters.asScalaIterator
import scala.collection.mutable.ListBuffer
import scala.math.max
import scala.math.min

import processing.core.PApplet

/**
 * trait of all processing objects, drawn or otherwise everything has an offset
 *
 * @author iztok
 *
 */
trait p_base {
  /**
   * offset along the x axis. function. defaluts to 0
   */
  var xoff = () => 0f
  /**
   * offset along the y axis. function. defaults to 0
   */
  var yoff = () => 0f
  /**
   * debug mode, disabled by default. when true, the component is in debug mode enabled for
   */
  var D: Boolean = false // debug
  def intersects(x: Float, y: Float) = false
  def project(x: Float, y: Float) = (0f, 0f)
}

/**
 * mouseable trait. implemenotors support mouse events;
 * @author iztok
 *
 */
trait msbl {
  // mouse event sensitivity events
  def mous_prsd = {}
  def mous_rlsd = {}
  def mous_movd = {}
  def mous_drgd = {}
  def mous_clckd = {}
  def mous_wheel(in: Boolean) = {}
}

/**
 * keyable trait. implemenotors support keyboard events;
 * @author iztok
 *
 */
trait keybl {
  private var (ky, shft, ctrl, alt) = (-1, false, false, false)

  def key_dwn(k: Int) =
    if (k == 16) shft = true
    else if (k == 17) ctrl = true
    else if (k == 18) alt = true
    else ky = k

  def key_up(k: Int) =
    if (k == 16) shft = false
    else if (k == 17) ctrl = false
    else if (k == 18) alt = false
    else ky = -1

  def key_dwn = ky
  def shft_dwn = shft
  def ctrl_dwn = ctrl
  def alt_dwn = alt
}

/**
 * abstract base of all drawn objects. needs to be drawn on graphics device - a sketch
 * @author iztok
 *
 */
abstract class p_drawn(protected var p: p_sktch, z_ord: Int = 0) extends p_base with msbl with keybl {
  var smth = false
  var visible = true
  //var draw_frqncy = () => 10l

  // add this object to the sketch
  p += (this, z_ord)

  def dtch = p = null
  def draw =
    try {
      //if ((p != null) && tst_tme) draw_impl
      if ((p != null)) draw_impl
    } catch {
      case e: Throwable => //e.printStackTrace
    }
  def draw_impl

  /**
   * getter for p - member mutable so not exposed directly
   */
  def get_p = p
}

/**
 * abstract base for any drawn object which has an associated rectangular area.
 * it is abstract and its visual component does not have to be rectangular or
 * even restricted
 * @author iztok
 *
 */
abstract class p_area(pp: p_sktch, z_ord: Int = 0) extends p_drawn(pp, z_ord) with p_util {
  var w = () => p.w - xoff() - 1
  var h = () => p.h - yoff() - 1
  override def intersects(x: Float, y: Float) = (x >= xoff()) && (x <= (xoff() + w())) && (y >= yoff()) && (y <= (yoff() + h()))
  // TODO:>> project is more like trim - rename ??
  override def project(x: Float, y: Float) = (min(max(x, xoff()), xoff() + w()), min(max(y, yoff()), yoff() + h()))

}

abstract class p_sktch extends PApplet with p_base with keybl {
  val o_prts = ListBuffer[(Int, p_drawn)]()
  var prts = List[p_drawn]()
  var sz = getSize

  //val tsks = new SynchronizedQueue[() => Unit]
  val tsks = new ConcurrentLinkedQueue[() => Unit]

  //implicit ooo = new Ordering[]
  implicit val ord = new scala.math.Ordering[(Int, ik.viz.p_drawn)] {
    def compare(a: (Int, ik.viz.p_drawn), b: (Int, ik.viz.p_drawn)) = a._1 compare b._1
  }

  def +=(c: p_drawn, o: Int = 0) = {
    o_prts += o -> c
    prts = o_prts.sorted.toList map (_._2)
    //println(prts)
  }
  //def +:(c: p_drawn) = c +=: prts //prts.append(c)

  def on_rsz = {}

  override def doLayout() = {
    try {
      super.doLayout
      background(0xf7, 0xf7, 0xf7)
      sz = getSize
    } catch {
      case e: Throwable => //e.printStackTrace
    }
  }

  var draw_period = () => 10l
  private var lst_tme = System.currentTimeMillis
  override def draw() = {
    if ((System.currentTimeMillis - lst_tme) > draw_period()) {
      prts foreach { x => x.draw }

      asScalaIterator(tsks.iterator) foreach (_())

      lst_tme = System.currentTimeMillis
    }
  }
  override def mousePressed() = prts foreach { x => x.mous_prsd }
  override def mouseReleased() = prts foreach { x => x.mous_rlsd }
  override def mouseDragged() = prts foreach { x => x.mous_drgd }
  override def mouseMoved() = prts foreach { x => x.mous_movd }
  override def mouseClicked() = prts foreach { x => x.mous_clckd }

  override def keyPressed() = {
    super.keyPressed()
    prts foreach { x => x.key_dwn(keyCode) }
    this.key_dwn(keyCode)
  }
  override def keyReleased() = {
    super.keyReleased()
    prts foreach { x => x.key_up(keyCode) }
    this.key_up(keyCode)
  }

  addMouseWheelListener(new MouseWheelListener {
    override def mouseWheelMoved(e: MouseWheelEvent) = prts foreach { x => x.mous_wheel(e.getWheelRotation() == -1) }
  })

  addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = on_rsz
  })

  //override def 
  def h = sz.height.asInstanceOf[Float]
  def w = sz.width.asInstanceOf[Float]
}
