//package ik.viz.tst
//
//import scala.math._
//import scala.util.Random
//
//import ik.util.loggd
//import ik.util.ui.clr
//import ik.util.vctr._
//import ik.viz.dsl._
//import ik.viz._
//import ik.viz.shp2._
//
//object sze_vctr_tst extends loggd {
//
//  def main(argv: Array[String]): Unit = {
//
//    log_dbg("1")
//    val N = 3000
//    val x = (0 until N) toArray;
//    val y1 = x map (x => sin(math.Pi * x / 360.0))
//    val y2 = x map (x => cos(math.Pi * x / 360.0))
//    val sz = x map (x => cos(math.Pi * x / 360.0) * 5 + 6)
//
//    if (true) {
//      val r = new Random
//      val p = plt(x = 200, y = 200, w = 1300, h = 1000)
//
//      p + sctr2(x, y1, strk = clr(0xff000055), fll = clr(0xff000055), shp2.o, 10, 2)
//      // p + sctr2(x, y2, strk = Array(clr(0xff550000), clr(0xff005500), clr(0xff000055)), fll = clr(0xff550000), shp2.x, 10, 2)
//      //p + sctr2(x, y2, strk = Array(clr(0xff550000), clr(0xff005500), clr(0xff000055)), fll = clr(0xff550000), Array(shp2.x, shp2.o, shp2.pls), Array(10, 20, 30), Array(3, 2, 1))
//      p + lbls(x, y1, x map (_.toString), Array(15), Array(0), Array(25), Array(clr(0xff550000)), "Arial")
//
//      p + lbls(x, y1, l = x map (_.toString), sz = 15, yoff = -25)
//
//      //p + sctr2(x, y, strk = clr(0xff000055), fll = clr(0xff000055), shp2.x, sz = sz)
//    }
//  }
//}
