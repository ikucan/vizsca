package ik.util.df

import org.scalatest.FlatSpec

import ik.util.log.loggd

class frm_tst extends FlatSpec with loggd {

  "frm" should " t " in {

    val N = 1024
    val ai = (0 until N) toArray;
    val al = ai map (_.toLong)
    val ad = ai map (_.toDouble)

    val f = new frm()
    f(a = ai, b = al)
    val aai = f.a[Int]

    assert(ai.deep == aai.deep)

  }
}