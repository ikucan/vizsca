//import ik.util.loggd
//import ik.viz.dsl._
//import ik.viz.dyn_foo
//import ik.viz.dsl.sctr
//import ik.viz.shape._
//import ik.util.ui.clr._
//import ik.util.ui.clr
//
//object dsl_tst_dynnmc extends loggd {
//
//  import math._
//
//  def main(argv: Array[String]): Unit = {
//
//    var x = 0.0
//    val xx = (0 until 1000) map (_.toDouble)
//    val yy = xx map (math.sin(_))
//
//    val df1 = new dyn_foo(Array[Double](0.0), Array[Double](0.0), 540)
//    val df2 = new dyn_foo(Array[Double](0.0), Array[Double](1.0), 0)
//    val p = plt(x = 30, y = 30, w = 700, h = 400)
//
//    p + sctr(df1, shp = dot, strk = clr.red, sz = 2)
//    p + sctr(df2, shp = pls, strk = clr.blu, sz = 2)
//
//    Thread.sleep(200)
//    for (i <- 1 until 2500) {
//      x += 1.0
//      val (sn, cs) = (math.sin(x * math.Pi / 180.0) + (x % 10).toDouble / 10.0, math.cos(x * math.Pi / 180.0))
//      df1 += (x, sn)
//      df2 += (x, cs)
//      Thread.sleep(10)
//    }
//
//    Thread.sleep(10 * 1000)
//
//  }
//
//}
