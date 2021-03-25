package tweb2

import java.util.{Date, TimeZone}

import ik.util.{csv, parq}
import ik.util.df._
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.dsl._
import ik.viz.shp2._


object book_test {

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
    //val (dt, instr, ch) = ("20200327", "RU00010YL3MS", "CME")
    //val (dt, instr, ch) = ("20200124", "RE00010YE6MA", "LCH")
    val (dt, instr, ch) = ("20200327", "RP00010YL6MS", "LCH")
    val fnm = if (argv.size < 1) {
      //"e:/tmp/tweb_md/out/20200225/book/RU00010YL3MS_CME.csv"
      f"v:/tradeweb/capture/data/${dt}/book/${instr}_${ch}.csv"
    }
    else
      argv(0)

    // -9999999     val fnm = "w:/tweb/book2/20191029/RP00010YL6MS_book.csv"
    val (data, hdrs) = csv.from_fl(fnm)
    val df = new frm()

    (0 until hdrs.size) foreach { i =>
      val (h, d) = (hdrs(i), data(i))
      if (h == "t" || h.startsWith("bt") || h.startsWith("at")) df.set_arg(h, csv.str2dte(d, "yyyy-MM-dd HH:mm:ss.SSS")) // timestamps
      else if (h.startsWith("bp") || h.startsWith("ap") || h.startsWith("bs") || h.startsWith("as")) df.set_arg(h, csv.str2dbl(d))
      else if (h.startsWith("bd") || h.startsWith("ad")) df.set_arg(h, d)
      else throw new Exception(f"unknown exception: ${h}")
    }

    //    val t = df.t[Long]
    //    val bp1 = df.bs1[Double]
    //    val ap1 = df.as1[Double]

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")

    def add(lvl: Int, c1: clr, c2: clr, f: frm): Unit = {
      val (tb, pb, sb, db) = cln(f.get_arg[Long]("t"), f.get_arg[Double]("bp" + lvl), f.get_arg[Double]("bs" + lvl), f.get_arg[String]("bd" + lvl))
      val (ta, pa, sa, da) = cln(f.get_arg[Long]("t"), f.get_arg[Double]("ap" + lvl), f.get_arg[Double]("as" + lvl), f.get_arg[String]("ad" + lvl))

      val ssb = (sb / sb.max * 18 + 2) map (_.toFloat)
      val ssa = (sa / sa.max * 18 + 2) map (_.toFloat)
      pp + tck(tb, pb, strk = c1)
      pp + tck(ta, pa, strk = c2)
      pp + sctr2(tb, pb, strk = c1, fll = c1, shp = >=, sz = ssb, ln_sz = ssb map { x => if (x > 10) 2f else 1f })
      pp + sctr2(ta, pa, strk = c2, fll = c2, shp = <=, sz = ssa, ln_sz = ssa map { x => if (x > 10) 2f else 1f })
      if (lvl < 10) {
        pp + lbls(tb, pb, db, sz = 10, xoff = 5 + lvl * 2, yoff = -5, rot = -45)
        pp + lbls(ta, pa, da, sz = 10, xoff = 5 + lvl * 2, yoff = 5, rot = 45)
      }
    }

    pp + ttl(f"book:  ${dt} : ${instr}/${ch}")

    add(1, clr(0x66880011), clr(0x66110088), df)
    add(2, clr(0x66888811), clr(0x66118811), df)
    add(3, clr(0x66880011), clr(0x66110088), df)
    add(4, clr(0x66888811), clr(0x66118811), df)
    add(5, clr(0x66880011), clr(0x66110088), df)
    add(6, clr(0x66888811), clr(0x66118811), df)
    add(7, clr(0x66880011), clr(0x66110088), df)
    add(8, clr(0x66888811), clr(0x66118811), df)
    add(9, clr(0x66880011), clr(0x66110088), df)
    add(10, clr(0x66888811), clr(0x66118811), df)
  }

}