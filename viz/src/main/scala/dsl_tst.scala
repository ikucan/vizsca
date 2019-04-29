//import ik.util.loggd
//import ik.viz.dsl._
//import ik.viz.foo
//import ik.viz.dsl.sctr
//import ik.viz.shape._
//import ik.util.ui.clr._
//import ik.util.ui.clr
//
//object dud extends loggd {
//
//  import math._
//
//  def main(argv: Array[String]): Unit = {
//
//    var x = 0.0
//    val xx = (0 until 1000) map (_.toDouble) toArray
//    val yy = xx map (x => math.sin(x * math.Pi / 180.0))
//
//    val f1 = foo(xx, yy)
//
//    val p = plt(x = 30, y = 30, w = 700, h = 400)
//
//    p + sctr(f1, shp = dot, strk = clr.red, sz = 2)
//
//    Thread.sleep(10 * 1000)
//
//  }
//
//}
