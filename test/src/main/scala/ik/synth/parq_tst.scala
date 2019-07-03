package ik.synth

import ik.util.parq


object parq_tst {


  def main(argv: Array[String]): Unit = {
    val fnm = "/workstem/py_tst/pds_tst_sml.parquet"
    parq.rd(fnm)


  }
}