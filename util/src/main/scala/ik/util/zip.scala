package ik.util

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

/**
 * zip stream.
 */
object zip {

  def unpck(zf: File, of: File) = {
    val fzp = new ZipFile(zf)
    val os = new BufferedOutputStream(new FileOutputStream(of))
    val zes = fzp.entries
    while (zes.hasMoreElements) {
      val ze = zes.nextElement
      val ze_is = fzp.getInputStream(ze)
      strms.drain(ze_is, os, 1024 * 1024)
    }

    os.close
    fzp.close
  }

}