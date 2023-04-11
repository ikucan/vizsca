package sxo

import ik.util.csv
import ik.util.df._
import ik.util.ui.clr
import ik.util.vctr
import ik.viz.dsl._
import ik.viz.shp2._
import ik.viz.ln_typ

import ik.analytics._
import java.util.TimeZone

object plot_quotes {

  def main(argv: Array[String]): Unit = {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

    val pair = "GBPEUR"
    val date = "20230406"
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

    val bid_clr = clr(0x55880011)
    val ask_clr = clr(0x55110088)
    val mid_clr = clr(0x55444466)

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    //pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")
    pp + grd(false, true) + x_axs_inst("HH:mm:ss.SSS\nyyyy.MM.dd") + y_axs("#.#####")
    pp + ttl(f"${pair} :: ${date} ")

    pp + tck(t, bid, strk = bid_clr)
    pp + sctr2(t, bid, strk = bid_clr, fll = bid_clr, shp = >=, sz = 10, ln_sz = 1)

    pp + tck(t, ask, strk = ask_clr)
    pp + sctr2(t, ask, strk = ask_clr, fll = ask_clr, shp = <=, sz = 10, ln_sz = 1)





  }
}
