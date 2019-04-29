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

/**
 * self - interface to persist and restore a frame
 */
trait frm_io extends frm_wrtr with frm_rdr {
  f: frm =>
}

/**
 * rehydration functionality
 */
trait frm_rdr {
  f: frm =>
  /**
   * read a serialised frame from a file
   */
  def rd(fn: String) = {
    val is = new BufferedInputStream(new FileInputStream(fn))
    rd_ln(is)
    is.close
    f
  }

  /**
   * recursively read lines, decompressing and inserting until done
   */
  private def rd_ln(is: InputStream): Unit = {
    def rd_wrd = {
      val bffr = new StringBuilder
      var c = is.read
      while (c.toByte != ':') { bffr += c.toChar; c = is.read }
      bffr.toString
    }
    is.available
    if (is.available > 0) {
      val (typ, col, n, sz, cf, csz) = (rd_wrd, rd_wrd, rd_wrd.toInt, rd_wrd.toInt, rd_wrd.toBoolean, rd_wrd.toInt)
      //println("reading >" + typ + "<>" + col + "<>" + n + "<>" + sz + "<>" + csz + "<>")
      val bff = new Array[Byte](csz)
      val nn = is.read(bff)
      if (nn != csz) throw new df_err("deserialisation error. size mismatch. expected " + csz + " bytes but only read " + nn + " bytes ")
      val f = Future { set_vctr(col, typ, bff, n, sz, cf) }
      rd_ln(is)
      Await.result(f, 1.hour)()
    }
  }

  /**
   *
   */
  private def set_vctr(col: String, typ: String, bff: Array[Byte], n: Int, sz: Int, cf: Boolean): () => Unit = {
    val ba = if (bff.size == sz) bff else dcmprs_byts(bff, sz)
    typ match {
      case "bol" =>
        () => f.set_arg((col, ba map (_ == 1)));
      case "chr" =>
        val cb = new Array[Char](n)
        ByteBuffer.wrap(ba).asCharBuffer.get(cb)
        () => f.set_arg((col, cb));
      case "byt" =>
        () => f.set_arg((col, ba));
      case "sht" =>
        val sb = new Array[Short](n)
        ByteBuffer.wrap(ba).asShortBuffer.get(sb)
        () => f.set_arg((col, sb));
      case "int" =>
        val ib = new Array[Int](n)
        ByteBuffer.wrap(ba).asIntBuffer.get(ib)
        () => f.set_arg((col, ib));
      case "lng" =>
        val lb = new Array[Long](n)
        ByteBuffer.wrap(ba).asLongBuffer.get(lb)
        () => f.set_arg((col, lb));
      case "flt" =>
        val fb = new Array[Float](n)
        ByteBuffer.wrap(ba).asFloatBuffer.get(fb)
        () => f.set_arg((col, fb));
      case "dbl" =>
        val db = new Array[Double](n)
        ByteBuffer.wrap(ba).asDoubleBuffer.get(db)
        () => f.set_arg((col, db));
      case "sym" =>
        val sb = new Array[Symbol](n)
        var ii = 0
        val sym_map = new String(ba).split('\u0000')
        var i = 0; while (i < sym_map.size) {
          val (sym, n) = (Symbol(sym_map(i)), sym_map(i + 1).toInt)
          val upto = ii + n; while (ii < upto) {
            sb(ii) = sym
            ii += 1
          }
          i += 2
        }
        () => f.set_arg((col, sb));
      case "str" =>
        val sb = new String(ba).split('\u0000')
        () => f.set_arg((col, sb));
      case _ => throw new df_err("deserialisation error. unsupported type: " + typ)
    }
  }

  /**
   *
   */
  private def dcmprs_byts(byts: Array[Byte], sz: Int) = {
    if (byts.size > 0) {
      val infltr = new Inflater
      infltr.setInput(byts)
      val res = new Array[Byte](sz)
      val n_inf = infltr.inflate(res)
      if (!infltr.finished || (n_inf != sz)) throw new df_err("decompression error. size mismatch. expected to inflate " + sz + " bytes but actually inflated " + n_inf + " bytes ")
      infltr.end
      res
    } else if (sz > 0) throw new df_err("logical error (BUG?). expected inflated size is " + sz + " but compressed size is 0")
    else Array[Byte]()
  }
}

