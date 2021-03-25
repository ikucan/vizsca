//package tck_wrt
//
//import java.util.TimeZone
//
//import ik.util.csv
//import ik.util.ui.clr
//import ik.viz.dsl._
//import ik.viz.shp2._
//import java.util.{Date, TimeZone}
//
//import ik.util.{csv, parq}
//import ik.util.df._
//import ik.util.ui.clr
//import ik.util.vctr._
//import ik.viz.dsl._
//import ik.viz.shp2._
//
//
//object quotes_test {
//
//  def cln(t: Array[Long], v: Array[Double]) = {
//    val idx = v((d: Double) => !d.isNaN)
//    (t(idx), v(idx))
//  }
//
//  def cln(t: Array[Long], v: Array[Double], v2: Array[Double]) = {
//    val idx = v((d: Double) => !d.isNaN)
//    (t(idx) map (x => x.toDouble), v(idx), v2(idx))
//  }
//
//  def main(argv: Array[String]): Unit = {
//
//    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
//
//    val fnm =
//      if (argv.size < 1) {
//        f"e:/tmp/tw_test/raw/FTM19.csv"
//      }
//      else
//        argv(0)
//
//    val (data, hdrs) = csv.from_fl(fnm)
//
//    println(hdrs.toList)
//
//    val df = new frm()
//
//    (0 until hdrs.size) foreach { i =>
//      val (h, d) = (hdrs(i), data(i))
//      if (h == "Date and Time") df.set_arg(h, csv.str2dte(d, "dd/MM/yyyy HH:mm:ss.SSS"))
//      else if (h == "Bid Price" || h == "Ask Price" || h == "Bid Size" || h == "Ask Size") df.set_arg(h, csv.str2dbl(d))
//      else println(f"ignoring field : ${h}")
//    }
//
//    val (tb, pb, sb) = cln(df.get_arg[Long]("Date and Time"), df.get_arg[Double]("Bid Price"), df.get_arg[Double]("Bid Size"))
//    val (ta, pa, sa) = cln(df.get_arg[Long]("Date and Time"), df.get_arg[Double]("Ask Price"), df.get_arg[Double]("Ask Size"))
//
//    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
//    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")
//
//    //
//    //    val bid_9_idx = pb === -999999
//    //    val ask_9_idx = pb === -999999
//    //    val bid_ok_idx = !bid_9_idx
//    //    val ask_ok_idx = !ask_9_idx
//    //
//    //    val (tbb, pbb, sbb) = (tb(bid_ok_idx), pb(bid_ok_idx), sb(bid_ok_idx))
//    //    val (taa, paa, saa) = (ta(ask_ok_idx), pa(ask_ok_idx), sa(ask_ok_idx))
//    //
//    //    val ssb = (sbb / sbb.max * 10 + 2) map (_.toFloat)
//    //    val ssa = (saa / saa.max * 10 + 2) map (_.toFloat)
//    //
//    ////    pp + tck(tbb, pbb, strk = clr(0x66880011))
//    ////    pp + tck(taa, paa, strk = clr(0x66110088))
//    pp + sctr2(tb, pb, strk = clr(0x66880011), fll = clr(0x66880011), shp = >=, sz = 5, ln_sz = 1)
//    pp + sctr2(ta, pa, strk = clr(0x66110088), fll = clr(0x66110088), shp = <=, sz = 5, ln_sz = 1)
//    //
//    //    pp + vln(tb(bid_9_idx), strk = clr(0x66880011), sz = 2)
//    //    pp + vln(ta(ask_9_idx), strk = clr(0x66110088), sz = 2)
//    //
//    //    pp + ttl(f"quotes:  ${dt} : ${instr}/${dlr}/${ch}")
//  }
//
//}