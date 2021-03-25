package ik.synth

import java.util.{Date, TimeZone}

import ik.util.df._
import ik.util.parq
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.dsl._
import ik.viz.shp2._
import java.io.File

object plot_raw_prices_by_dealer_clearer {
  TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

  def main(argv: Array[String]): Unit = {
    def get_dta(root: String, instr: String, dates: Seq[String]) = {
      val frms = dates map { d =>
        val tgt = root + "/" + d + "/" + instr + ".parquet"
        if (new File(tgt).exists) {
          println("Opening file:> " + tgt)
          val t0 = System.currentTimeMillis()
          val f = parq.rd(tgt)
          println(raw"parquet reading time::  ${System.currentTimeMillis - t0}")
          Some(f)
        }
        else {
          println("Missing file :> " + tgt)
          None
        }
      }

      frms filter (_ != None) map (_.get) reduce (frm.++(_, _))
    }

    /**
      * filter out NaN values
      */
    def fltr(t: Array[Long], v: Array[Double]) = {
      val non_nan = !v.is_nan
      (t(non_nan) map (x => x.toDouble), v(non_nan))
    }

    val raw_root = "w:/tweb/parquet/"


    //val dates = List("20190305", "20190306", "20190307", "20190308", "20190309", "20190310", "20190311", "20190312", "20190313", "20190314", "20190315", "20190316")
    //val dates = (10 until 25) map ("201906" + _)
    val dates = (1 to 15) map (i => f"201907${i}%02d")
    val instr = "RU00003YL3MS"

    val df = get_dta(raw_root, instr, dates)

    if (1 > 0) {
      val pp = plt(x = 10, y = 10, w = 500, h = 300) + wmrk()
      pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")
      pp + ttl(raw"CME v LCH. Bid/Ask for RU00003YL3MS. Dealer: DB")

      val dlrs = df.dlr[String]
      val clrs = df.ttp[String]
      val db_idx = dlrs((x: String) => x == "DB")
      val cme_idx = clrs((x: String) => x == "CME")
      val lch_idx = clrs((x: String) => x == "LCH")

      {
        val cme_frm = df.slct(db_idx && cme_idx)
        val (tb_cme, b_cme) = fltr(cme_frm.st[Long], cme_frm.bid[Double])
        val (ta_cme, a_cme) = fltr(cme_frm.st[Long], cme_frm.ask[Double])
        //pp + tck(tb_cme, b_cme, strk = clr(0xffff0000))
        //pp + tck(ta_cme, a_cme, strk = clr(0xff0000ff))
        pp + sctr2(tb_cme, b_cme, strk = clr(0xffff0000), fll = clr(0x66ff0000), shp = x, sz = 8f, ln_sz = 1f)
        pp + sctr2(ta_cme, a_cme, strk = clr(0xff0000ff), fll = clr(0x660000ff), shp = x, sz = 8f, ln_sz = 1f)
      }

      {
        val lch_frm = df.slct(db_idx && lch_idx)
        val (tb_lch, b_lch) = fltr(lch_frm.st[Long], lch_frm.bid[Double])
        val (ta_lch, a_lch) = fltr(lch_frm.st[Long], lch_frm.ask[Double])
        pp + sctr2(tb_lch, b_lch, strk = clr(0xffbbbb00), fll = clr(0xccbbbb00), shp = dot, sz = 8f, ln_sz = 1f)
        pp + sctr2(ta_lch, a_lch, strk = clr(0xff22bb00), fll = clr(0xcc22bb00), shp = dot, sz = 8f, ln_sz = 1f)
        //pp + tck(tb_lch, b_lch, strk = clr(0xffaaaa00))
        //pp + tck(ta_lch, a_lch, strk = clr(0xff22aa00))
      }
    }
  }
}