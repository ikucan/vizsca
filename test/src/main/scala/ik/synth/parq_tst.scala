package ik.synth

import ik.util.parq

import ik.viz.dsl._
import ik.util.ui.clr
import ik.util.df._
import ik.util.vctr._
import ik.util.csv
import ik.viz.dot2

import ik.viz.shp2._


object parq_tst {


  def main(argv: Array[String]): Unit = {
    val fnm = "/workstem/py_tst/pds_tst_sml.parquet"
    val df = parq.rd(fnm)
    println(df.col_nms toList)
    println(df.typs toList)

    val pp = plt(x = 10, y = 10, w = 1500, h = 800) + wmrk()
    pp + grd(false, true) + x_axs_dttm() + y_axs("#.#####")
    pp + sctr2(df.x_deg[Long] map (_.toDouble),df.sin[Double],  strk = clr(0xffff0000), fll = clr(0xffff0000), shp = x, sz = 8f, ln_sz = 1f)
    pp + sctr2(df.x_deg[Long] map (_.toDouble),df.cos[Double],  strk = clr(0xff0000ff), fll = clr(0xff0000ff), shp = x, sz = 8f, ln_sz = 1f)


  }
}