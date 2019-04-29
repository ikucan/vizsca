//package ik.util.vctr
//
//import org.junit.runner.RunWith
//import org.scalatest.junit.JUnitRunner
//import org.scalatest._
//
//@RunWith(classOf[JUnitRunner])
//class v_tst extends FlatSpec with ShouldMatchers {
//  //  "df" should " t " in {
//  //
//  //    val N = 10 * 512 * 1024
//  //
//  //    {
//  //      val a1 = (1 to N) map (_.toDouble) toArray
//  //      val a2 = (1 to N) map (_.toDouble) toArray
//  //      val t1 = System.currentTimeMillis
//  //      val vv1 = a1 + a2
//  //      a1 - a2
//  //      a1 - 1.0
//  //      a1 * a2
//  //      a1 * 1.0
//  //      a1 * a2
//  //      a1 * 1.0
//  //      //      a1 / a2
//  //      //      a1 / 1.
//  //      println(System.currentTimeMillis - t1)
//  //      val v1 = new gen_vctr_wrppr(a1)
//  //      val t0 = System.currentTimeMillis
//  //      val s1 = v1 + a2
//  //      v1 - a2
//  //      v1 - 1.0
//  //      v1 * a2
//  //      v1 * 1.0
//  //      v1 * a2
//  //      v1 * 1.0
//  //      println(System.currentTimeMillis - t0)
//  //    }
//  //
//  //    {
//  //      val a1 = (1 to N) map (_.toLong) toArray
//  //      val a2 = (1 to N) map (_.toLong) toArray
//  //      val t1 = System.currentTimeMillis
//  //      a1 - a2
//  //      a1 - 1l
//  //      a1 * a2
//  //      a1 * 1l
//  //      a1 / a2
//  //      a1 / 1l
//  //      println(System.currentTimeMillis - t1)
//  //      val v1 = new gen_vctr_wrppr(a1)
//  //      val t0 = System.currentTimeMillis
//  //      val s1 = v1 + a2
//  //      println(System.currentTimeMillis - t0)
//  //    }
//  //
//  //    {
//  //      val a1 = (1 to N) toArray
//  //      val a2 = (1 to N) toArray
//  //      val t1 = System.currentTimeMillis
//  //      val s2 = a1 + a2
//  //      a1 - a2
//  //      a1 - 1
//  //      a1 * a2
//  //      a1 * 1
//  //      a1 / a2
//  //      a1 / 1
//  //      println(System.currentTimeMillis - t1)
//  //      val v1 = new gen_vctr_wrppr(a1)
//  //      val t0 = System.currentTimeMillis
//  //      val s1 = v1 + a2
//  //      println(System.currentTimeMillis - t0)
//  //    }
//  //  }
//
//  //  "df" should " t " in {
//  //    val N = 10;
//  //    {
//  //      val a1 = (1 to N) map (_.toDouble) toArray;
//  //      val f1 = a1(math.abs(_: Double) == 2)
//  //      println(f1 toList)
//  //      val f2 = a1((_: Double) % 2 == 0)
//  //      println(f2 toList)
//  //    }
//  //  }
//  //  
//
//  " vector " should " should index correctly  " in {
//    val N = 10;
//    {
//      //val a1 = (5 to N ) map (_.toDouble) toArray;
//      val a1 = Array(2.0, 4.0, 6.0, 8.0, 10.0)
//      println(a1.srch(3.0, 11.0))
//      println(Array(1, 2, 3, 4, 5).mntnc)
//      println(Array(1, -1, 2, 3, 4, 5).mntnc)
//      println(Array(1.0, -1.0, 2.0, 3.0, 4.0, 5.0).mntnc)
//    }
//  }
//
//}