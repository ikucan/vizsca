//package ik.viz.tst
//
//import scala.math.cos
//import ik.util.loggd
//import ik.util.ui.clr
//import ik.util.vctr._
//import ik.viz.dsl.plt
//import ik.viz.dsl.sctr
//import ik.viz.dsl.tck
//import ik.viz.shape.{ <> => <> }
//import scala.util.Random
//
//object tck_test extends loggd {
//
//  def main(argv: Array[String]): Unit = {
//
//    log_dbg("1")
//    val N = 3000
//    val x = (0 until N) toArray;
//    val y = x map (x => cos(math.Pi * x / 360.0))
//
//    if (true) {
//      //      val r = new Random(0)
//      //      val x = (0 until 100) map (_.toDouble) toArray;
//      //      val y = (0 until 100) map (i => 5.0 + r.nextInt(3)) toArray;
//
//      val x = Array(1, 2, 3, 4) //map (_.toDouble) toArray;
//      val y = Array(3, 3, 3, 4) // map (i => 5.0 + r.nextInt(3)) toArray;
//
//      val p = plt(x = 200, y = 200, w = 1300, h = 1000)
//      p + sctr(x, y, shp = <>(8), strk = clr(0xff444488))
//      p + tck(x, y, strk = clr(0x66880011))
//
//    }
//  }
//}
