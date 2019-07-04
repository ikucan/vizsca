package ik.util.parq

import java.io.FileInputStream
import org.apache.parquet.io.DelegatingSeekableInputStream


object pqrq_tst {

  def read_fl(fl: String) = {
    val is = new DelegatingSeekableInputStream(new FileInputStream(fl)) {

      override def getPos: Long = 123l

      override def seek(newPos: Long): Unit = {}
    }


  }

  def main(argv: Array[String]): Unit = {



    println(1232)
  }
}
