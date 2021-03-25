package bbg

import java.util.TimeZone

import ik.util.csv
import ik.viz.dsl._
import ik.util.{csv, parq}
import ik.util.df._
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.shp2._

object bbg_trades_check {

  //  def cln(t: Array[Long], v: Array[Double]) = {
  //    val idx = v((d: Double) => !d.isNaN)
  //    (t(idx), v(idx))
  //  }
  //
  //  def cln(t: Array[Long], v: Array[Double], v2: Array[Double], l: Array[String]) = {
  //    val idx = v((d: Double) => !d.isNaN)
  //    (t(idx) map (x => x.toDouble), v(idx), v2(idx), l(idx))
  //  }

  def main(argv: Array[String]): Unit = {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

    println("=== start ===")

    def rd_quotes(f: String) = {
      val (data, hdrs) = csv.from_fl(f)
      val df = new frm()

      (0 until hdrs.size) foreach { i =>
        val (h, d) = (hdrs(i), data(i))
        if (h == "record_timestamp") df.set_arg("rt", csv.str2dte(d, "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"))
        else if (h == "trade_timestamp") df.set_arg("t", csv.str2dte(d, "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))
        else if (h == "trade_price") df.set_arg("prc", csv.str2dbl(d))
        else if (h == "trade_size") df.set_arg("sze", csv.str2dbl(d))
        else if (h == "security_id") df.set_arg("id", csv.str2dbl(d))
        else println("h:>> " + h)
      }
      df
    }

    //
    //    def cln(t: Array[Long], v: Array[Double], v2: Array[Double], l: Array[String]) = {
    //      val idx = v((d: Double) => !d.isNaN)
    //      (t(idx) map (x => x.toDouble), v(idx), v2(idx), l(idx))
    //    }
    //
    val frm = rd_quotes("e:/tmp/bbg_fut/trades_8938.csv")
    //
    val (t, p, s) = (frm.rt[Long], frm.prc[Double], frm.sze[Double])

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")


    pp + sctr2(t, p, strk = clr(0x99440011), fll = clr(0x22880011), shp = bx, sz = 7, ln_sz = 2)

    println("=== end ===")
  }
}

