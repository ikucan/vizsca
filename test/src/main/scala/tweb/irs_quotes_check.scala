package tweb

import java.util.TimeZone

import ik.util.csv
import ik.util.df._
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.dsl._
import ik.viz.shp2._

object irs_quotes_check {

  def cln(t: Array[Long], v: Array[Double]) = {
    val idx = v((d: Double) => !d.isNaN)
    (t(idx), v(idx))
  }

  def cln(t: Array[Long], v: Array[Double], v2: Array[Double], l: Array[String]) = {
    val idx = v((d: Double) => !d.isNaN)
    (t(idx) map (x => x.toDouble), v(idx), v2(idx), l(idx))
  }

  def main(argv: Array[String]): Unit = {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

    if (argv.size < 1) throw new Exception("Pass CSV file containing the quotes as param.")
    val fnm = argv(0)

    def rd_quotes(f: String) = {
      val (data, hdrs) = csv.from_fl(f)
      val df = new frm()

      (0 until hdrs.size) foreach { i =>
        val (h, d) = (hdrs(i), data(i))
        if (h == "st" || h == "st_ny" || h == "btime" || h == "atime") df.set_arg(h, csv.str2dte(d, "yyyy-MM-dd HH:mm:ss")) // timestamps
        else if (h.startsWith("bid") || h.startsWith("ask")) df.set_arg(h, csv.str2dbl(d)) // prices
        else if (h.startsWith("bsiz") || h.startsWith("asiz")) df.set_arg(h, csv.str2dbl(d)) // sizes
        else df.set_arg(h, d) // sizes
        //else df.set_arg(h, csv.str2dbl(d))
      }
      df
    }

    def cln(t: Array[Long], v: Array[Double], v2: Array[Double], l: Array[String]) = {
      val idx = v((d: Double) => !d.isNaN && d > -99999)
      (t(idx) map (x => x.toDouble), v(idx), v2(idx), l(idx))
    }

    val frm = rd_quotes(fnm)
    //val frm = rd_quotes("e:/tmp/tw_test/RP00010YL6MS.csv")

    //    val (bt, bp, bs, bd) = cln(frm.st[Long], frm.bid[Double], frm.bsiz[Double], frm.dlr[String])
    //    val (at, ap, as, ad) = cln(frm.st[Long], frm.ask[Double], frm.asiz[Double], frm.dlr[String])
    val (bt, bp, bs, bd) = cln(frm.btime[Long], frm.bid[Double], frm.bsiz[Double], frm.dlr[String])
    val (at, ap, as, ad) = cln(frm.btime[Long], frm.ask[Double], frm.asiz[Double], frm.dlr[String])

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")

    def plot_dlr(dlr: String, n: Int = 0) = {
      val ixb = bd((x: String) => x == dlr)
      val ixa = ad((x: String) => x == dlr)
      val (bt2, bp2, bs2) = (bt(ixb), bp(ixb), bs(ixb))
      val (at2, ap2, as2) = (at(ixa), ap(ixa), as(ixa))
      
      val opcty = 0x11000000 * (0xa - n)
      pp + sctr2(bt2, bp2, strk = clr(0x11440011 | opcty), fll = clr(0x22880011), shp = >=, sz = 7, ln_sz = 2)
      //pp + tck(bt2, bp2, strk = clr(0x22440011))
      pp + sctr2(at2, ap2, strk = clr(0x00110044 | opcty), fll = clr(0x22110088), shp = <=, sz = 7, ln_sz = 2)
      //pp + tck(at2, ap2, strk = clr(0x22110044))
      pp + lbls(bt2, bp2, bp2 map (_ => dlr), sz = 10, xoff = 5, yoff = -5, rot = 0)
      pp + lbls(at2, ap2, ap2 map (_ => dlr), sz = 10, xoff = 5, yoff = 5, rot = 0)
    }

    var n = 0;
    bd.distinct foreach { x =>
      println(f" ===== ${x} =====")
      //if (x == "DB") {
      //if (x == "MIZU" || x == "JPM") {
        if (true) {
        plot_dlr(x, n)
        n += 1
      }
    }

//    // overlay the LLOYds data over the top
//    val dlr = "MIZU"
//    val idx = bd((x: String) => x == dlr)
//    val (bt2, bp2, bs2) = (bt(idx), bp(idx), bs(idx))
//    val (at2, ap2, as2) = (at(idx), ap(idx), as(idx))
//
//    pp + sctr2(bt2, bp2, strk = clr(0xaa005511), fll = clr(0xaa005511), shp = dot, sz = 7, ln_sz = 2)
//    pp + tck(bt2, bp2, strk = clr(0x66005511))
//    pp + sctr2(at2, ap2, strk = clr(0xaa555500 ), fll = clr(0xaa555500), shp = dot, sz = 7, ln_sz = 2)
//    pp + tck(at2, ap2, strk = clr(0x66110044))
//    pp + lbls(bt2, bp2, bp2 map (_ => dlr), sz = 10, xoff = 5, yoff = -5, rot = 0)
//    pp + lbls(at2, ap2, ap2 map (_ => dlr), sz = 10, xoff = 5, yoff = 5, rot = 0)
  }
}

//
//def cln (t: Array[Long], v: Array[Double], v2: Array[Double], l: Array[String] ) = {
//  val idx = v ((d: Double) => ! d.isNaN)
//  (t (idx) map (x => x.toDouble), v (idx), v2 (idx), l (idx) )
//}

//      val f2 = rd_quotes(dta_fle)

//

//    val f1 = rd_quotes(fle1)
//    val (bt1, bp1, bs1, bi1) = cln(f1.msg_datetime[Long], f1.bid[Double], f1.bsz[Double], f1.tweb_id[String])
//    val (at1, ap1, as1, ai1) = cln(f1.msg_datetime[Long], f1.ask[Double], f1.asz[Double], f1.tweb_id[String])
//
//
//    val f3 = rd_quotes(fle3)
//    val (bt3, bp3, bs3, bi3) = cln(f3.msg_datetime[Long], f3.bid[Double], f3.bsz[Double], f3.tweb_id[String])
//    val (at3, ap3, as3, ai3) = cln(f3.msg_datetime[Long], f3.ask[Double], f3.asz[Double], f3.tweb_id[String])

//  val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
//  pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")

//    pp + sctr2(bt1, bp1, strk = clr(0x66880011), fll = clr(0x66880011), shp = <>, sz = 6, ln_sz = 2)
//    pp + sctr2(at1, ap1, strk = clr(0x66110088), fll = clr(0x66110088), shp = <>, sz = 6, ln_sz = 2)
//
//
//    def overlay(dta_fle:String, lbl:String) = {
//      val f2 = rd_quotes(dta_fle)
//      val (bt2, bp2, bs2, bi2) = cln(f2.msg_datetime[Long], f2.bid[Double], f2.bsz[Double], f2.tweb_id[String])
//      val (at2, ap2, as2, ai2) = cln(f2.msg_datetime[Long], f2.ask[Double], f2.asz[Double], f2.tweb_id[String])
//      pp + sctr2(bt2, bp2, strk = clr(0xaa440011), fll = clr(0x22880011), shp = o, sz = 8, ln_sz = 2)
//      pp + lne(bt2, bp2, strk = clr(0x44440011))
//      pp + sctr2(at2, ap2, strk = clr(0xaa110044), fll = clr(0x22110088), shp = o, sz = 8, ln_sz = 2)
//      pp + lne(at2, ap2, strk = clr(0x44110044))
//      pp + lbls(bt2, bp2, bi2 map (_ => lbl), sz = 10, xoff = 5, yoff = -5, rot = 0)
//      pp + lbls(at2, ap2, ai2 map (_ => lbl), sz = 10, xoff = 5, yoff = 5, rot = 0)
//    }
//
//    overlay("c:/tmp/quotes/20200124/quote/RP00025YL6MS_DB_LCH_post.csv", "25Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00020YL6MS_DB_LCH_post.csv", "20Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00015YL6MS_DB_LCH_post.csv", "15Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00014YL6MS_DB_LCH_post.csv", "14Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00013YL6MS_DB_LCH_post.csv", "13Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00012YL6MS_DB_LCH_post.csv", "12Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00011YL6MS_DB_LCH_post.csv", "11Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00010YL6MS_DB_LCH_post.csv", "10Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00009YL6MS_DB_LCH_post.csv", "9Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00008YL6MS_DB_LCH_post.csv", "8Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00007YL6MS_DB_LCH_post.csv", "7Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00006YL6MS_DB_LCH_post.csv", "6Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00005YL6MS_DB_LCH_post.csv", "5Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00004YL6MS_DB_LCH_post.csv", "4Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00003YL6MS_DB_LCH_post.csv", "3Y")
//    overlay("c:/tmp/quotes/20200124/quote/RP00002YL6MS_DB_LCH_post.csv", "2Y")
