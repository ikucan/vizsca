package ik.util.r

import ik.util.df.frm
import ik.util.r._

object tst {
  def main_(argv: Array[String]): Unit = {
    println(123)
  }

  def main(argv: Array[String]): Unit = main_1(argv)

  def main_2(argv: Array[String]): Unit = {
    try {
      val f = new frm()(a = Array(1, 2, 3), b = Array(2.0, 3.0, 4.0), s = Array("a", "b", "c"), boo = Array(true, false, true))
      r("install.packages('ggplot2')")
      r("print('------')")
      Thread.sleep(10000)
      System.exit(0)
    } catch {
      case x: Throwable => x.printStackTrace
    } finally {
      r.end
    }
  }

  def main_1(argv: Array[String]): Unit = {
    try {
      val f = new frm()(a = Array(1, 2, 3), b = Array(2.0, 3.0, 4.0), s = Array("a", "b", "c"), boo = Array(true, false, true))
      gd("a b c")
      r ! ("'df'", f)
      r("print(ls())")
      r("print(df)")
      r("print(qplot(1,2,3))")
      println(123)
      Thread.sleep(10000)
      gd.off
      System.exit(0)
    } catch {
      case x: Throwable => x.printStackTrace
    } finally {
      r.end
    }
  }

}
