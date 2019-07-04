package ik.smplng

import ik.util.csv
import ik.util.df._
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.dsl._
import ik.viz.shp2._
import java.util.TimeZone

object as_of_smpl_tst {


  def cln(t: Array[Long], v: Array[Double]) = {
    val idx = v((d: Double) => !d.isNaN)
    (t(idx), v(idx))
  }

  def main(argv: Array[String]): Unit = {

    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    val (smpls, smpl_hdrs) = csv.from_fl(s"c:/tmp/smpls.csv")
    val (raw, raw_hdrs) = csv.from_fl(s"c:/tmp/raw.csv")

    println(smpl_hdrs.toList)
    println(raw_hdrs.toList)


    val df_raw = new frm()
    df_raw.set_arg("t", csv.str2inst(raw(1)))
    df_raw.set_arg("prc", csv.str2dbl(raw(2)))

    val df_smpl = new frm()
    df_smpl.set_arg("t_smpl", csv.str2inst(smpls(0)))
    df_smpl.set_arg("t", csv.str2inst(smpls(1)))
    df_smpl.set_arg("prc", csv.str2dbl(smpls(2)))

    //    df_raw.set_arg(h, csv.str2dte(d, "yyyy-MM-dd HH:mm:ss"))
    //
    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.##")

    val (tb, pb) = cln(df_raw.t[Long], df_raw.prc[Double])
    pp + vln(df_smpl.t_smpl[Long], strk = clr(0x8899bb99))
    pp + tck(tb, pb, strk = clr(0xcc881111))
    pp + sctr2(tb map (x => x.toDouble), pb, strk = clr(0xff881111), fll = clr(0xff881111), shp = x, sz = 6f, ln_sz = 2f)
    pp + sctr2(df_smpl.t_smpl[Long], df_smpl.prc[Double], strk = clr(0x99111188), fll = clr(0x99111188), shp = dot, sz = 12f, ln_sz = 2f)
    pp + sctr2(df_smpl.t[Long], df_smpl.prc[Double], strk = clr(0x99111188), fll = clr(0x99111188), shp = o, sz = 12f, ln_sz = 2f)

    //
    //    def add(brkr: String, c: clr, c2: clr, sdf: frm): Unit = {
    //      val (tb, pb) = cln(sdf.time[Long], sdf.bidPrice[Double])
    //      val (ta, pa) = cln(sdf.time[Long], sdf.askPrice[Double])
    //      pp + tck(tb, pb, strk = c)
    //      pp + tck(ta, pa, strk = c)
    //      pp + sctr2(tb map (x => x.toDouble), pb, strk = c2, fll = c, shp = >=, sz = 8f, ln_sz = 1f)
    //      pp + sctr2(ta map (x => x.toDouble), pa, strk = c2, fll = c, shp = <=, sz = 8f, ln_sz = 1f)
    //    }
    //
    //    brkrs.dstnct foreach { brkr =>
    //      println(brkr)
    //      val idx = brkrs((x: String) => x == brkr)
    //      val sdf = df.slct(idx)
    //
    //      if (brkr == "JPM") add(brkr, clr(0x66110088), clr(0xdd110055), sdf)
    //      if (brkr == "BAML") add(brkr, clr(0x66880011), clr(0xdd550011), sdf)
    //      if (brkr == "GS") add(brkr, clr(0x66008811), clr(0xdd005511), sdf)
    //      if (brkr == "SG") add(brkr, clr(0x66888811), clr(0xdd555511), sdf)
    //    }
    //
    //    pp + ttl(s"Broker breakdown: ${instr}, ${date}")

    Thread.sleep(2000)
  }
}