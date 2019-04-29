package ik.util

import java.io.File
import java.text.SimpleDateFormat
import java.io._

object fle {

  def sve(s: String, f: String) = {
    val fos = new FileOutputStream(f)
    fos.write(s.getBytes)
    fos.close
  }

}
