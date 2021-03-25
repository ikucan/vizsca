package tweb

import java.util.TimeZone

import ik.util.csv
import ik.util.df._
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.dsl._
import ik.viz.shp2._

object irs_book_w_trds {

  def cln(t: Array[Long], v: Array[Double]) = {
    val idx = v((d: Double) => !d.isNaN)
    (t(idx), v(idx))
  }

  def cln(t: Array[Long], v: Array[Double], v2: Array[Double], l: Array[String]) = {
    val idx = v((d: Double) => !d.isNaN)
    (t(idx) map (x => x.toDouble), v(idx), v2(idx), l(idx))
  }


  def rd_qts(fnm: String) = {
    val (data, hdrs) = csv.from_fl(fnm)
    val df = new frm()

    (0 until hdrs.size) foreach { i =>
      val (h, d) = (hdrs(i), data(i))
      if (h == "t") df.set_arg(h, csv.str2dte(d, "dd/MM/yyyy HH:mm:ss")) // timestamps
      else if (h.startsWith("ad") || h.startsWith("bd")) df.set_arg(h, d) //
      else df.set_arg(h, csv.str2dbl(d))
    }
    df
  }

  def rd_trd(fnm: String) = {
    val (data, hdrs) = csv.from_fl(fnm)
    val df = new frm()

    (0 until hdrs.size) foreach { i =>
      val (h, d) = (hdrs(i), data(i))
      if (h == "tradetimeUTC") df.set_arg(h, csv.str2dte(d, "yyyy-MM-dd HH:mm:ss")) // timestamps
      else if (h.startsWith("price") || h.startsWith("quantity")) df.set_arg(h, csv.str2dbl(d))
      else df.set_arg(h, d)
    }
    df
  }

  def main(argv: Array[String]): Unit = {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    val (instr, dte, ch) = ("RU00010YL3MS", "20191011", "CME")
    val df_qts = rd_qts("c:/tmp/tweb_book/RU00010YL3MS_CME_20191011_book_2.csv")
    val df_trd = rd_trd("c:/tmp/execs/RU00010YL3MS_20191011_CME_trades.csv")

    //    val dlrs = df_trds.dealer[String]
    //    val cme_trds = df_trds.slct(dlrs((x: String) => x == "CME"))

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")

    def add_qts(lvl: Int, c1: clr, c2: clr, f: frm, mid: Boolean = false): Unit = {
      val (tb, pb, sb, db) = cln(f.t[Long], f.get_arg[Double]("bp" + lvl), f.get_arg[Double]("bs" + lvl), f.get_arg[String]("bd" + lvl))
      val (ta, pa, sa, da) = cln(f.t[Long], f.get_arg[Double]("ap" + lvl), f.get_arg[Double]("as" + lvl), f.get_arg[String]("ad" + lvl))

      //val wmid =

      val ssb = (sb / sb.max * 18 + 2) map (_.toFloat)
      val ssa = (sa / sa.max * 18 + 2) map (_.toFloat)
      val xxb = (0 until sb.size).toArray map (i => db(i) + ":" + (sb(i).toInt / 1000000))
      val xxa = (0 until sa.size).toArray map (i => db(i) + ":" + (sa(i).toInt / 1000000))

      pp + tck(tb, pb, strk = c1)
      pp + tck(ta, pa, strk = c2)
      pp + sctr2(tb, pb, strk = c1, fll = c1, shp = >=, sz = ssb, ln_sz = ssb map { x => if (x > 10) 2f else 1f })
      pp + sctr2(ta, pa, strk = c2, fll = c2, shp = <=, sz = ssa, ln_sz = ssa map { x => if (x > 10) 2f else 1f })
      if (lvl < 13) {
        pp + lbls(tb, pb, xxb, sz = 12, xoff = 12, yoff = lvl * -12, clr = c1, rot = 30)
        pp + lbls(ta, pa, xxa, sz = 12, xoff = 12, yoff = lvl * 12, clr = c2, rot = 30)
      }
      if (mid) {
        val wmid = (pb * sa + pa * sb) / (sa + sb)
        pp + lne(tb, wmid, strk = clr(0x66666666), sz = 3)

      }
    }

    val (tt, tp, ts, td) = (df_trd.tradetimeUTC[Long] map (_.toDouble), df_trd.price[Double], df_trd.quantity[Double], df_trd.dealer[String])
    val (tts, ts_lbl) = ((ts / ts.max * 20 + 2) map (_.toFloat), (ts / 1000000) map (_.toString))
    val xxt = (0 until ts.size).toArray map (i => td(i) + ":" + (ts(i).toInt / 1000000))

    pp + vln(tt, clr(0x77aaaa00))

    add_qts(4, clr(0x33888811), clr(0x33118811), df_qts)
    add_qts(3, clr(0x33880011), clr(0x33110088), df_qts)
    add_qts(2, clr(0x55888811), clr(0x55118811), df_qts)
    add_qts(1, clr(0x66880011), clr(0x66110088), df_qts, true)

    pp + sctr2(tt, tp, strk = clr(0xff000000), fll = clr(0xaaffff11), shp = <>, sz = tts, ln_sz = 2)
    pp + lbls(tt, tp, xxt, sz = 12, xoff = 12, yoff = -12, clr = clr(0xff000000), rot = 30)

  }

}