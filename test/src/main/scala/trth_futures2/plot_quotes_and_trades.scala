package trth_futures2

import ik.util.csv
import ik.util.df._
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.dsl._
import ik.viz.shp2._
import java.util.TimeZone

object plot_quotes_and_trades {

    def cln(t: Array[Long], p: Array[Double], v: Array[Double]) = {
      val idx = p((d: Double) => !d.isNaN)
      (t(idx), p(idx), v(idx))
    }

  def main(argv: Array[String]): Unit = {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    val root = "c:/tmp/export/futures2/"
    //TYH0_2019-10-02_quotes.csv
    val (dt, inst) = ("2019-10-02", "TYH0")

    val fnm_t = f"${root}/${inst}_${dt}_trades.csv"
    val fnm_q = f"${root}/${inst}_${dt}_quotes.csv"

    println(f"Plotting trades from file: ${fnm_t}")
    println(f"Plotting quotes from file: ${fnm_q}")

    val (t_data, t_hdrs) = csv.from_fl(fnm_t)
    val (q_data, q_hdrs) = csv.from_fl(fnm_q)
    println(t_hdrs toList)

    val df_trd = new frm()
    val df_qts = new frm()

    (0 until t_hdrs.size) foreach { i =>
      val (h, d) = (t_hdrs(i), t_data(i))
      if (h == "t_ex" ) df_trd.set_arg(h, csv.str2dte(d, "yyyy-MM-dd HH:mm:ss.SSS"))
      else if (h == "price" || h == "volume"  || h == "accvolume" ) df_trd.set_arg(h, csv.str2dbl(d))
      else if (h == "tick_direction" || h == "aggressor") df_trd.set_arg(h, d)
      else println(f"ignoring trade columns: ${h}")
    }
    (0 until q_hdrs.size) foreach { i =>
      val (h, d) = (q_hdrs(i), q_data(i))
      if (h == "t_ex" ) df_qts.set_arg(h, csv.str2dte(d, "yyyy-MM-dd HH:mm:ss.SSS"))
      else if (h == "bid_price" || h == "ask_price"  || h == "bid_size" || h == "ask_size" ) df_qts.set_arg(h, csv.str2dbl(d))
      else println(f"ignoring quote columns: ${h}")
    }

    val (t_tme, t_prc, t_vol, t_acc_vol) = (df_trd.t_ex[Long], df_trd.get_arg[Double]("price"), df_trd.get_arg[Double]("volume"), df_trd.get_arg[Double]("accvolume"))
    val (bid_tme, bid_prc, bid_sze) = cln(df_qts.t_ex[Long], df_qts.get_arg[Double]("bid_price"), df_qts.get_arg[Double]("bid_size"))
    val (ask_tme, ask_prc, ask_sze) = cln(df_qts.t_ex[Long], df_qts.get_arg[Double]("ask_price"), df_qts.get_arg[Double]("ask_size"))

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")
    pp + ttl(inst)

    val bid_clr = clr(0x33880011)
    val ask_clr = clr(0x33110088)

    pp + tck(bid_tme, bid_prc, strk = bid_clr)
    pp + sctr2(bid_tme, bid_prc, strk = bid_clr, fll = bid_clr, shp = dot, sz = 5, ln_sz = 1)
    pp + tck(ask_tme, ask_prc, strk = ask_clr)
    pp + sctr2(ask_tme, ask_prc, strk = ask_clr, fll = ask_clr, shp = dot, sz = 5, ln_sz = 1)

    //val t_clr = clr(0x66110088)
    //pp + tck(t_tme, t_prc, strk = t_clr)
    val shps = df_trd.get_arg[String]("aggressor") map {x => if (x== "BID") >=  else if (x== "ASK") <= else <>}
    val clrs = df_trd.get_arg[String]("aggressor") map {x => if (x== "BID") clr(0xaaaa3366)  else if (x== "ASK") clr(0xaa6633aa) else clr(0xaa666666)}
    val lne_clrs = df_trd.get_arg[String]("aggressor") map {x => if (x== "BID") clr(0x66990033)  else if (x== "ASK") clr(0x66330099) else clr(0x66666666)}
    pp + sctr2(t_tme, t_prc, strk = clrs, fll = clrs, shp = shps, sz = 10, ln_sz = 2)
  }
}