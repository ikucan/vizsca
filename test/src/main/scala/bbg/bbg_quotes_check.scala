package bbg

import java.util.TimeZone

import ik.util.csv
import ik.viz.dsl._

import ik.util.csv
import ik.viz.dsl._
import ik.util.{csv, parq}
import ik.util.df._
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.shp2._

object bbg_quotes_check {

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
        else if (h == "bid_timestamp") {
          val dt_str = d map (_.replaceAll("...$",""))
          df.set_arg("bt", csv.str2dte(dt_str, "yyyy-MM-dd'T'HH:mm:ss.SSS"))
        }
        else if (h == "ask_timestamp") {
          val dt_str = d map (_.replaceAll("...$",""))
          df.set_arg("at", csv.str2dte(dt_str, "yyyy-MM-dd'T'HH:mm:ss.SSS"))
        }
        else if (h == "bid") df.set_arg("bid", csv.str2dbl(d))
        else if (h == "ask") df.set_arg("ask", csv.str2dbl(d))
        else if (h == "bid_size") df.set_arg("bsz", csv.str2dbl(d))
        else if (h == "ask_size") df.set_arg("asz", csv.str2dbl(d))
        else if (h == "security_id") df.set_arg("id", csv.str2dbl(d))
        else println("h:>> " + h)

      }
      df
    }


    def cln(t: Array[Long], v: Array[Double], v2: Array[Double]) = {
      val idx = v((d: Double) => !d.isNaN)
      (t(idx) map (x => x.toDouble), v(idx), v2(idx))
    }

    val frm = rd_quotes("e:/tmp/bbg_fut/quotes_8938.csv")
    //
    val (tb, b, bs) = cln(frm.bt[Long], frm.bid[Double], frm.bsz[Double])
    val (ta, a, as) = cln(frm.at[Long], frm.ask[Double], frm.asz[Double])

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")
    pp + sctr2(tb, b, strk = clr(0x99440011), fll = clr(0x22880011), shp = >, sz = 7, ln_sz = 2)
    pp + sctr2(ta, a, strk = clr(0x99110044), fll = clr(0x22110088), shp = <, sz = 7, ln_sz = 2)

    println("=== end ===")
  }
}

