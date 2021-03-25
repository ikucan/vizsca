package ik.synth

import java.util.{Date, TimeZone}

import ik.util.df._
import ik.util.parq
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.dsl._

import ik.synth.swps_tst.cln
import ik.util.parq
import ik.viz.dsl._
import ik.util.ui.clr
import ik.util.df._
import ik.util.vctr._
import ik.util.csv
import ik.viz.dot2
import ik.viz.shp2._



object plot_volume {


  def main(argv: Array[String]): Unit = {

    def fltr(t: Array[Long], v: Array[Double]) = {
      val non_nan = !v.is_nan
      (t(non_nan) map (x => x.toDouble), v(non_nan))
    }

    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))


    //val fnm = "//elementcapital.corp/ecns01/TDPipeline/tweb/parquet/20190306/RU00003YL3MS.parquet"
    //val fnm = "c:/tmp/parquet/20190306/RU00003YL3MS.parquet"
    val (instr, date) = ("RU00003YL3MS", "20190306")
    val fnm = "c:/tmp/processed/20190306/RU00003YL3MS.parquet"
    val t0 = System.currentTimeMillis()
    val df = parq.rd(fnm)
    println(raw"parquet reading time::  ${System.currentTimeMillis - t0}")
    println(df.col_nms toList)
    println(df.typs toList)

    println(df.date[String].slice(0, 10).toList)
    println(df.st[Long].slice(0, 10).toList)
    println(df.st[Long].slice(0, 10).toList map (x => new Date(x / 1000)))
    println(df.dlr[String].dstnct toList)

    if (1 > 0) {
      val pp = plt(x = 10, y = 10, w = 500, h = 300) + wmrk()
      pp + grd(false, true) + x_axs_tm() + y_axs("#.##")
      pp +  x_lbl("time of day") + y_lbl("bid/ask size (x1E6)")

      pp + ttl(raw"Volume by broker for ${instr} on ${date}")

      def add_volume(brkr: String, c: clr, c2: clr, sdf: frm): Unit = {
        val (bt, bsz) = fltr(sdf.st[Long], sdf.bsiz[Double]/1000000.0)
        val (at, asz) = fltr(sdf.st[Long], sdf.asiz[Double]/1000000.0)

        pp + tck(bt, bsz, strk = c, sz=1)
        pp + tck(at, asz, strk = c, sz=1)
        pp + sctr2(bt, bsz, strk = c2, fll = c, shp = >=, sz = 10f, ln_sz = 1f)
        pp + sctr2(at, asz, strk = c2, fll = c, shp = <=, sz = 10f, ln_sz = 1f)
      }

      val dlrs = df.dlr[String]

      df.dlr[String].dstnct foreach { brkr =>
        println(brkr)
        val idx = dlrs((x: String) => x == brkr)
        val sdf = df.slct(idx)

        if (brkr == "GS") add_volume(brkr, clr(0x66110088), clr(0xdd110055), sdf)
        if (brkr == "BAML") add_volume(brkr, clr(0x66880011), clr(0xdd550011), sdf)
          if (brkr == "C") add_volume(brkr, clr(0x66008811), clr(0xdd005511), sdf)
        if (brkr == "SG") add_volume(brkr, clr(0x66888811), clr(0xdd555511), sdf)

      }

      {

      }
    }
    //pp + sctr2(df.x_deg[Long] map (_.toDouble),df.cos[Double],  strk = clr(0xff0000ff), fll = clr(0xff0000ff), shp = x, sz = 8f, ln_sz = 1f)
  }
}