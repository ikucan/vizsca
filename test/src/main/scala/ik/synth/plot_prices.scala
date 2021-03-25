package ik.synth

import java.util.TimeZone
import java.util.Date

import ik.synth.swps_tst.cln
import ik.util.parq
import ik.viz.dsl._
import ik.util.ui.clr
import ik.util.df._
import ik.util.vctr._
import ik.util.csv
import ik.viz.dot2
import ik.viz.shp2._


object plot_prices {


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
      val pp = plt(x = 10, y = 10, w = 500, h = 300) + wmrk()
      pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")
      pp + ttl(raw"Bid/Ask for RU00003YL3MS on 20190306")

      def add(brkr: String, c: clr, c2: clr, sdf: frm): Unit = {
        val (tb, pb) = fltr(sdf.btime[Long], sdf.bid[Double])
        val (ta, pa) = fltr(sdf.atime[Long], sdf.ask[Double])
        //val (ttb, ppb) = fltr(sdf.st[Long], sdf.bid[Double])

        val t_ba = (tb ++ ta)

        println(tb.size)
        println(ta.size)
        println(t_ba.size)
        println(Set(t_ba:_*).size)

        pp + tck(tb, pb, strk = c2)
        pp + tck(ta, pa, strk = c2)
        pp + sctr2(tb, pb, strk = c2, fll = c, shp = >=, sz = 6f, ln_sz = 1f)
        pp + sctr2(ta, pa, strk = c2, fll = c, shp = <=, sz = 6f, ln_sz = 1f)
        //pp + sctr2(ttb, ppb, strk = clr(0xffff0000), fll = c, shp =dot, sz = 6f, ln_sz = 1f)
      }

      val dlrs = df.dlr[String]
      val clrhs = df.ttp[String]
      val cme_idx = clrhs((x: String) => x == "LCH")
      df.dlr[String].dstnct foreach { brkr =>
        println(brkr)
        val idx = dlrs((x: String) => x == brkr)
        val sdf = df.slct(idx && cme_idx)

        if (brkr == "GS") add(brkr, clr(0x66110088), clr(0xdd110055), sdf)
        if (brkr == "BAML") add(brkr, clr(0x66880011), clr(0xdd550011), sdf)
        if (brkr == "C") add(brkr, clr(0x66008811), clr(0xdd005511), sdf)
        if (brkr == "SG") add(brkr, clr(0x66888811), clr(0xdd555511), sdf)
      }

      {

      }
    }
    //pp + sctr2(df.x_deg[Long] map (_.toDouble),df.cos[Double],  strk = clr(0xff0000ff), fll = clr(0xff0000ff), shp = x, sz = 8f, ln_sz = 1f)
  }
}