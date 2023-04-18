package sxo

import ik.util.csv
import ik.util.df._
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.dsl._
import ik.viz.shp2._
import ik.viz.ln_typ

import ik.analytics._
import java.util.TimeZone

object plot_quotes {

  def main(argv: Array[String]): Unit = {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

    val pair = "GBPUSD"
    val date = "20230417"
    val quotesFile = f"/data/$pair/$pair-$date.csv"

    val (data, _) = csv.from_fl(quotesFile)
    val headers = Array("t", "bid", "bsz", "ask", "asz")

    val df = new frm()

    (0 until headers.size) foreach { i =>
      val (h, d) = (headers(i), data(i))
      if (h == "t") {
        val times = csv.str2inst_ns(d, Some("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"))
        df.set_arg(h, times)
      } else if (h == "bid" || h == "ask") {
        df.set_arg(h, csv.str2dbl(d))
      } else if (h == "bsz" || h == "asz") {
        df.set_arg(h, csv.str2dbl(d))
      } else println(f"ignoring field : ${h}")
    }

    val (t, bid, ask) = (df.t[Long] map (_.toDouble / 1), df.get_arg[Double]("bid"), df.get_arg[Double]("ask"))
    val (bsz, asz) = (df.get_arg[Double]("bsz"), df.get_arg[Double]("asz"))

    val (mean_bsz, mean_asz) = (bsz.mean, asz.mean)
    val (bsz_strength, asz_strength) = (bsz/mean_bsz, asz/mean_asz)

    val tol = 0.1
    val b_up_idx = (0 until bsz.size) filter (i => bsz_strength(i) > (1 + tol)) toArray
    val b_down_idx = (0 until bsz.size) filter (i => bsz_strength(i) < (1 - tol)) toArray
    val a_up_idx = (0 until asz.size) filter (i => asz_strength(i) > (1 + tol)) toArray
    val a_down_idx = (0 until asz.size) filter (i => asz_strength(i) < (1 - tol)) toArray
    
    // map (i => (t(i), bid(i), bsz_strength(i) - mean_bsz)) toArray
    // val t_up = b_up_idx map (_._1)
    val (t_bid_up, mag_bid_up) = (t(b_up_idx), bid(b_up_idx))
    val (t_bid_down, mag_bid_down) = (t(b_down_idx), bid(b_down_idx))
    val bid_down_sz = ((bsz_strength(b_down_idx) * -1 + 1) * 10 ).map(_.toFloat) + 10
    val bid_up_sz = ((bsz_strength(b_up_idx) - 1) * 10 ).map(_.toFloat) + 10


    val (t_ask_up, mag_ask_up) = (t(a_up_idx), ask(a_up_idx))
    val (t_ask_down, mag_ask_down) = (t(a_down_idx), ask(a_down_idx))
    val ask_down_sz = ((asz_strength(a_down_idx) * -1 + 1) * 10 ).map(_.toFloat) + 10
    val ask_up_sz = ((asz_strength(a_up_idx) - 1) * 10 ).map(_.toFloat) + 10

    val bid_clr = clr(0x44880011)
    val ask_clr = clr(0x44110088)
    val tick_sz = 2

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    //pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")
    pp + grd(false, true) + x_axs_inst("HH:mm:ss.SSS\nyyyy.MM.dd") + y_axs("#.#####")
    pp + ttl(f"${pair} :: ${date} ")

    pp + tck(t, bid, strk = bid_clr)
    pp + sctr2(t, bid, strk = bid_clr, fll = bid_clr, shp = dot, sz = tick_sz, ln_sz = 1)

    pp + sctr2(t_bid_down, mag_bid_down, strk = bid_clr, fll = bid_clr, shp = <=, sz = bid_down_sz, ln_sz = 1)
    pp + sctr2(t_bid_up, mag_bid_up, strk = bid_clr, fll = bid_clr, shp = >=, sz = bid_up_sz, ln_sz = 1)
    pp + lbls(t_bid_down, mag_bid_down, bsz_strength(b_down_idx) map (_.toString.substring(0, 4)), sz = 12, xoff = 0, yoff = -10, rot = 90)
    pp + lbls(t_bid_up, mag_bid_up, bsz_strength(b_up_idx) map (_.toString.substring(0, 4)), sz = 12, xoff = 0, yoff = 10, rot = -90)

    pp + tck(t, ask, strk = ask_clr)
    pp + sctr2(t, ask, strk = ask_clr, fll = ask_clr, shp = dot, sz = tick_sz, ln_sz = 1)
    pp + sctr2(t_ask_down, mag_ask_down, strk = ask_clr, fll = ask_clr, shp = <=, sz = ask_down_sz, ln_sz = 1)
    pp + sctr2(t_ask_up, mag_ask_up, strk = ask_clr, fll = ask_clr, shp = >=, sz = ask_up_sz, ln_sz = 1)
    pp + lbls(t_ask_down, mag_ask_down, asz_strength(a_down_idx) map (_.toString.substring(0, 4)), sz = 12, xoff = 0, yoff = -10, rot = 90)
    pp + lbls(t_ask_up, mag_ask_up, asz_strength(a_up_idx) map (_.toString.substring(0, 4)), sz = 12, xoff = 0, yoff = 10, rot = -90)



  }
}
