//import ik.util.loggd
//import ik.util.ui.clr
//import ik.viz.dsl.grd
//import ik.viz.dsl.ln_seg
//import ik.viz.dsl.plt
//import ik.viz.dsl.sctr
//import ik.viz.dyn_foo
//import ik.viz.ln_typ
//import ik.viz.shape.bx
//import ik.viz.shape.x
//
//object tst_seg_plts extends loggd {
//
//  import math._
//
//  def main(argv: Array[String]): Unit = {
//
//    val pts1 = Array[(Double, Double)]((0, 1), (1, 2), (3, 1))
//    val pts2 = Array[(Double, Double)]((1, 1), (2, 2), (3, 2))
//
//    //print(pts1 map (x=> x._1))
//    val df1 = new dyn_foo(pts1 map (_._1), pts1 map (_._2))
//    val df2 = new dyn_foo(pts2 map (_._1), pts2 map (_._2))
//
//    //val df1 = new dyn_foo(Array[Double](0, 1), Array[Double](1, 2))
//    //val df2 = new dyn_foo(Array[Double](1, 2), Array[Double](1, 2))
//
//    val p = plt(x = 200, y = 100, w = 1000, h = 800)
//    p + grd(false, false)
//
//    p + sctr(df1, shp = bx(10), strk = clr.red)
//    p + sctr(df2, shp = x(10), strk = clr.blu)
//    p + ln_seg(df1, df2, strk = clr.blu, sz = 1, typ = ln_typ.-)
//
//  }
//
//}
