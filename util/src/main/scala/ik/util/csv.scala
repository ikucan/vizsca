package ik.util

import scala.collection.mutable.ArrayBuffer
import java.io._
import java.text.SimpleDateFormat
import java.time._
import java.time.format._

class csv_err(msg: String, cause: Throwable) extends RuntimeException(msg, cause) {
  def this(msg: String) = this(msg, null)
}

/**
  * simple csv parser
  */
object csv {

  def str2dbl(vals: Array[String]) = vals map (x => if (x.size == 0) Double.NaN else if (x == "NaN") Double.NaN else if (x == "Inf") Double.PositiveInfinity else if (x == "-Inf") Double.NegativeInfinity else x.toDouble)

  def str2dte(vals: Array[String], frmt: String) = {
    val fmt = new SimpleDateFormat(frmt)
    vals map { x => if (x.size == 0) 0 else fmt.parse(x).getTime }
  }

  def str2inst(vals: Array[String], frmt: Option[String] = None, tz_offst: Int = 0) = {
    if (frmt == None) {
      vals map { x => if (x.size == 0) 0 else Instant.parse(x).toEpochMilli() }
    } else {
      val fmt = DateTimeFormatter.ofPattern(frmt.get)
      vals map { x => if (x.size == 0) 0 else LocalDateTime.parse(x, fmt).toInstant(ZoneOffset.ofHours(tz_offst)).toEpochMilli() }
    }
  }

  val dlm = ','

  def prs_ln(s: String) = {
    val tkns = new ArrayBuffer[String]
    var (i0, i1) = (0, s.indexOf(dlm))
    while (i1 > -1) {
      tkns += s.substring(i0, i1)
      i0 = i1 + 1
      i1 = s.indexOf(dlm, i0)
    }
    tkns += s.substring(i0, s.size)
    tkns.toArray
  }

  def from_fl(fle: String, rd_hdr: Boolean = true): (Array[Array[String]], Array[String]) = {
    val rdr = new BufferedReader(new FileReader(fle))
    var lne = rdr.readLine()
    val (bffrs, hdrs) =
      if (lne != null) {
        val tkns = prs_ln(lne)
        val bffs = (tkns map (x => new ArrayBuffer[String])) toArray;
        if (rd_hdr) {
          lne = rdr.readLine()
          (bffs, tkns)
        } else
          (bffs, (0 until tkns.size) map (_.toString) toArray)
      } else {
        (Array[ArrayBuffer[String]](), Array[String]())
      }
    while (lne != null) {
      val tkns = prs_ln(lne)
      if (tkns.size != hdrs.size)
        throw new csv_err(s"# tokens on line: ${tkns.size} doesn't match the number of tokens required: ${hdrs.size}")
      (0 until tkns.size) foreach { i => bffrs(i) += tkns(i) }
      lne = rdr.readLine()
    }
    (bffrs map (_.toArray), hdrs)
  }

}