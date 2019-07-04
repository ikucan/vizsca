package ik.synth

import ik.viz.dsl._
import ik.util.ui.clr
import ik.util.df._
import ik.util.vctr._
import ik.util.csv
import ik.viz.dot2


import ik.viz.shp2._

object swps_tst {


  def cln(t: Array[Long], v: Array[Double]) = {
    val idx = v((d: Double) => !d.isNaN)
    (t(idx), v(idx))
  }

  val str_cols = Set("sym", "clearingHouse", "bidBroker", "askBroker")
  val tstmp_cols = Set("time")
  val dta_cols = Set("bidPrice", "bidSize", "askPrice", "askSize")


  def main(argv: Array[String]): Unit = {

    val (instr, date) = ("RU00003YL3MS", "2019.05.22")
    //val (instr, date) = ("RU00003YL3MS", "2019.05.23")

    val (data, hdrs) = csv.from_fl(s"c:/tmp/${instr}_${date}.csv")
    val df = new frm()

    (0 until hdrs.size) foreach { i =>
      val (h, d) = (hdrs(i), data(i))
      if (str_cols.contains(h)) df.set_arg(h, d)
      else if (tstmp_cols.contains(h)) df.set_arg(h, csv.str2dte(d, "yyyy-MM-dd HH:mm:ss"))
      else if (dta_cols.contains(h)) df.set_arg(h, csv.str2dbl(d))
    }

    val brkrs = df.bidBroker[String]

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")

    def add(brkr: String, c: clr, c2: clr, sdf: frm): Unit = {
      val (tb, pb) = cln(sdf.time[Long], sdf.bidPrice[Double])
      val (ta, pa) = cln(sdf.time[Long], sdf.askPrice[Double])
      pp + tck(tb, pb, strk = c)
      pp + tck(ta, pa, strk = c)
      pp + sctr2(tb map (x => x.toDouble), pb, strk = c2, fll = c, shp = >=, sz = 8f, ln_sz = 1f)
      pp + sctr2(ta map (x => x.toDouble), pa, strk = c2, fll = c, shp = <=, sz = 8f, ln_sz = 1f)
    }

    brkrs.dstnct foreach { brkr =>
      println(brkr)
      val idx = brkrs((x: String) => x == brkr)
      val sdf = df.slct(idx)

      if (brkr == "JPM") add(brkr, clr(0x66110088), clr(0xdd110055), sdf)
      if (brkr == "BAML") add(brkr, clr(0x66880011), clr(0xdd550011), sdf)
      if (brkr == "GS") add(brkr, clr(0x66008811), clr(0xdd005511), sdf)
      if (brkr == "SG") add(brkr, clr(0x66888811), clr(0xdd555511), sdf)
    }

    pp + ttl(s"Broker breakdown: ${instr}, ${date}")

    Thread.sleep(2000)
  }

}