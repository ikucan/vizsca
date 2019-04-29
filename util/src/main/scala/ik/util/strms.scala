package ik.util

import java.io.InputStream
import java.io.OutputStream
import java.io.File
import java.io.FileInputStream

/**
 * a stream helper
 */
object strms {
  /**
   * drain the input stream into the output stream
   */
  def drain(is: InputStream, os: OutputStream, bsz: Int = 32) = {
    val bff = new Array[Byte](bsz)
    var n = is.read(bff)
    while (n > 0) {
      os.write(bff, 0, n)
      n = is.read(bff)
    }
  }

  def txt(in: File): String = {
    if (!in.exists) throw new util_err("file does not exist:>> " + in.getCanonicalPath)
    val is = new FileInputStream(in)
    val res = txt(is)
    is.close
    res
  }

  def txt(is: InputStream): String = {
    val out = new StringBuilder(1024)
    val bff = new Array[Byte](32)
    var n = is.read(bff)
    while (n > 0) {
      out ++= new String(bff, 0, n)
      n = is.read(bff)
    }
    out.toString
  }

}