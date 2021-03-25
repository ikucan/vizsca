package tweb2

import java.util.{Date, TimeZone}
import java.io.{File}

import ik.util.{csv, parq}
import ik.util.df._
import ik.util.ui.clr
import ik.util.vctr._
import ik.viz.dsl._
import ik.viz.shp2._


object quotes_test {

  def cln(t: Array[Long], v: Array[Double]) = {
    val idx = v((d: Double) => !d.isNaN)
    (t(idx), v(idx))
  }

  def cln(t: Array[Long], v: Array[Double], v2: Array[Double]) = {
    val idx = v((d: Double) => !d.isNaN)
    (t(idx) map (x => x.toDouble), v(idx), v2(idx))
  }

  def main(argv: Array[String]): Unit = {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

    //val (dt, instr, dlr, ch) = ("20200313", "RU00010YL3MS", "GS", "CME")
    //val (dt, instr, dlr, ch) = ("20200325", "RU00010YL3MS", "MIZU", "CME")
    //val (dt, instr, ch) = ("20200311", "RE00010YE6MA", "LCH")
    //val (dt, instr, dlr, ch) = ("20200327", "RP00010YL6MS", "DB", "CME")
    var (dt, instr, dlr, ch) = ("", "", "", "")

    val fnm =
      if (argv.size < 1) {
        f"v:/tradeweb/capture/data/${dt}/quote/${instr}_${dlr}_${ch}.csv"
      }
      else
        argv(0)

    {
      // parse the file name to get the details of date
      val ffn = new File(fnm).getName.replace(".csv", "")
      dt =  new File(fnm).getParentFile.getParentFile.getName
      ch  = ffn.replaceFirst(".*\\_", "")
      val stub = ffn.replace("_" + ch,"")
      instr = stub.replaceFirst("_[A-Z]{1,5}", "")
      dlr = stub.replaceFirst("[A-z, 0-9]{1,15}_", "")
      println(f"xxx:>> ${dt} :: ${ffn} :: ch=${ch} :: stub: ${stub} :: inst: ${instr} :: dlr: ${dlr}")
    }

    val (data, hdrs) = csv.from_fl(fnm)
    val df = new frm()

    (0 until hdrs.size) foreach { i =>
      val (h, d) = (hdrs(i), data(i))
      if (h == "msg_datetime" || h == "btm" || h == "atm") df.set_arg(h, csv.str2dte(d, "yyyy-MM-dd HH:mm:ss"))
      else if (h == "msg_timestamp") df.set_arg(h, csv.str2dte(d, "yyyy-MM-dd HH:mm:ss.SSS"))
      else if (h == "bid" || h == "bsz" || h == "ask" || h == "asz") df.set_arg(h, csv.str2dbl(d))
      else if (h == "dlr") df.set_arg(h, d)
      else println(f"ignoring field : ${h}")
    }

    val pp = plt(x = 10, y = 10, w = 2300, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")

    val (tb, pb, sb) = cln(df.get_arg[Long]("msg_datetime"), df.get_arg[Double]("bid"), df.get_arg[Double]("bsz"))
    val (ta, pa, sa) = cln(df.get_arg[Long]("msg_datetime"), df.get_arg[Double]("ask"), df.get_arg[Double]("asz"))

    val bid_9_idx = pb === -999999
    val ask_9_idx = pb === -999999
    val bid_ok_idx = !bid_9_idx
    val ask_ok_idx = !ask_9_idx

    val (tbb, pbb, sbb) = (tb(bid_ok_idx), pb(bid_ok_idx), sb(bid_ok_idx))
    val (taa, paa, saa) = (ta(ask_ok_idx), pa(ask_ok_idx), sa(ask_ok_idx))

    val ssb = (sbb / sbb.max * 10 + 2) map (_.toFloat)
    val ssa = (saa / saa.max * 10 + 2) map (_.toFloat)

//    pp + tck(tbb, pbb, strk = clr(0x66880011))
//    pp + tck(taa, paa, strk = clr(0x66110088))
    pp + sctr2(tbb, pbb, strk = clr(0x66880011), fll = clr(0x66880011), shp = >=, sz = ssb, ln_sz = ssb map { x => if (x > 10) 2f else 1f })
    pp + sctr2(taa, paa, strk = clr(0x66110088), fll = clr(0x66110088), shp = <=, sz = ssa, ln_sz = ssa map { x => if (x > 10) 2f else 1f })

    pp + vln(tb(bid_9_idx), strk = clr(0x66880011), sz = 2)
    pp + vln(ta(ask_9_idx), strk = clr(0x66110088), sz = 2)

    pp + ttl(f"quotes:  ${dt} : ${instr} / ${dlr} / ${ch}")
  }

}