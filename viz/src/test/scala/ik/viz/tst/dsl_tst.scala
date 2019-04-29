//package ik.viz.tst
//
//import org.junit.runner.RunWith
//import org.scalatest.FlatSpec
//import org.scalatest.ShouldMatchers
//import ik.util.loggd
//import ik.viz.dsl.plt
//import ik.viz.dsl.wmrk
//import org.scalatest.junit.JUnitRunner
//
//@RunWith(classOf[JUnitRunner])
//class tsdb_test extends FlatSpec with ShouldMatchers with loggd {
//
//  //  "viz dsl" should "handle sizing, location and debug modes" in {
//  //    log_dbg("1")
//  //    val p = plt(x = 0, y = 0, w = 100, h = 100)
//  //    Thread.sleep(1000)
//  //    p + sz(600, 450) + lc(200, 200)
//  //    Thread.sleep(1000)
//  //    p + dbg()
//  //    Thread.sleep(1000)
//  //    p + dbg(false)
//  //    Thread.sleep(1000)
//  //    p + dbg()
//  //    Thread.sleep(1000)
//  //    p - dbg()
//  //    Thread.sleep(2000)
//  //  }
//
//  "viz dsl" should "allow for watermarking addition and removal " in {
//    log_dbg("1")
//    val p = plt(x = 100, y = 100, w = 600, h = 450)
//    Thread.sleep(5000)
//    p + wmrk()
//    Thread.sleep(5000)
//    p - wmrk()
//    Thread.sleep(5000)
//  }
//
//  //  "viz dsl" should "allow for line  plot construction" in {
//  //    log_dbg("1")
//  //    val N = 3000
//  //    val x = (0 until N) toArray;
//  //    val y = x map (x => cos(math.Pi * x / 360.))
//  //
//  //    val p = plt(x = 100, y = 100, w = 600, h = 450)
//  //
//  //    p + lne(fx, fy1, color.grey) + lne(fx, fy2, color.red)
//  //    Thread.sleep(30 * 1000)
//  //  }
//
//  //  "viz dsl" should "allow for scatter plot construction" in {
//  //
//  //    import shape._
//  //    import color._
//  //
//  //    log_dbg("1")
//  //    val N = 3000
//  //    val x = (0 until N) toArray;
//  //    val y = x map (x => cos(math.Pi * x / 360.))
//  //
//
//  //    val p = plt(x = 30, y = 30, w = 800, h = 450)
//  //
//  //    p + lne(fx, fy2)
//  //    p + sctr(fx, fy1, shp = dot, strk = black, fill = red, sz = 8)
//  //    p + sctr(fx, fy1, shp = cross, strk = blue, fill = red, sz = 4)
//  //
//  //    Thread.sleep(2 * 60 * 1000)
//  //  }
//
//  //  "viz dsl" should "be able to draw scatter plots" in {
//  //    import shape._
//  //
//  //    val NN = 1000
//  //
//  //    val fx3 = (0 to NN).toArray map (_.toDouble)
//  //    val fy3 = fx3 map { x => math.sin(x * math.Pi / 180.) }
//  //    val fx4 = (-NN to 000).toArray map (_.toDouble)
//  //    val fy4 = fx4 map { x => math.sin(x * math.Pi / 180.) }
//  //    val fx5 = (-NN to NN).toArray map (_.toDouble)
//  //    val fy5 = fx5 map { x => 0.1 + math.sin(x * math.Pi / 180.) }
//  //
//  //    val p = plt(x = 30, y = 30, w = 700, h = 400)
//  //    p +
//  //      sctr(fx3, fy3, shp = x, strk = clr(0xff001177), sz = 1) +
//  //      sctr(fx4, fy4, shp = pls, strk = clr(0xff771100), sz = 2) +
//  //      sctr(fx5, fy5, shp = dot, strk = clr(0xaa117700), sz = 1)
//  //
//  //    Thread.sleep(180 * 1000)
//  //  }
//
//  //  "viz dsl" should "be able to draw vline plots" in {
//  //    import shape._
//  //
//  //    val NN = 800
//  //
//  //    val fx1 = (NN/2 to NN).toArray map (_.toDouble)
//  //    val fy0 = fx1 map { x => math.sin(x * math.Pi / 180.) }
//  //    //val fy1 = fx1 map (_ => 0.)
//  //    //val fy1 = fx1 map { x => -0.5 + math.sin(x * math.Pi / 180.) }
//  //    val fy1 = fx1 map { x => math.cos(x * math.Pi / 180.) }
//  //    val fy2 = fx1 map { x => math.sin(x * math.Pi / 180.) - 1 }
//  //
//  //    val p = plt(x = 30, y = 30, w = 700, h = 400)
//  //    p + vlne(fx1, fy0, fy1, strk = clr(0xbb333366), sz = 2) +
//  //      vlne(fx1, fy2, fy1, strk = clr(0xbb663333), sz = 2) +
//  //      sctr(fx1, fy0, shp = dot, strk = clr(0xaa117700), sz = 6) +
//  //      sctr(fx1, fy1, shp = pls, strk = clr(0xaa117700), sz = 8) +
//  //      sctr(fx1, fy2, shp = x, strk = clr(0xaa117700), sz = 6)
//  //
//  //    Thread.sleep(180 * 1000)
//  //  }
//  //  "viz dsl" should "XXXXXXXXXXXXXXXXXX" in {
//  //    import shape._
//  //    import ik.util.vctr._
//  //
//  //    val NN = 10
//  //    val fx = (0 to NN) map (x => x + 5) toArray
//  //    val fy = (0 to NN) map (x => x) toArray
//  //
//  //    val p = plt(x = 30, y = 30, w = 700, h = 400)
//  //    p + lne(x = fx, y = fy, strk = clr(0xaa117700)) +
//  //      sctr(fx, fy, shp = dot, strk = clr(0xaa117700), fill = clr(0xaa771100), sz = 6)
//  //
//  //    Thread.sleep(180 * 1000)
//  //  }
//
//}
