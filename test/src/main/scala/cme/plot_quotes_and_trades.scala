package cme

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

  def cln(t: Array[Long], p: Array[Double], v: Array[Double]) = {
    //def cln(t: Array[Long], p: Array[Double], v: Array[Double]): (Array[Long], Array[Double], Array[Double])= {
    val idx = p((d: Double) => !d.isNaN)
    (t(idx), p(idx), v(idx))
  }

  def main(argv: Array[String]): Unit = {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    //    val cme_root = "c:/tmp/cme_sample/decoded/20200101"
    val cme_root = "w:/sundry/cme_sample/unpacked/"
    val trth_root = "c:/tmp/export/futures2/"
    //    val fnm_q = f"${cme_root}/mini_q.csv"
    //    val fnm_t = f"${cme_root}/mini_t.csv"
    val cme_instr = "ZNH0"
    val trth_instr = "TYH0"
    val date = "20191002"
    val fnm_q_cme = f"${cme_root}/${date}/${cme_instr}_quote.csv"
    val fnm_t_cme = f"${cme_root}/${date}/${cme_instr}_trade.csv"
    val fnm_q_trth = f"${trth_root}/${date}/${trth_instr}_quote.csv"
    val fnm_t_trth = f"${trth_root}/${date}/${trth_instr}_trade.csv"

    val df_qts_trth = {
      println(f"Reading TRTH quotes ${fnm_q_trth}")
      val (data, hdrs) = csv.from_fl(fnm_q_trth)
      val df = new frm()
      (0 until hdrs.size) foreach { i =>
        val (h, d) = (hdrs(i), data(i))
        if (h == "SendingTime" || h == "transactTime") {
          val times = csv.str2inst_ns_trunc(d, Some("yyyy-MM-dd HH:mm:ss.SSS"))
          df.set_arg(h, times)
        }
        else if (h == "bid_price" || h == "ask_price" || h == "bid_size" || h == "ask_size") df.set_arg(h, csv.str2dbl(d))
        else println(f"ignoring quote columns: ${h}")
      }
      df
    }

    val df_qts_cme = {
      println(f"Reading CME quotes ${fnm_q_cme}")
      val (data, hdrs) = csv.from_fl(fnm_q_cme)
      val df_qts = new frm()
      (0 until hdrs.size) foreach { i =>
        val (h, d) = (hdrs(i), data(i))
        if (h == "SendingTime" || h == "TransactTime") {
          val times = csv.str2inst_ns_trunc(d, Some("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))
          df_qts.set_arg(h, times)
        }
        else if (h == "bid" || h == "ask" || h == "bid_size" || h == "ask_size") df_qts.set_arg(h, csv.str2dbl(d))
        else if (h == "Symbol" || h == "TradeDate") df_qts.set_arg(h, d)
        else println(f"ignoring quote columns: ${h}")
      }
      //    val ma1 = ewma.async(mt, mp, 30*1000)
    }
    //    val (t_data, t_hdrs) = csv.from_fl(fnm_t)
    //    val df_trds = new frm()
    //    (0 until t_hdrs.size) foreach { i =>
    //      val (h, d) = (t_hdrs(i), t_data(i))
    //      if (h == "SendingTime" || h == "TransactTime") {
    //        val times = csv.str2inst_ns_trunc(d, Some("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))
    //        df_trds.set_arg(h, times)
    //      }
    //      else if (h == "MDEntryPx" || h == "MDEntrySize" || h == "NumberOfOrders" ) df_trds.set_arg(h, csv.str2dbl(d))
    //      else if (h == "AggressorSide" || h == "TradeDate") df_trds.set_arg(h, d)
    //      else println(f"ignoring quote columns: ${h}")
    //    }
    //    val (tt, tp, ts) = (df_trds.TransactTime[Long], df_trds.get_arg[Double]("MDEntryPx"), df_trds.get_arg[Double]("MDEntrySize"))
    //    val aggrs = df_trds.AggressorSide[String]

    val pp = plt(x = 1600, y = 300, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_inst("HH:mm:ss.SSSSSSSSS\n dd/MM") + y_axs("#.######")
    //    pp + ttl(f"${sym(0)}/${trade_date(0)}")

    if (true) {
      val (bt, bp, bs) = cln(df_qts_trth.transactTime[Long], df_qts_trth.get_arg[Double]("bid_price"), df_qts_trth.get_arg[Double]("bid_size"))
      val (at, ap, as) = cln(df_qts_trth.transactTime[Long], df_qts_trth.get_arg[Double]("ask_price"), df_qts_trth.get_arg[Double]("ask_size"))

      val bid_clr = clr(0x77880011)
      val bid_clr_l = clr(0x33880011)
      val ask_clr = clr(0x77110088)
      val ask_clr_l = clr(0x33110088)
      val trd_clr = clr(0xaa449944)
      pp + tck(bt, bp, strk = bid_clr)
      pp + sctr2(bt, bp, strk = bid_clr, fll = bid_clr_l, shp = >=, sz = 6, ln_sz = 1)
      pp + tck(at, ap, strk = ask_clr)
      pp + sctr2(at, ap, strk = ask_clr, fll = ask_clr_l, shp = <=, sz = 6, ln_sz = 1)

    }

    if (true) {
      val (bt, bp, bs) = cln(df_qts_cme.TransactTime[Long], df_qts_cme.get_arg[Double]("bid"), df_qts_cme.get_arg[Double]("bid_size"))
      val (at, ap, as) = cln(df_qts_cme.TransactTime[Long], df_qts_cme.get_arg[Double]("ask"), df_qts_cme.get_arg[Double]("ask_size"))

      val bid_clr = clr(0x77880011)
      val bid_clr_l = clr(0x33880011)
      val ask_clr = clr(0x77110088)
      val ask_clr_l = clr(0x33110088)
      val trd_clr = clr(0xaa449944)

      pp + tck(bt, bp, strk = bid_clr)
      pp + sctr2(bt, bp, strk = bid_clr, fll = bid_clr_l, shp = o, sz = 6, ln_sz = 1)
      pp + tck(at, ap, strk = ask_clr)
      pp + sctr2(at, ap, strk = ask_clr, fll = ask_clr_l, shp = box, sz = 6, ln_sz = 1)
      //pp + tck(tt, tp, strk = trd_clr)
      //pp + sctr2(tt, tp, strk = trd_clr, fll = trd_clr, shp = <>, sz = 12, ln_sz = 2)

    }

    //    pp + tck(bt, bp, strk = bid_clr)
    //    pp + sctr2(bt, bp, strk = bid_clr, fll = bid_clr_l, shp = >=, sz = 6, ln_sz = 1)
    //    pp + tck(at, ap, strk = ask_clr)
    //    pp + sctr2(at, ap, strk = ask_clr, fll = ask_clr_l, shp = <=, sz = 6, ln_sz = 1)
    //    //pp + tck(tt, tp, strk = trd_clr)
    //    //pp + sctr2(tt, tp, strk = trd_clr, fll = trd_clr, shp = <>, sz = 12, ln_sz = 2)
    //
    //
    //    {
    //      val a_idx = aggrs((x:String) => x == "Buy")
    //      pp + sctr2(tt(a_idx), tp(a_idx), strk = clr(0xaa22aa55), fll = clr(0x99aa7722), shp = >=, sz = 11, ln_sz = 2)
    //      pp + lbls(tt(a_idx), tp(a_idx), ts(a_idx) map (_.toString), 10, 5, -10)
    //    }
    //    {
    //      val a_idx = aggrs((x:String) => x == "Sell")
    //      pp + sctr2(tt(a_idx), tp(a_idx), strk = clr(0xaacccc22), fll = clr(0x992277aa), shp = <=, sz = 11, ln_sz = 2)
    //      pp + lbls(tt(a_idx), tp(a_idx), ts(a_idx) map (_.toString), 10, 5, 10)
    //    }
    //    {
    //      val a_idx = aggrs((x: String) => x == "NoAgresssor")
    //      pp + sctr2(tt(a_idx), tp(a_idx), strk = clr(0xaa22aa22), fll = clr(0x99999999), shp = <>, sz = 11, ln_sz = 2)
    //      pp + lbls(tt(a_idx), tp(a_idx), ts(a_idx) map (_.toString), 10, 5, 10)
    //    }
  }
}