package ik.synth

import java.util.{Date, TimeZone}

import ik.util.df._
import ik.util.parq
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.dsl._
import ik.viz.shp2._


object plot_spreads {


  def main(argv: Array[String]): Unit = {

    def fltr(t: Array[Long], v: Array[Double]) = {
      val non_nan = !v.is_nan
      (t(non_nan) map (x => x.toDouble), v(non_nan))
    }

    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

    //val fnm = "//elementcapital.corp/ecns01/TDPipeline/tweb/parquet/20190306/RU00003YL3MS.parquet"
    //val fnm = "c:/tmp/parquet/20190306/RU00003YL3MS.parquet"
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
      val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
      pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")
      pp + ttl(raw"Spreads by dealer for RU00003YL3MS on 20190306")


      def add_spreads(brkr: String, c: clr, sdf: frm): Unit = {
        val (t, spread) = fltr(sdf.st[Long], sdf.spread[Double])

        pp + tck(t, spread, strk = c, sz = 1)
        //pp + sctr2(ttb, ppb, strk = clr(0xffff0000), fll = c, shp =dot, sz = 6f, ln_sz = 1f)
      }

      val dlrs = df.dlr[String]

      df.dlr[String].dstnct foreach { brkr =>
        println(brkr)
        val idx = dlrs((x: String) => x == brkr)
        val sdf = df.slct(idx)

        if (brkr == "GS") add_spreads(brkr, clr(0xff1100bb), sdf)
        if (brkr == "BAML") add_spreads(brkr, clr(0xffbb0011), sdf)
        if (brkr == "C") add_spreads(brkr, clr(0xff00bb11), sdf)
        if (brkr == "SG") add_spreads(brkr, clr(0xffbbbb11), sdf)
        //        if (brkr == "WFS") add_spreads(brkr, clr(0xffbbbb11), sdf)
        //        if (brkr == "DB") add_spreads(brkr, clr(0xffbbbb11), sdf)
        //        if (brkr == "JPM") add_spreads(brkr, clr(0xffbbbb11), sdf)
        //        if (brkr == "RBS") add_spreads(brkr, clr(0xffbbbb11), sdf)
      }

      {

      }
    }
    //pp + sctr2(df.x_deg[Long] map (_.toDouble),df.cos[Double],  strk = clr(0xff0000ff), fll = clr(0xff0000ff), shp = x, sz = 8f, ln_sz = 1f)
  }
}