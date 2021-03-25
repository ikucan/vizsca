package trth_bonds

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
      val idx = p((d: Double) => !d.isNaN)
      (t(idx), p(idx), v(idx))
    }

  def main(argv: Array[String]): Unit = {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    val root = "c:/tmp/export/bonds/"
    //    val (inst, d0, d1) = ("BE0000340498", "20210212", "20210213")
    //    val fnm_q = f"${root}/${inst}_${d0}_${d1}.csv"
    // val (inst, d0) = ("BE0000334434", "20200817")
    // val (inst, d0) = ("BE0000334434", "20200910")
    // val (inst, d0) = ("BE0000334434", "20200911")
    // val (inst, d0) = ("BE0000336454", "20190829")
     // val (inst, d0) = ("BE0000336454", "20190830") //!! a bit at the tail
    // val (inst, d0) = ("BE0000336454", "20190912")
    //val (inst, d0) = ("BE0000337460", "20190415")
    //val (inst, d0) = ("BE0000340498", "20191001")
    // val (inst, d0) = ("BE0000342510", "20190327")
    //val (inst, d0) = ("BE0000342510", "20190328")
    //val (inst, d0) = ("BE0000346552", "20190903")
    //val (inst, d0) = ("BE0000346552", "20190904")  // !! a bit at the tail
    // val (inst, d0) = ("BE0000346552", "20190905")

//    val (inst, d0) = ("BE0000346552", "20190927")
//    val (inst, d0) = ("BE0000347568", "20190123")
//    val (inst, d0) = ("BE0000347568", "20190301")
//    val (inst, d0) = ("BE0000348574", "20190415")
//    val (inst, d0) = ("BE0000348574", "20190416")
//    val (inst, d0) = ("BE0000348574", "20190417")
//    val (inst, d0) = ("BE0000348574", "20190418")
//    val (inst, d0) = ("BE0000348574", "20190606")
//    val (inst, d0) = ("BE0000348574", "20190722")
//    val (inst, d0) = ("BE0000353624", "20210217")

    val (inst, d0) = ("DE0001102473", "20200103")  // Interesting!!!
    // val (inst, d0) = ("DE0001102523", "20200813")
    // val (inst, d0) = ("FR0010916924", "20200721")
    //val (inst, d0) = ("FR0013154028", "20190827")
    // val (inst, d0) = ("FR0013154028", "20190828")
    //val (inst, d0) = ("FR0013154028", "20190829")
    //val (inst, d0) = ("FR0013154028", "20190830")
    //val (inst, d0) = ("FR0013154028", "20210119")
    // val (inst, d0) = ("FR0013234333", "20190222")
//    val (inst, d0) = ("FR0013313582", "20190328")
//    val (inst, d0) = ("FR0013313582", "20190507")
//    val (inst, d0) = ("FR0013313582", "20190712")
//    val (inst, d0) = ("FR0013313582", "20190722")
//    val (inst, d0) = ("FR0013313582", "20190725")
//    val (inst, d0) = ("FR0013404969", "20190312")
//    val (inst, d0) = ("FR0013404969", "20190415")
//    val (inst, d0) = ("FR0013404969", "20190416")
//    val (inst, d0) = ("FR0013404969", "20190423")
//    val (inst, d0) = ("FR0013404969", "20190613")
//    val (inst, d0) = ("FR0013404969", "20190614")
//    val (inst, d0) = ("FR0013404969", "20190619")
//    val (inst, d0) = ("FR0013404969", "20190712")
//    val (inst, d0) = ("FR0013404969", "20190715")
//    val (inst, d0) = ("FR0013404969", "20190725")
//    val (inst, d0) = ("FR0013404969", "20190912")
//    val (inst, d0) = ("FR0013415627", "20200211")
//    val (inst, d0) = ("FR0013479102", "20200211")
//    val (inst, d0) = ("FR0013479102", "20200212")
//    val (inst, d0) = ("FR0013515806", "20200624")
//    val (inst, d0) = ("IT0005365165", "20190618")
//    val (inst, d0) = ("NL0000102234", "20190819")
//    val (inst, d0) = ("NL0000102234", "20190828")

    //val (inst, d0) = ("NL0000102234", "20190829")
//    val (inst, d0) = ("NL0010071189", "20200102")
//    val (inst, d0) = ("NL0011220108", "20191230")
//    val (inst, d0) = ("NL0011220108", "20200102")
//    val (inst, d0) = ("NL0011819040", "20200226")
//    val (inst, d0) = ("NL0011819040", "20200227")
//    val (inst, d0) = ("NL0012171458", "20190104")
//    val (inst, d0) = ("NL0012171458", "20190131")
//    val (inst, d0) = ("NL0012171458", "20190626")
//    val (inst, d0) = ("NL0012818504", "20190829")
    //val (inst, d0) = ("NL0013332430", "20191104")
// val (inst, d0) = ("NL0013332430", "20191122")
//    val (inst, d0) = ("NL0013552060", "20201216")

    //val fnm_q = f"${root}/${inst}_${d0}_quotes.csv"
    val fnm_q = "c:/tmp/tst2.csv"

    println(f"Plotting quotes from file: ${fnm_q}")

    val (q_data, q_hdrs) = csv.from_fl(fnm_q)
    val df_qts = new frm()

    (0 until q_hdrs.size) foreach { i =>
      val (h, d) = (q_hdrs(i), q_data(i))
      if (h == "t" || h == "t_lcl") df_qts.set_arg(h, csv.str2dte(d, "yyyy-MM-dd HH:mm:ss.SSS"))
      else if (h == "bid" || h == "ask"  || h == "bidyield" || h == "askyield" ) df_qts.set_arg(h, csv.str2dbl(d))
      else if (h == "sym" ) df_qts.set_arg(h, d)
      else println(f"ignoring quote columns: ${h}")
    }

    val (bid_tme, bid_prc, bid_yield) = cln(df_qts.t[Long], df_qts.get_arg[Double]("bid"), df_qts.get_arg[Double]("bid"))
    val (ask_tme, ask_prc, ask_yield) = cln(df_qts.t[Long], df_qts.get_arg[Double]("ask"), df_qts.get_arg[Double]("ask"))
    val (mid, mid_yield) = ((bid_prc + ask_prc)/2, (bid_yield + ask_yield)/2)

    val sym = df_qts.sym[String]

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")
    pp + ttl(f"${inst}/${sym(0)} :: ${d0} :: ${mid.size}")

    val bid_clr = clr(0x55880011)
    val ask_clr = clr(0x55110088)
    val mid_clr = clr(0x55444466)
    val ma1_clr = clr(0x55224422)

    val (bt, at, mt) = (bid_tme, ask_tme, ask_tme)
    val (bp, ap, mp) = (bid_prc, ask_prc, mid)
    //val (bp, ap, mp) = (bid_yield, ask_yield, mid_yield)
    val ma1 = ewma.async(mt, mp, 30*1000)

    pp + tck(bt, bp, strk = bid_clr)
    pp + sctr2(bt, bp, strk = bid_clr, fll = bid_clr, shp = >=, sz = 10, ln_sz = 1)
    pp + tck(at, ap, strk = ask_clr)
    pp + sctr2(at, ap, strk = ask_clr, fll = ask_clr, shp = <=, sz = 10, ln_sz = 1)
    pp + tck(mt, mp, strk = mid_clr)
    pp + sctr2(mt, mp, strk = mid_clr, fll = mid_clr, shp = dot, sz = 10, ln_sz = 1)

    pp + lne(mt, ma1, strk = ma1_clr, fill = ma1_clr, typ = ln_typ.-, sz = 5)
    pp + sctr2(mt, ma1, strk = ma1_clr, fll = ma1_clr, shp = bx, sz = 5, ln_sz = 2)
  }
}