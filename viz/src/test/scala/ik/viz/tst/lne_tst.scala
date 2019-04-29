//package ik.viz.tst
//
//import scala.math.cos
//import scala.util.Random
//
//import ik.util.loggd
//import ik.util.ui.clr
//import ik.util.vctr.mk_vctr_wrppr
//import ik.viz.dsl.hsg
//import ik.viz.dsl.ln_seg
//import ik.viz.dsl.lne
//import ik.viz.dsl.plt
//import ik.viz.dsl.vsg
//import ik.viz.ln_typ
//
//object lne_test extends loggd {
//
//  def main(argv: Array[String]): Unit = {
//
//    log_dbg("1")
//    val N = 3000
//    val x = (0 until N) toArray;
//    val y = x map (x => cos(math.Pi * x / 360.0))
//
//    if (false) {
//      val p = plt(x = 100, y = 100, w = 1300, h = 1000)
//      p + hsg(Array(1f, 2f, 3f), Array(1f, 2f, 3f), Array(4f, 5f, 6f), strk = clr(0x66aa0022), typ = ln_typ.-, sz = 1)
//    }
//
//    if (false) {
//      val p = plt(x = 150, y = 150, w = 1300, h = 1000)
//      p + vsg(Array(1f, 2f, 3f), Array(1f, 2f, 3f), Array(4f, 5f, 6f), strk = clr(0x66aa0022), typ = ln_typ.-, sz = 1)
//    }
//
//    if (false) {
//      val p = plt(x = 200, y = 200, w = 1300, h = 1000)
//      val x1s = Array(1.0, 2.0, 3.0)
//      val y1s = x1s * 0.5
//      val x2s = x1s * 2
//      val y2s = x1s * 4
//
//      p + ln_seg(x1s, y1s, x2s, y2s, strk = clr(0x66aa0022), typ = ln_typ.-, sz = 1)
//    }
//
//    if (true) {
//      val r = new Random
//      val p = plt(x = 200, y = 200, w = 1300, h = 1000)
//      val x1s = (0 to 1) map (_.toDouble) toArray
//      val y1s = x1s map (x => 100.0 - x)
//      //val y1s = x1s map (x => 100.0 + r.nextInt % 10)
//      //val y1s = x1s map (x => 90.0)
//
//      p + lne(x1s, y1s)
//    }
//  }
//}
