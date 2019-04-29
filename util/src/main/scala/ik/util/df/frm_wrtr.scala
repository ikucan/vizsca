package ik.util.df

import java.util.zip.GZIPOutputStream
import java.util.zip.Deflater
import java.io.ByteArrayOutputStream
import java.nio.ByteOrder.LITTLE_ENDIAN
import java.nio.ByteBuffer
import java.io.RandomAccessFile
import java.io.ByteArrayOutputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.Inflater
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuilder
import scala.collection.mutable.ArrayBuffer
import java.nio.channels.FileChannel
import java.nio.ByteOrder
import java.io.File

/**
 * write compoenent of persistence
 */
trait frm_wrtr {
  f: frm =>

  def ssve(fn: String)(implicit cf: Boolean = true) = {
    val (cs, ts) = (f.col_nms, f.typs)
    val cmpr_sym = Future { sym_cols.par map { a => val (n, c) = dflt(a._2); (("sym:" + a._1 + ":" + a._2.size + ":" + n + ":" + cf + ":" + c.size + ":").getBytes -> c) }seq }
    val cmpr_str = Future { str_cols.par map { a => val (n, c) = dflt(a._2); (("str:" + a._1 + ":" + a._2.size + ":" + n + ":" + cf + ":" + c.size + ":").getBytes -> c) }seq }
    val cmpr_bol = Future { bol_cols.par map { a => val (n, c) = dflt(a._2); (("bol:" + a._1 + ":" + a._2.size + ":" + n + ":" + cf + ":" + c.size + ":").getBytes -> c) }seq }
    val cmpr_chr = Future { chr_cols.par map { a => val (n, c) = dflt(a._2); (("chr:" + a._1 + ":" + a._2.size + ":" + n + ":" + cf + ":" + c.size + ":").getBytes -> c) }seq }
    val cmpr_byt = Future { byt_cols.par map { a => val (n, c) = dflt(a._2); (("byt:" + a._1 + ":" + a._2.size + ":" + n + ":" + cf + ":" + c.size + ":").getBytes -> c) }seq }
    val cmpr_sht = Future { sht_cols.par map { a => val (n, c) = dflt(a._2); (("sht:" + a._1 + ":" + a._2.size + ":" + n + ":" + cf + ":" + c.size + ":").getBytes -> c) }seq }
    val cmpr_int = Future { int_cols.par map { a => val (n, c) = dflt(a._2); (("int:" + a._1 + ":" + a._2.size + ":" + n + ":" + cf + ":" + c.size + ":").getBytes -> c) }seq }
    val cmpr_lng = Future { lng_cols.par map { a => val (n, c) = dflt(a._2); (("lng:" + a._1 + ":" + a._2.size + ":" + n + ":" + cf + ":" + c.size + ":").getBytes -> c) }seq }
    val cmpr_flt = Future { flt_cols.par map { a => val (n, c) = dflt(a._2); (("flt:" + a._1 + ":" + a._2.size + ":" + n + ":" + cf + ":" + c.size + ":").getBytes -> c) }seq }
    val cmpr_dbl = Future { dbl_cols.par map { a => val (n, c) = dflt(a._2); (("dbl:" + a._1 + ":" + a._2.size + ":" + n + ":" + cf + ":" + c.size + ":").getBytes -> c) }seq }

    // save to file
    if (new File(fn).exists) new File(fn).delete
    val ch = new RandomAccessFile(fn, "rw").getChannel
    Await.result(cmpr_sym, 1.hour) foreach { x => wrt(ch, x) }
    Await.result(cmpr_str, 1.hour) foreach { x => wrt(ch, x) }
    Await.result(cmpr_bol, 1.hour) foreach { x => wrt(ch, x) }
    Await.result(cmpr_chr, 1.hour) foreach { x => wrt(ch, x) }
    Await.result(cmpr_byt, 1.hour) foreach { x => wrt(ch, x) }
    Await.result(cmpr_sht, 1.hour) foreach { x => wrt(ch, x) }
    Await.result(cmpr_int, 1.hour) foreach { x => wrt(ch, x) }
    Await.result(cmpr_lng, 1.hour) foreach { x => wrt(ch, x) }
    Await.result(cmpr_flt, 1.hour) foreach { x => wrt(ch, x) }
    Await.result(cmpr_dbl, 1.hour) foreach { x => wrt(ch, x) }
    ch.force(true)
    ch.close
  }

  /**
   * write the header part and compressed buffer file to the actual output stream
   */
  private def wrt(ch: FileChannel, hdr_bff: (Array[Byte], Array[Byte])) = {
    ch.write(ByteBuffer.wrap(hdr_bff._1))
    ch.write(ByteBuffer.wrap(hdr_bff._2))
  }

  /**
   * serialize arrays of type to array of byte and compress
   * these could all be written like this:
   *       private def dflt(v: Array[Long])(implicit c: Boolean) = cmprs_byts((ByteBuffer.allocate(v.size * 8) /: v) { (bb, v) => bb.putLong(v) } array)
   *  but are put into illegible while loops for performance (~10% better than above)
   */
  private def dflt(v: Array[Boolean])(implicit c: Boolean) = {
    val ba = new Array[Byte](v.size)
    var i = 0; while (i < v.size) { ba(i) = if (v(i)) 1.toByte else 0.toByte; i += 1 }
    cmprs_byts(ba.array)
  }
  private def dflt(v: Array[Byte])(implicit c: Boolean) = cmprs_byts(v)
  private def dflt(v: Array[Char])(implicit c: Boolean) = {
    val bb = ByteBuffer.allocate(v.size * 2)
    var i = 0; while (i < v.size) { bb.putChar(v(i)); i += 1 }
    cmprs_byts(bb.array)
  }
  private def dflt(v: Array[Short])(implicit c: Boolean) = {
    val bb = ByteBuffer.allocate(v.size * 2)
    var i = 0; while (i < v.size) { bb.putShort(v(i)); i += 1 }
    cmprs_byts(bb.array)
  }
  private def dflt(v: Array[Int])(implicit c: Boolean) = {
    val bb = ByteBuffer.allocate(v.size * 4)
    var i = 0; while (i < v.size) { bb.putInt(v(i)); i += 1 }
    cmprs_byts(bb.array)
  }
  private def dflt(v: Array[Long])(implicit c: Boolean) = {
    val bb = ByteBuffer.allocate(v.size * 8)
    var i = 0; while (i < v.size) { bb.putLong(v(i)); i += 1 }
    cmprs_byts(bb.array)
  }
  private def dflt(v: Array[Float])(implicit c: Boolean) = {
    val bb = ByteBuffer.allocate(v.size * 4)
    var i = 0; while (i < v.size) { bb.putFloat(v(i)); i += 1 }
    cmprs_byts(bb.array)
  }
  private def dflt(v: Array[Double])(implicit c: Boolean) = {
    val bb = ByteBuffer.allocate(v.size * 8)
    var i = 0; while (i < v.size) { bb.putDouble(v(i)); i += 1 }
    cmprs_byts(bb.array)
  }
  private def dflt(v: Array[Symbol])(implicit c: Boolean) = {
    val (smap, sb) = (new ArrayBuffer[(Symbol, Int)](), new StringBuilder)
    var (s, n, i) = (v(0), 1, 1)
    while (i < v.size) {
      if (s == v(i)) n += 1
      else { smap += ((s, n)); s = v(i); n = 1 }
      i += 1
    }
    smap += ((s, n))
    i = 0; while (i < smap.size) {
      val (sym, num) = smap(i)
      sb ++= sym.name; sb += '\u0000'; sb ++= num.toString; sb += '\u0000'
      i += 1
    }
    cmprs_byts(sb.toString.getBytes)

  }
  private def dflt(v: Array[String])(implicit c: Boolean) = cmprs_byts((new StringBuilder /: v) { (sb, s) => sb ++= s; sb += '\u0000' }.toString.getBytes)

  private def cmprs_byts(byts: Array[Byte])(implicit c: Boolean) = {
    if (c) {
      val bfr = new Array[Byte](byts.size + 1024)
      val dfltr = new Deflater
      dfltr.setInput(byts)
      dfltr.finish()
      val len = dfltr.deflate(bfr)
      dfltr.end()
      (byts.size, bfr.slice(0, len))
    } else (byts.size, byts)
  }
}

// private def dflt(v: Array[Short])(implicit c: Boolean) = cmprs_byts((ByteBuffer.allocate(v.size * 2) /: v) { (bb, v) => bb.putShort(v) } array)
// private def dflt1(v: Array[Int])(implicit c: Boolean) = cmprs_byts((ByteBuffer.allocate(v.size * 4) /: v) { (bb, v) => bb.putInt(v) } array)
//  private def dflt(v: Array[Long])(implicit c: Boolean) = cmprs_byts((ByteBuffer.allocate(v.size * 8) /: v) { (bb, v) => bb.putLong(v) } array)
