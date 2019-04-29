package ik.util

import java.io.BufferedReader
import java.io.InputStreamReader

import scala.Array.canBuildFrom

import org.rosuda.JRI.RMainLoopCallbacks
import org.rosuda.JRI.Rengine
import ik.util.df.frm

package object r {

  class cnsl extends RMainLoopCallbacks with ik.util.log.loggd {
    def rBusy(e: Rengine, which: Int): Unit = {}
    def rChooseFile(e: Rengine, fle: Int): String = { null }
    def rFlushConsole(e: Rengine): Unit = {}
    def rLoadHistory(e: Rengine, fnm: String): Unit = {}

    def rReadConsole(e: Rengine, prmpt: String, add2hst: Int): String = {
      println("++++++++++++++++++++++++++++++++++++")
      try {
        val br = new BufferedReader(new InputStreamReader(System.in))
        val s = br.readLine()
        if (s == null || s.length == 0) s
        else s + "\n"
      } catch {
        case t: Throwable => log_err("error caught: ", t)
      }
      null
    }

    def rSaveHistory(e: Rengine, fnm: String): Unit = {}
    def rShowMessage(e: Rengine, msg: String): Unit = {}
    def rWriteConsole(e: Rengine, txt: String, add2hst: Int): Unit = print(txt)
  }

  /**
   * an encapsulation of an R interface
   */
  object r extends ik.util.log.loggd {

    if (!Rengine.versionCheck) throw new r_ex()
    log_dbg("r version ok  " + Rengine.versionCheck)
    val re = new Rengine(Array("--no-save"), false, new cnsl());
    if (!re.waitForR) log_err("Cannot load R");

    /**
     * end the R session
     */
    def end = re.end
    def end(to: Long) = {
      Thread.sleep(to)
      re.end
      gd.off
    }

    /**
     * evaluate an R expression
     */
    def apply(r: String) = re.eval(r)

    /**
     * set an R value and query for a value
     */
    def !(nm: String, v: Array[Int]) = re.assign(nm, v)
    def !(nm: String, v: Array[Double]) = re.assign(nm, v)
    def !(nm: String, v: Array[String]) = re.assign(nm, v)
    def !(nm: String, v: Array[Boolean]) = re.assign(nm, v)

    def ?(ex: String) = re.eval(ex).asDoubleArray

    def !(nm: String, f: frm): Unit = {
      val c1 = new StringBuilder(nm + "<-data.frame(")
      val c2 = new StringBuilder("rm(")
      val tm = f.typ_map
      tm foreach { c =>
        val tmp_nm = "zzz___" + c._1 + "___zzz"
        c match {
          case (n, Boolean)   => this ! (tmp_nm, f.get_arg[Boolean](n))
          case (n, Char)      => this ! (tmp_nm, f.get_arg[Char](n) map (_.toString))
          case (n, Byte)      => this ! (tmp_nm, f.get_arg[Byte](n) map (_.toInt))
          case (n, Short)     => this ! (tmp_nm, f.get_arg[Short](n) map (_.toInt))
          case (n, Int)       => this ! (tmp_nm, f.get_arg[Int](n))
          case (n, Long)      => this ! (tmp_nm, f.get_arg[Long](n) map (_.toDouble))
          case (n, Float)     => this ! (tmp_nm, f.get_arg[Float](n) map (_.toDouble))
          case (n, Double)    => this ! (tmp_nm, f.get_arg[Double](n))
          case (n, Symbol)    => this ! (tmp_nm, f.get_arg[Symbol](n) map (_.name))
          case (n, s: String) => this ! (tmp_nm, f.get_arg[String](n))
          case (x, y)         => throw new r_ex("BUG: column " + x + " in frame has unexpected R type")
        }
        if (c != tm.head) { c1 ++= ","; c2 ++= "," }
        c1 ++= (c._1 + "=" + tmp_nm); c2 ++= tmp_nm
      }
      c1 ++= ")"; c2 ++= ")"
      this(c1.toString); this(c2.toString)
    }

  }
}
