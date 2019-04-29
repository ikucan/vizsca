package ik.util

import scala.collection.mutable.ArrayBuffer
/**
 * simple csv parser
 */
object csv {
  val dlm = ','
  def prs(s: String) = {
    val tkns = new ArrayBuffer[String]
    var (i0, i1) = (0, s.indexOf(dlm))
    while (i1 > -1) {
      tkns += s.substring(i0, i1)
      i0 = i1 + 1
      i1 = s.indexOf(dlm, i0)
    }
    tkns.toArray
  }
}