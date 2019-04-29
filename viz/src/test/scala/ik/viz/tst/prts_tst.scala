//package ik.viz.tst
//
//import ik.util.loggd
//import ik.util.ui.clr
//import ik.viz._
//import scala.math._
//import org.junit.runner.RunWith
//import org.scalatest.junit.JUnitRunner
//import org.scalatest._
//import ik.viz.dsl._
//import java.util.Arrays
//import scala.swing.Frame
//import java.awt.Point
//import java.awt.Dimension
//
//@RunWith(classOf[JUnitRunner])
//class prts_test extends FlatSpec with ShouldMatchers with loggd {
//
//  "zoom pad" should "zoom, scroll and pan correctlly" in {
//
//    val v = new p_sktch {
//      xoff = () => 20f
//      yoff = () => 20f
//      new zzoom_pad(this) {
//        xoff = p.xoff
//        yoff = p.yoff
//        w = () => p.w - 2 * xoff()
//        h = () => p.h - 2 * yoff()
//
//        D = true
//      }
//    }
//
//    embed(v)
//
//    Thread.sleep(2 * 60 * 1000)
//  }
//
//  def embed(p: p_sktch) = {
//    val (x0, y0, w0, h0) = (100, 100, 800, 400)
//    val f = new Frame {
//      title = "grid test window"
//      visible = true
//      location = new Point(x0, y0)
//      size = new Dimension(w0, h0)
//      minimumSize = new Dimension(w0, h0)
//    }
//    p.init
//    f.peer.setContentPane(p)
//    f.size = new Dimension(w0, h0)
//    f.peer.doLayout
//    f.repaint
//  }
//
//  def fx = (0 until 1000).toArray map { x => x.asInstanceOf[Double] }
//  def fy1 = fx map { x => (cos(x * Pi / 180.0)) }
//  def fy2 = fx map { x => (sin(x * Pi / 360.0)) }
//}
