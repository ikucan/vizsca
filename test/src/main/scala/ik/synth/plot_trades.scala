//package ik.synth
//
//import java.io.File
//import java.util.TimeZone
//import java.util.Date
//
//import ik.util.df._
//import ik.util.parq
//import ik.util.ui.clr
//import ik.util.vctr._
//import ik.viz.dsl._
//import ik.viz.shp2._
//
//object plot_trades {
//  TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
//
//  val trds_path = "C:/tmp/trades"
//  val tgt_instr = "RU00010YL3MS"
//  val tgt_dlr = "BAML"
//  val tgt_ch = "CME"
//
//  def rd_trds(f:String) = {
//    if (new File(trds_fle).exists) {
//      println("Opening file:> " + trds_fle)
//      val t0 = System.currentTimeMillis()
//      val f = parq.rd(trds_fle)
//      println(raw"parquet reading time::  ${System.currentTimeMillis - t0}")
//      println(f.cols)
//
//      val dlr = f.dealer[String]
//      val dlr_idx = dlr((x: String) => x == "BAML")
//      val ch = f.clearingvenue[String]
//      val ch_idx = ch((x: String) => x == "CME")
//
//      val subset = f.slct(dlr_idx && ch_idx)
//
//      val dts = subset.dt[Long]
//      val tms = subset.t[Long]
//      println(dts.dstnct.toList map (new Date(_)))
//      println(dts.dstnct.size)
//      //println(f.dt[Long].slice(0,5).toList map (new Date(_)))
//      // println(f.t[Long].slice(0,5).toList map (new Date(_)))
//      //val dlr_idx = f.dealer[String]"BAML"
//
//    }
//    else {
//      println("Missing file :> " + trds_fle)
//    }
//  }
//
//  def main(argv: Array[String]): Unit = {
//
//    val trds_fle = trds_path + "/" + tgt_instr + "_trds.parquet"
//
//
//  }
//}