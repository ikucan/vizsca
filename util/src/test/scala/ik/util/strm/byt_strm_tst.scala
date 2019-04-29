//package ik.util.strm
//
//import org.junit.runner.RunWith
//import org.scalatest.junit.JUnitRunner
//import org.scalatest._
//
//@RunWith(classOf[JUnitRunner])
//class byt_strm_tst extends FlatSpec with ShouldMatchers {
//  "strm" should " foo " in {
//    val bs = byt_strm(4)
//
//    bs << "123"
//    bs << "34567"
//    bs << "34567"
//    println(new String(bs.byts))
//    bs << "0987654321"
//    println(new String(bs.byts))
//    bs <<~1 
//    println(new String(bs.byts))
//  }
//}