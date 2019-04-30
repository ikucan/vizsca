import ik.viz._
import ik.viz.dsl._
import math.sin
import ik.util.ui.clr

object basic_tst {

  def main(argv: Array[String]): Unit = {
    val x = (-1000 to 1000).toArray map (_.toDouble)
    val sinx = x map (x => sin(x * math.Pi / 180))

    val pp = plt(x = 10, y = 10, w = 2500, h = 1200) + wmrk()
    pp + grd(false, true) + x_axs_tm()
    // grid
    //pp + vln(tg1, strk = clr(0xaaaaaaaa))
    // ticks
    pp + lne(x, sinx, strk = clr(0x66110088))
    //pp + lne(t, b, strk = clr(0x66880011))
    //pp + sctr(t, a, shp = pls(6), strk = clr(0x66110088))
    //pp + sctr(t, b, shp = pls(6), strk = clr(0x66880011))
    // trades
    //pp + sctr(tt, pt, shp = <>(5), strk = clr(0x77222222))
    //TODO :>>
    //pp + sctr(tt, pt, shp = dot(5), strk = clr(0x55662222))
    //    // orders
    //    pp + sctr(esot, esop, shp = o(8), strk = clr(0xff2200aa))
    //    pp + sctr(esft, esfp, shp = dot(8), strk = clr(0xff2200aa))
    //    pp + sctr(esftx, esfp, shp = dot(8), strk = clr(0xffaaaa00))
    //    pp + ln_seg.hrz(esfp, esft, esftx, strk = clr(0x11aaaa22), typ = ln_typ.-, sz = 1)
    //
    //    pp + sctr(xbot, xbop, shp = bx(8), strk = clr(0xffff00ff))
    //    pp + sctr(xbft, xbfp, shp = bx(8), strk = clr(0xffaa0022))
    //    pp + sctr(xbftx, xbfp, shp = bx(10), strk = clr(0xff11cc11))
    //    pp + ln_seg.hrz(xbfp, xbft, xbftx, strk = clr(0x66aa0022), typ = ln_typ.-, sz = 1)
    pp + ttl("Test plot")
  }

}