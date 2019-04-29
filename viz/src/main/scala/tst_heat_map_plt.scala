//import scala.util.Random
//
//import ik.util.loggd
//import ik.viz.dscrt_foo2d_tle
//import ik.viz.dsl.grd
//import ik.viz.dsl.heat
//import ik.viz.dsl.plt
//import ik.viz.dsl.wmrk
//
//object tst_heat_map_plt extends loggd {
//
//  import math._
//
//  def main(argv: Array[String]): Unit = {
//
//    val r = new Random
//
//    val XDIM = 30
//    val YDIM = 30
//
//    val x = (0 until XDIM) map (_.toDouble) toArray
//    val y = (0 until YDIM) map (_.toDouble) toArray
//
//    val xlbl = (0 until XDIM) map (x => 65 + (x % 27)) map (_.toChar) map (_.toString) toArray
//    val ylbl = (0 until XDIM) map (x => 97 + (x % 27)) map (_.toChar) map (_.toString) toArray
//    //val z = x map (x => y map (y => x + y)) toArray
//    //val z = x map (x => y map (y => r.nextInt(255).toDouble)) toArray
//    //val z = x map (x => y map (y => x)) toArray
//    val z = x map (x => y map (y => y)) toArray
//
//    val f = new dscrt_foo2d_tle(z)
//
//    val p = plt(x = 200, y = 50, w = 1000, h = 1000) + wmrk()
//    p + heat(xlbl, ylbl, z)
//    p + grd(false, false)
//
//  }
//
//}
