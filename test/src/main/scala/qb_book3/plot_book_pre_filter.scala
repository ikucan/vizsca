package qb_book3

import ik.util.csv
import ik.util.df._
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.dsl._
import ik.viz.shp2._
import java.util.TimeZone

object plot_book_pre_filter {

  def cln(t: Array[Long], v: Array[Double]) = {
    val idx = v((d: Double) => !d.isNaN)
    (t(idx), v(idx))
  }

  def cln(t: Array[Long], v: Array[Double], v2: Array[Double], l: Array[String]) = {
    //val idx = v((d: Double) => !d.isNaN)
    val idx = v((d: Double) => d > -9999)
    (t(idx) map (x => x.toDouble), v(idx), v2(idx), l(idx))
  }

  def main(argv: Array[String]): Unit = {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    //val root = "w:/tweb/book3/"
    val root = "c:/tmp/enablements_test/post/"
    val (dt, inst) = ("20201125", "RU00010YL3MS")

    val fnm = if (argv.size < 1)
      f"${root}/${dt}/${inst}_book.csv"
    else
      argv(0)

    println(f"Plotting book in file: ${fnm}")

    val (data, hdrs) = csv.from_fl(fnm)
    val df = new frm()

    (0 until hdrs.size) foreach { i =>
      val (h, d) = (hdrs(i), data(i))
      if (h == "t" || h == "st" || h == "t_ny") df.set_arg(h, csv.str2dte(d, "yyyy-MM-dd HH:mm:ss"))
      else if (h == "date") df.set_arg(h, csv.str2dte(d, "yyyy-MM-dd"))
      else if (h.startsWith("sym") || h.startsWith("ad") || h.startsWith("bd")) df.set_arg(h, d)
      else
        try {
          df.set_arg(h, csv.str2dbl(d))
        }
        catch {
          case re: Exception =>
            println(f"failed to convert field ${h}")
            throw re
        }
    }

    val t = df.t[Long]
    val bp1 = df.bp1[Double]
    val ap1 = df.ap1[Double]

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")
    pp + ttl(fnm)

    def add(lvl: Int, c1: clr, c2: clr, f: frm): Unit = {
//      val (tb, pb, sb, db) = cln(f.t_ny[Long], f.get_arg[Double]("bp" + lvl), f.get_arg[Double]("bs" + lvl), f.get_arg[String]("bd" + lvl))
//      val (ta, pa, sa, da) = cln(f.t_ny[Long], f.get_arg[Double]("ap" + lvl), f.get_arg[Double]("as" + lvl), f.get_arg[String]("ad" + lvl))
      val (tb, pb, sb, db) = cln(f.t[Long], f.get_arg[Double]("bp" + lvl), f.get_arg[Double]("bs" + lvl), f.get_arg[String]("bd" + lvl))
      val (ta, pa, sa, da) = cln(f.t[Long], f.get_arg[Double]("ap" + lvl), f.get_arg[Double]("as" + lvl), f.get_arg[String]("ad" + lvl))

      if (sb.size > 0) {
        val ssb = (sb / sb.max * 18 + 2) map (_.toFloat)
        val ssa = (sa / sa.max * 18 + 2) map (_.toFloat)
        pp + tck(tb, pb, strk = c1)
        pp + tck(ta, pa, strk = c2)
        pp + sctr2(tb, pb, strk = c1, fll = c1, shp = >=, sz = ssb, ln_sz = ssb map { x => if (x > 10) 2f else 1f })
        pp + sctr2(ta, pa, strk = c2, fll = c2, shp = <=, sz = ssa, ln_sz = ssa map { x => if (x > 10) 2f else 1f })
        if (lvl < 13) {
          pp + lbls(tb, pb, db, sz = 10, xoff = 5 + lvl * 2, yoff = -5, rot = -45)
          pp + lbls(ta, pa, da, sz = 10, xoff = 5 + lvl * 2, yoff = 5, rot = 45)
        }
      }
    }

    //    pp + tck(t, bp1, strk = )
    //    pp + tck(t, ap1, strk = clr(0x66880011))

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