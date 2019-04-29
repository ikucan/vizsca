package ik.util.hdf

import java.io.File

/**
 * @author iztokkucan
 */
object tst {
  def main(argv: Array[String]): Unit = {
    println(" START ")
    
   //println(System.load("/workstem/g22/g2/jvm/util/libjri.jnilib"))

    ///println(System.getenv("PATH"))
    //println(System.getenv("LD_LIBRARY_PATH"))
    //println(System.getenv("R_HOME"))
    //System.setenv("R_HOME", "/Library/Frameworks/R.framework/Resources")
    ///sprintln(System.getenv("R_HOME"))
    println(System.getProperty("java.library.path"))
    //System.setProperty("java.library.path", System.getenv("LD_LIBRARY_PATH"))
    //println(System.getProperty("java.library.path"))

    
    val fn = "ut1.hdf5"
    val f0 = h_fle(fn)
    f0.cls
    val f1 = h_fle(fn, true)
    f1.cls
    //new File(fn).delete
    println(" ut1 >>> " + h_lib.n_opn)
    println(" STOP ")

  }
}