package ik.util.hdf

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

import scala.collection.mutable.HashMap
import scala.collection.JavaConverters.asScalaIterator

import hdf.hdf5lib.H5
import hdf.hdf5lib.H5.H5Aclose
import hdf.hdf5lib.H5.H5Acreate
import hdf.hdf5lib.H5.H5Aget_name
import hdf.hdf5lib.H5.H5Aget_space
import hdf.hdf5lib.H5.H5Aget_storage_size
import hdf.hdf5lib.H5.H5Aget_type
import hdf.hdf5lib.H5.H5Aopen_by_idx
import hdf.hdf5lib.H5.H5Aread
import hdf.hdf5lib.H5.H5Awrite
import hdf.hdf5lib.H5.H5Dclose
import hdf.hdf5lib.H5.H5Dcreate
import hdf.hdf5lib.H5.H5Dget_space
import hdf.hdf5lib.H5.H5Dget_type
import hdf.hdf5lib.H5.H5Dopen
import hdf.hdf5lib.H5.H5Dwrite
import hdf.hdf5lib.H5.H5Dwrite_double
import hdf.hdf5lib.H5.H5Dwrite_float
import hdf.hdf5lib.H5.H5Dwrite_int
import hdf.hdf5lib.H5.H5Dwrite_long
import hdf.hdf5lib.H5.H5Dwrite_short
import hdf.hdf5lib.H5.H5Fclose
import hdf.hdf5lib.H5.H5Fcreate
import hdf.hdf5lib.H5.H5Fflush
import hdf.hdf5lib.H5.H5Fget_obj_count
import hdf.hdf5lib.H5.H5Fget_obj_ids
import hdf.hdf5lib.H5.H5Fis_hdf5
import hdf.hdf5lib.H5.H5Fopen
import hdf.hdf5lib.H5.H5Gclose
import hdf.hdf5lib.H5.H5Gcreate
import hdf.hdf5lib.H5.H5Gget_obj_info_all
import hdf.hdf5lib.H5.H5Gn_members
import hdf.hdf5lib.H5.H5Gopen
import hdf.hdf5lib.H5.H5Oget_info
import hdf.hdf5lib.H5.H5Pcreate
import hdf.hdf5lib.H5.H5Pset_chunk
import hdf.hdf5lib.H5.H5Pset_szip
import hdf.hdf5lib.H5.H5Sclose
import hdf.hdf5lib.H5.H5Screate_simple
import hdf.hdf5lib.H5.H5Sget_simple_extent_dims
import hdf.hdf5lib.H5.H5Sget_simple_extent_ndims
import hdf.hdf5lib.H5.H5Sis_simple
import hdf.hdf5lib.H5.H5Sselect_hyperslab
import hdf.hdf5lib.H5.H5Tclose
import hdf.hdf5lib.H5.H5Tcopy
import hdf.hdf5lib.H5.H5Tequal
import hdf.hdf5lib.H5.H5Tis_variable_str
import hdf.hdf5lib.H5.H5Tset_order
import hdf.hdf5lib.H5.H5Tset_size
import hdf.hdf5lib.H5.H5get_libversion
import hdf.hdf5lib.HDF5Constants
import hdf.hdf5lib.HDF5Constants.H5F_SCOPE_GLOBAL
import hdf.hdf5lib.HDF5Constants.H5G_DATASET
import hdf.hdf5lib.HDF5Constants.H5G_GROUP
import hdf.hdf5lib.HDF5Constants.H5P_DATASET_CREATE
import hdf.hdf5lib.HDF5Constants.H5P_DEFAULT
import hdf.hdf5lib.HDF5Constants.H5S_ALL
import hdf.hdf5lib.HDF5Constants.H5S_SELECT_SET
import hdf.hdf5lib.HDF5Constants.H5_INDEX_NAME
import hdf.hdf5lib.HDF5Constants.H5_SZIP_NN_OPTION_MASK
import hdf.hdf5lib.exceptions.HDF5LibraryException

/**
 * unchecked exception used by the hdf. in the style of other unckecked excpetions
 * @author i.kucan
 */
class h_err(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause)

/**
 * constant definitions
 */
object cnst {
  val MX_ATT_SZ = 65400
}

/**
 * abstract definition of a hdf type. tries to encapsulate (and isolate) type specific operations
 * primarily focuses on arrays of simple types. many features of HDF5 are omitted in order to simplify
 * basic features such as array writing and reading.
 * this class is effectively limited to creation of immutable data sets.
 *
 * @author i.kucan
 */
trait h_typ {
  /**
   * abstract type definition. implementation will be specific for type and wil povide actual implementaion
   */
  type JTYP
  /**
   * implicit manifest for providing type hints for array parameters
   */
  implicit val m: Manifest[JTYP]
  /**
   * hdf type id
   */
  val tid: Int
  /**
   * hdf type size for calculating space requirements
   */
  val tsz: Int
  /**
   * read/write an array attribute
   */
  def f_rd_att(id: Int, tid: Int): Array[JTYP]
  def f_wrt_att(id: Int, v: Array[JTYP]): Int

  /**
   * linear read/write of a dataset
   */
  def f_wrt_dst(id: Int, s_id_f: Int, s_id_m: Int, v: Array[JTYP]): Int
  def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int, bffr: Array[JTYP]): Unit = {}
  def f_wrt_dst(id: Int, v: Array[JTYP]): Int = f_wrt_dst(id, H5S_ALL, H5S_ALL, v)
  def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int): Array[JTYP]
  def f_rd_dst(d: h_dst): Array[JTYP] = f_rd_dst(d, H5S_ALL, H5S_ALL, d.s.sz)

  /**
   * are two types equal in hdf5 sense
   */
  def hdf_eql(o: Int) = H5Tequal(o, tid)

  protected def mk_bfr(nb: Int) = ByteBuffer.allocate(nb).order(ByteOrder.LITTLE_ENDIAN)
  protected def mk_bfr(b: Array[Byte]) = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN)
}

/**
 * a h_typ factory for creating required subtypes based on the passed scala array type or hdf type paramater
 */
object h_typ {
  def apply(v: Array[Char]) = h_char
  def apply(v: Array[Byte]) = h_int8
  def apply(v: Array[Short]) = h_int16
  def apply(v: Array[Int]) = h_int32
  def apply(v: Array[Long]) = h_int64
  def apply(v: Array[Float]) = h_ieee32
  def apply(v: Array[Double]) = h_ieee64
  def apply(v: Array[String]) = h_str
  def mtch(typ_id: Int) = {
    if (H5Tequal(typ_id, HDF5Constants.H5T_ALPHA_B16)) h_char
    else if (H5Tequal(typ_id, HDF5Constants.H5T_NATIVE_INT8)) h_int8
    else if (H5Tequal(typ_id, HDF5Constants.H5T_NATIVE_INT16)) h_int16
    else if (H5Tequal(typ_id, HDF5Constants.H5T_NATIVE_INT32)) h_int32
    else if (H5Tequal(typ_id, HDF5Constants.H5T_NATIVE_INT64)) h_int64
    else if (H5Tequal(typ_id, HDF5Constants.H5T_IEEE_F32LE)) h_ieee32
    else if (H5Tequal(typ_id, HDF5Constants.H5T_IEEE_F64LE)) h_ieee64
    else if (H5Tis_variable_str(typ_id)) h_str
    else throw new h_err("type id not supported :> " + typ_id)
  }
}

/**
 * abstract ancestor for simple types.
 * @author i.kucan
 *
 */
abstract class h_prmtv_typ(val typ: Int) extends h_typ {
  override type JTYP <: AnyVal
  override val tid = H5Tcopy(typ)
  H5Tset_order(tid, HDF5Constants.H5T_ORDER_LE)

  def bf_rd(bfr: ByteBuffer, arr: Array[JTYP])
  def bf_wrt(bfr: ByteBuffer, arr: Array[JTYP])

  override def f_rd_att(att_id: Int, tid: Int): Array[JTYP] = {
    val s = h_spc.by_id(H5Aget_space(att_id))
    if (s.dim.length != 1) throw new h_err("only simple attributes supported. dimensionality of attribute is " + s.dim.length)
    // cross check actual size v expected size
    val nbyts = H5Aget_storage_size(att_id)
    if (s.dim(0) != (nbyts / tsz)) throw new h_err("sizes dont match up. dataspace size is " + s.dim(0) + " but calculated size is" + (nbyts / tsz))
    val rv = Array.ofDim[JTYP](s.dim(0).toInt)
    val bffr = Array.ofDim[Byte](nbyts.toInt)
    H5Aread(att_id, tid, bffr)
    bf_rd(mk_bfr(bffr), rv)
    s.cls
    rv
  }

  override def f_wrt_att(att_id: Int, arr: Array[JTYP]): Int = {
    val att_sz = arr.length * tsz
    if (att_sz > cnst.MX_ATT_SZ) throw new h_err("attribut size too large: " + att_sz + ". maximum allowed attribute size is " + cnst.MX_ATT_SZ)
    val bfr = mk_bfr(att_sz)
    bf_wrt(bfr, arr)
    H5Awrite(att_id, typ, bfr.array)
  }
}

/**
 * specialisation for the character data type. implement contracted functionality for character type
 */
case object h_char extends h_prmtv_typ(HDF5Constants.H5T_NATIVE_INT16) {
  override type JTYP = Char
  override lazy val m: Manifest[JTYP] = manifest
  override val tsz = 2
  override def bf_rd(bf: ByteBuffer, arr: Array[JTYP]) = bf.asCharBuffer get arr
  override def bf_wrt(bf: ByteBuffer, arr: Array[JTYP]) = bf.asCharBuffer put arr
  override def f_wrt_dst(id: Int, s_id_f: Int, s_id_m: Int, arr: Array[JTYP]): Int = H5Dwrite(id, typ, s_id_m, s_id_f, H5P_DEFAULT, (arr map (_.toShort)))
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int): Array[JTYP] = {
    val bffr = Array.ofDim[Short](n_elem)
    H5.H5Dread(d.id, tid, s_id_f, s_id_m, H5P_DEFAULT, bffr)
    bffr map (_.toChar)
  }

}
/**
 * specialisation for the 8-bit int data type
 */
case object h_int8 extends h_prmtv_typ(HDF5Constants.H5T_NATIVE_INT8) {
  override type JTYP = Byte
  override lazy val m: Manifest[JTYP] = manifest
  override val tsz = 1
  override def bf_rd(bf: ByteBuffer, arr: Array[JTYP]) = bf get arr
  override def bf_wrt(bf: ByteBuffer, arr: Array[JTYP]) = bf put arr
  override def f_wrt_dst(id: Int, s_id_f: Int, s_id_m: Int, arr: Array[JTYP]): Int = H5Dwrite(id, typ, s_id_m, s_id_f, H5P_DEFAULT, arr)
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int): Array[JTYP] = {
    var bffr = Array.ofDim[JTYP](n_elem)
    H5.H5Dread(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)
    bffr
  }
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int, bffr: Array[JTYP]) = H5.H5Dread(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)

}
/**
 * specialisation for the 16-bit int data type
 */
case object h_int16 extends h_prmtv_typ(HDF5Constants.H5T_NATIVE_INT16) {
  override type JTYP = Short
  override lazy val m: Manifest[JTYP] = manifest
  override val tsz = 2
  override def bf_rd(bf: ByteBuffer, arr: Array[JTYP]) = bf.asShortBuffer get arr
  override def bf_wrt(bf: ByteBuffer, arr: Array[JTYP]) = bf.asShortBuffer put arr
  override def f_wrt_dst(id: Int, s_id_f: Int, s_id_m: Int, arr: Array[JTYP]): Int = H5Dwrite_short(id, typ, s_id_m, s_id_f, H5P_DEFAULT, arr)
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int): Array[JTYP] = {
    var bffr = Array.ofDim[JTYP](n_elem)
    H5.H5Dread_short(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)
    bffr
  }
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int, bffr: Array[JTYP]) = H5.H5Dread_short(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)

}
/**
 * specialisation for the 32-bit int data type
 */
case object h_int32 extends h_prmtv_typ(HDF5Constants.H5T_NATIVE_INT32) {
  override type JTYP = Int
  override lazy val m: Manifest[JTYP] = manifest
  override val tsz = 4
  override def bf_rd(bf: ByteBuffer, arr: Array[JTYP]) = bf.asIntBuffer get arr
  override def bf_wrt(bf: ByteBuffer, arr: Array[JTYP]) = bf.asIntBuffer put arr
  override def f_wrt_dst(id: Int, s_id_f: Int, s_id_m: Int, arr: Array[JTYP]): Int = H5Dwrite_int(id, typ, s_id_m, s_id_f, H5P_DEFAULT, arr)
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int): Array[JTYP] = {
    var bffr = Array.ofDim[JTYP](n_elem)
    H5.H5Dread_int(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)
    bffr
  }
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int, bffr: Array[JTYP]) = H5.H5Dread_int(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)

}
/**
 * specialisation for the 64-bit int data type
 */
case object h_int64 extends h_prmtv_typ(HDF5Constants.H5T_NATIVE_INT64) {
  override type JTYP = Long
  override lazy val m: Manifest[JTYP] = manifest
  override val tsz = 8
  override def bf_rd(bf: ByteBuffer, arr: Array[JTYP]) = bf.asLongBuffer get arr
  override def bf_wrt(bf: ByteBuffer, arr: Array[JTYP]) = bf.asLongBuffer put arr
  override def f_wrt_dst(id: Int, s_id_f: Int, s_id_m: Int, arr: Array[JTYP]): Int = H5Dwrite_long(id, typ, s_id_m, s_id_f, H5P_DEFAULT, arr)
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int): Array[JTYP] = {
    var bffr = Array.ofDim[JTYP](n_elem)
    H5.H5Dread_long(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)
    bffr
  }
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int, bffr: Array[JTYP]) = H5.H5Dread_long(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)

}
/**
 * specialisation for the 32-bit floating pt data type
 */
case object h_ieee32 extends h_prmtv_typ(HDF5Constants.H5T_IEEE_F32LE) {
  override type JTYP = Float
  override lazy val m: Manifest[JTYP] = manifest
  override val tsz = 4
  override def bf_rd(bf: ByteBuffer, arr: Array[JTYP]) = bf.asFloatBuffer get arr
  override def bf_wrt(bf: ByteBuffer, arr: Array[JTYP]) = bf.asFloatBuffer put arr
  override def f_wrt_dst(id: Int, s_id_f: Int, s_id_m: Int, arr: Array[JTYP]): Int = H5Dwrite_float(id, typ, s_id_m, s_id_f, H5P_DEFAULT, arr)
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int): Array[JTYP] = {
    var bffr = Array.ofDim[JTYP](n_elem)
    H5.H5Dread_float(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)
    bffr
  }
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int, bffr: Array[JTYP]) = H5.H5Dread_float(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)
}
/**
 * specialisation for the 64-bit floating pt data type
 */
case object h_ieee64 extends h_prmtv_typ(HDF5Constants.H5T_IEEE_F64LE) {
  override type JTYP = Double
  override lazy val m: Manifest[JTYP] = manifest
  override val tsz = 8
  override def bf_rd(bf: ByteBuffer, arr: Array[JTYP]) = bf.asDoubleBuffer get arr
  override def bf_wrt(bf: ByteBuffer, arr: Array[JTYP]) = bf.asDoubleBuffer put arr
  override def f_wrt_dst(id: Int, s_id_f: Int, s_id_m: Int, arr: Array[JTYP]): Int = H5Dwrite_double(id, typ, s_id_m, s_id_f, H5P_DEFAULT, arr)
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int): Array[JTYP] = {
    var bffr = Array.ofDim[JTYP](n_elem)
    H5.H5Dread_double(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)
    bffr
  }
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int, bffr: Array[JTYP]) = H5.H5Dread_double(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)

}

/**
 * specialisation for the string data type
 */
case object h_str extends h_typ {
  override type JTYP = String
  override lazy val m: Manifest[JTYP] = manifest
  override val tsz = -1
  override val tid = H5Tcopy(HDF5Constants.H5T_C_S1)
  H5Tset_size(tid, HDF5Constants.H5T_VARIABLE)
  override def f_rd_att(id: Int, tid: Int): Array[JTYP] = throw new h_err("string array attributes feature not supported")
  override def f_wrt_att(id: Int, arr: Array[JTYP]): Int = throw new h_err("string array attributes feature not supported")
  override def f_wrt_dst(id: Int, s_id_f: Int, s_id_m: Int, arr: Array[JTYP]): Int = H5Dwrite(id, tid, s_id_m, s_id_f, H5P_DEFAULT, arr)
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int): Array[JTYP] = {
    var bffr = Array.ofDim[JTYP](n_elem)
    H5.H5Dread(d.id, tid, s_id_m, s_id_f, H5P_DEFAULT, bffr)
    bffr
  }
}

/**
 * specialisation for complex data types
 */
case class h_cplx(chldr: List[h_typ]) extends h_typ {
  override lazy val tid = -1
  override lazy val tsz = -1
  override val m: Manifest[JTYP] = null

  override def f_rd_att(id: Int, tid: Int): Array[JTYP] = throw new h_err("feature not implemented")
  override def f_wrt_att(id: Int, v: Array[JTYP]) = throw new h_err("feature not implemented")
  override def f_wrt_dst(id: Int, s_id_f: Int, s_id_m: Int, v: Array[JTYP]): Int = throw new h_err("feature not implemented")
  override def f_rd_dst(d: h_dst, s_id_f: Int, s_id_m: Int, n_elem: Int): Array[JTYP] = null
}

/**
 * abstraction for the hdf5 database object. basically something with an id created in hdf5 lib
 */
abstract class h_obj {
  val id: Int
  var opn = false
  def flsh = H5Fflush(id, HDF5Constants.H5F_SCOPE_GLOBAL)
  def cls = opn = false
}

/**
 * hdf5 attribute
 */
class h_att(override val id: Int, val n: String) extends h_obj {
  opn = true
  val rd_typ = H5Aget_type(id)
  override def cls = {
    super.cls
    H5Aclose(id)
    H5Tclose(rd_typ)
  }
}

/**
 * companion object for attirbute generatoin
 */
object h_att {
  def apply(n: String, id: Int, s: h_spc, t: h_typ) = mk(n, id, s, t)
  def mk(n: String, loc_id: Int, s: h_spc, t: h_typ) = {
    val new_att = new h_att(H5Acreate(loc_id, n, t.tid, s.id, H5P_DEFAULT, H5P_DEFAULT), n)
    s.cls
    new_att
  }
}

/**
 * a trait describing an object which can be adorned with attributes
 */
trait adorned {
  self: h_obj =>

  lazy val loc_id = self.id

  val atts = HashMap[String, h_att]()
  def cls_all: Unit = atts foreach { x => x._2.cls }

  def set(n: String, v: Char): Unit = set(n, Array(v))
  def set(n: String, v: Byte): Unit = set(n, Array(v))
  def set(n: String, v: Short): Unit = set(n, Array(v))
  def set(n: String, v: Int): Unit = set(n, Array(v))
  def set(n: String, v: Long): Unit = set(n, Array(v))
  def set(n: String, v: Float): Unit = set(n, Array(v))
  def set(n: String, v: Double): Unit = set(n, Array(v))
  def set(n: String, v: String): Unit = set(n, Array(v))

  def set(n: String, v: Array[Char]): Unit = sttr(n, h_spc(v.length), h_char, (i: Int) => { h_char.f_wrt_att(i, v) })
  def set(n: String, v: Array[Byte]): Unit = sttr(n, h_spc(v.length), h_int8, (i: Int) => { h_int8.f_wrt_att(i, v) })
  def set(n: String, v: Array[Short]): Unit = sttr(n, h_spc(v.length), h_int16, (i: Int) => { h_int16.f_wrt_att(i, v) })
  def set(n: String, v: Array[Int]): Unit = sttr(n, h_spc(v.length), h_int32, (i: Int) => { h_int32.f_wrt_att(i, v) })
  def set(n: String, v: Array[Long]): Unit = sttr(n, h_spc(v.length), h_int64, (i: Int) => { h_int64.f_wrt_att(i, v) })
  def set(n: String, v: Array[Float]): Unit = sttr(n, h_spc(v.length), h_ieee32, (i: Int) => { h_ieee32.f_wrt_att(i, v) })
  def set(n: String, v: Array[Double]): Unit = sttr(n, h_spc(v.length), h_ieee64, (i: Int) => { h_ieee64.f_wrt_att(i, v) })
  def set(n: String, v: Array[String]): Unit = sttr(n, h_spc(v.length), h_str, (i: Int) => { h_str.f_wrt_att(i, v) })

  private def sttr(n: String, spc: h_spc, t: h_typ, s_foo: (Int) => Unit): Unit = s_foo(atts.getOrElseUpdate(n, h_att(n, loc_id, spc, t)).id)

  private def chk_att(n: String, t: h_typ): h_att = {
    val a = atts.getOrElse(n, throw new h_err("attribute named " + n + " does not exist on object. "))
    if (!t.hdf_eql(a.rd_typ)) throw new h_err("attribute named " + n + " is not of type double. check your database.")
    a
  }

  def get_chr(n: String): Array[Char] = h_char.f_rd_att(chk_att(n, h_char).id, h_char.tid)
  def get_str(n: String): Array[String] = h_str.f_rd_att(chk_att(n, h_str).id, h_str.tid)
  def get_byt(n: String): Array[Byte] = h_int8.f_rd_att(chk_att(n, h_int8).id, h_int8.tid)
  def get_sht(n: String): Array[Short] = h_int16.f_rd_att(chk_att(n, h_int16).id, h_int16.tid)
  def get_int(n: String): Array[Int] = h_int32.f_rd_att(chk_att(n, h_int32).id, h_int32.tid)
  def get_lng(n: String): Array[Long] = h_int64.f_rd_att(chk_att(n, h_int64).id, h_int64.tid)
  def get_flt(n: String): Array[Float] = h_ieee32.f_rd_att(chk_att(n, h_ieee32).id, h_ieee32.tid)
  def get_dbl(n: String): Array[Double] = h_ieee64.f_rd_att(chk_att(n, h_ieee64).id, h_ieee64.tid)

  protected def enum(obj_nm: String) = {
    val att_inf = H5Oget_info(loc_id)
    for (i <- 0 until att_inf.num_attrs.toInt) {
      val att_id = H5Aopen_by_idx(loc_id, ".", HDF5Constants.H5_INDEX_NAME, HDF5Constants.H5_ITER_NATIVE, i, H5P_DEFAULT, H5P_DEFAULT)
      val att_nm = new Array[String](1)
      H5Aget_name(att_id, att_nm)
      atts(att_nm(0)) = new h_att(att_id, att_nm(0))
    }
  }
}

/**
 * hdf5 dataset
 */
class h_dst(val n: String, override val id: Int, val s: h_spc, val t: h_typ) extends h_obj with adorned {
  super.enum(n)
  opn = true

  override def cls = {
    super.cls
    super.cls_all
    s.cls
    H5Dclose(id)
  }
}

/**
 * dataset companion object
 */
object h_dst {
  /**
   * create a new dataset on a group
   */
  def apply(n: String, g: h_grp, s: h_spc, t: h_typ) = {
    var dst_id = H5Dcreate(g.id, n, t.tid, s.id, H5P_DEFAULT, H5P_DEFAULT, H5P_DEFAULT)
    new h_dst(n, dst_id, s, t)
  }

  /**
   * open an existing dataset and associated space and type objects
   */
  def apply(g: h_grp, n: String): h_dst = {
    val dst_id = H5Dopen(g.id, n, H5P_DEFAULT)
    val spc_id = H5Dget_space(dst_id)
    val typ_id = H5Dget_type(dst_id)

    // note the match - this uses type equality to match to an existing primitive object type
    // rendering the dataset type redundant. hence close the type
    val new_dst = new h_dst(n, dst_id, h_spc.by_id(spc_id), h_typ.mtch(typ_id))
    H5Tclose(typ_id)
    new_dst
  }

  def cmprssd(n: String, g: h_grp, s: h_spc, t: h_typ) = {
    val dst_prps = H5Pcreate(H5P_DATASET_CREATE)
    H5Pset_szip(dst_prps, H5_SZIP_NN_OPTION_MASK, 32)
    H5Pset_chunk(dst_prps, s.dim.length, s.dim)
    var dst_id = H5Dcreate(g.id, n, t.tid, s.id, HDF5Constants.H5P_DEFAULT, dst_prps, HDF5Constants.H5P_DEFAULT)
    new h_dst(n, dst_id, s, t)
  }

}

object h_lib {
  def n_opn = H5.getOpenIDCount
  def opn_ids = asScalaIterator(H5.getOpenIDs.iterator).toList
  def gc = H5.H5garbage_collect
}

/**
 * database file. this is a fundamental construct encapsulating the hdf5 file. all H5F_xxx functions are applied at this level
 */
class h_fle(override val id: Int) extends h_obj {
  opn = true

  def n_opn_grp = n_opn(HDF5Constants.H5F_OBJ_GROUP)
  def n_opn_dst = n_opn(HDF5Constants.H5F_OBJ_DATASET)
  def n_opn_typ = n_opn(HDF5Constants.H5F_OBJ_DATATYPE)
  def n_opn_all = n_opn(HDF5Constants.H5F_OBJ_ALL)
  def n_opn_att = n_opn(HDF5Constants.H5F_OBJ_ATTR)
  def n_opn_lcl = n_opn(HDF5Constants.H5F_OBJ_LOCAL)
  def n_opn_fle = n_opn(HDF5Constants.H5F_OBJ_FILE)

  def opn_grp = opn_ids(HDF5Constants.H5F_OBJ_GROUP)
  def opn_dst = opn_ids(HDF5Constants.H5F_OBJ_DATASET)

  override def flsh = H5Fflush(id, HDF5Constants.H5F_SCOPE_GLOBAL)
  override def cls = {
    super.cls
    H5Fflush(id, H5F_SCOPE_GLOBAL)
    H5Fclose(id)
  }

  private def n_opn(typ: Int) = H5Fget_obj_count(id, HDF5Constants.H5F_OBJ_GROUP)
  private def opn_ids(typ: Int) = {
    val r = new Array[Int](n_opn(typ))
    H5Fget_obj_ids(id, typ, r.length, r)
    r
  }
}

/**
 * file companion object. create a new hdf file or use an existing one.
 * in case of overwrite,
 */
object h_fle {
  def apply(n: String, overwrite: Boolean = false): h_fle =
    if (!overwrite && new File(n).exists) {
      if (H5Fis_hdf5(n)) opn(n)
      else throw new h_err("existing file " + n + " is not recognised as a hdf 5 file.")
    } else mk(n)

  def opn(n: String): h_fle = new h_fle(H5Fopen(n, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT))
  def rd(n: String): h_fle = new h_fle(H5Fopen(n, HDF5Constants.H5F_ACC_RDONLY, HDF5Constants.H5P_DEFAULT))
  def mk(n: String): h_fle = new h_fle(H5Fcreate(n, HDF5Constants.H5F_ACC_TRUNC, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT))

  def is_hdf5(n: String) = new File(n).exists && H5Fis_hdf5(n)
}

/**
 * hdf5 group
 */
class h_grp(override val id: Int, val nm: String) extends h_obj with adorned {
  lazy val opn_grps = HashMap[String, h_grp]()
  lazy val opn_dsts = HashMap[String, h_dst]()

  opn = true

  def grps = enum_kids(H5G_GROUP)
  def dsts = enum_kids(H5G_DATASET)

  // enumerate and child groups
  private def n_kids = H5Gn_members(id, ".")
  private def enum_kids(kd_typ: Int) = {
    val n_objs = n_kids
    // enumerate children
    if (n_objs > 0) {
      val (typ, nme, ref, lnk) = (new Array[Int](n_objs), new Array[String](n_objs), new Array[Long](n_objs), new Array[Int](n_objs))
      H5Gget_obj_info_all(id, null, nme, typ, lnk, ref, H5_INDEX_NAME)
      (0 until n_objs) filter (i => typ(i) == kd_typ) map (i => nme(i)) toSet
    } else Set[String]()
  }

  /**
   * selector
   */
  def apply(n: String): h_grp = apply((n split "/").toList filter (_.length > 0))
  def apply(n: List[String]): h_grp =
    // XX:>> if (n.length == 1) opn_grps(n.head)
    if (n.length == 1) opn_grps.getOrElseUpdate(n.head, h_grp(id, n.head))
    else opn_grps(n.head)(n.tail)

  def exist(n: String): Boolean = exist((n split "/").toList filter (_.length > 0))
  def exist(n: List[String]): Boolean =
    if (n.length == 1) opn_grps.contains(n.head)
    else if (opn_grps.contains(n.head)) opn_grps(n.head).exist(n.tail)
    else false

  /**
   * close the group and all of its children
   */
  override def cls = {
    opn_dsts foreach { dst => if (dst._2.opn) dst._2.cls }
    opn_grps foreach { grp => if (grp._2.opn) grp._2.cls }
    if (opn) {
      super.cls
      super.cls_all
      H5Gclose(id)
    }
  }
  def mk_grp(n: String): h_grp = {
    if (n.trim.length <= 0) throw new h_err("cant create a group with a null or root ('/') name. pass in a valid string name, like '/foo'")
    val pth = (n split "/").toList filter (_.length > 0)
    mk_grp(pth)
  }
  def mk_grp(p: List[String]): h_grp = {
    val g = opn_grps.getOrElseUpdate(p.head, h_grp.mk(id, p.head))
    if (p.tail.isEmpty) g
    else g.mk_grp(p.tail)
  }

  def opn_dst(n: String) = opn_dsts.getOrElseUpdate(n, h_dst(this, n))

  def add_dst(n: String, s: h_spc, t: h_typ) = {
    val dst = h_dst(n, this, s, t)
    opn_dsts += n -> dst
    dst
  }

  /**
   * write a new dataset
   */
  def wrt(n: String, v: Array[Char]): h_dst = wrt(n, h_spc(v.length), v)
  def wrt(n: String, v: Array[Byte]): h_dst = wrt(n, h_spc(v.length), v)
  def wrt(n: String, v: Array[Short]): h_dst = wrt(n, h_spc(v.length), v)
  def wrt(n: String, v: Array[Int]): h_dst = wrt(n, h_spc(v.length), v)
  def wrt(n: String, v: Array[Long]): h_dst = wrt(n, h_spc(v.length), v)
  def wrt(n: String, v: Array[Float]): h_dst = wrt(n, h_spc(v.length), v)
  def wrt(n: String, v: Array[Double]): h_dst = wrt(n, h_spc(v.length), v)
  def wrt(n: String, v: Array[String]): h_dst = wrt(n, h_spc(v.length), v)

  def wrt(n: String, s: h_spc, v: Array[Char]): h_dst = wrtr(n, s, h_char, (d: h_dst) => { h_char.f_wrt_dst(d.id, v) })
  def wrt(n: String, s: h_spc, v: Array[Byte]): h_dst = wrtr(n, s, h_int8, (d: h_dst) => { h_int8.f_wrt_dst(d.id, v) })
  def wrt(n: String, s: h_spc, v: Array[Short]): h_dst = wrtr(n, s, h_int16, (d: h_dst) => { h_int16.f_wrt_dst(d.id, v) })
  def wrt(n: String, s: h_spc, v: Array[Int]): h_dst = wrtr(n, s, h_int32, (d: h_dst) => { h_int32.f_wrt_dst(d.id, v) })
  def wrt(n: String, s: h_spc, v: Array[Long]): h_dst = wrtr(n, s, h_int64, (d: h_dst) => { h_int64.f_wrt_dst(d.id, v) })
  def wrt(n: String, s: h_spc, v: Array[Float]): h_dst = wrtr(n, s, h_ieee32, (d: h_dst) => { h_ieee32.f_wrt_dst(d.id, v) })
  def wrt(n: String, s: h_spc, v: Array[Double]): h_dst = wrtr(n, s, h_ieee64, (d: h_dst) => { h_ieee64.f_wrt_dst(d.id, v) })
  def wrt(n: String, s: h_spc, v: Array[String]): h_dst = wrtr(n, s, h_str, (d: h_dst) => { h_str.f_wrt_dst(d.id, v) })

  def shrd(n: String, N: Int, off: Long, v: Array[Char]): h_dst = {
    val (d, memspace) = prpr_shrd(n, N, off, v.length, h_char)
    h_char.f_wrt_dst(d.id, d.s.id, memspace, v)
    H5Sclose(memspace)
    d
  }
  def shrd(n: String, N: Int, off: Long, v: Array[Byte]): h_dst = {
    val (d, memspace) = prpr_shrd(n, N, off, v.length, h_int8)
    h_int8.f_wrt_dst(d.id, d.s.id, memspace, v)
    H5Sclose(memspace)
    d
  }
  def shrd(n: String, N: Int, off: Long, v: Array[Short]): h_dst = {
    val (d, memspace) = prpr_shrd(n, N, off, v.length, h_int16)
    h_int16.f_wrt_dst(d.id, d.s.id, memspace, v)
    H5Sclose(memspace)
    d
  }
  def shrd(n: String, N: Int, off: Long, v: Array[Int]): h_dst = {
    val (d, memspace) = prpr_shrd(n, N, off, v.length, h_int32)
    h_int32.f_wrt_dst(d.id, d.s.id, memspace, v)
    H5Sclose(memspace)
    d
  }
  def shrd(n: String, N: Int, off: Long, v: Array[Long]): h_dst = {
    val (d, memspace) = prpr_shrd(n, N, off, v.length, h_int64)
    h_int64.f_wrt_dst(d.id, d.s.id, memspace, v)
    H5Sclose(memspace)
    d
  }
  def shrd(n: String, N: Int, off: Long, v: Array[Float]): h_dst = {
    val (d, memspace) = prpr_shrd(n, N, off, v.length, h_ieee32)
    h_ieee32.f_wrt_dst(d.id, d.s.id, memspace, v)
    H5Sclose(memspace)
    d
  }
  def shrd(n: String, N: Int, off: Long, v: Array[Double]): h_dst = {
    val (d, memspace) = prpr_shrd(n, N, off, v.length, h_ieee64)
    h_ieee64.f_wrt_dst(d.id, d.s.id, memspace, v)
    H5Sclose(memspace)
    d
  }

  /**
   * prepare shard helper function. map the file dataspace to the memory space of given length at given offset
   */
  private def prpr_shrd(n: String, N: Int, off: Long, vlngth: Int, t: h_typ) = {
    val spc_sz = if (vlngth - off < N) vlngth - off else N
    val f_spc = h_spc(spc_sz)
    val d = h_dst.apply(n, this, f_spc, t)
    //val d = h_dst.cmprssd(n, this, f_spc, t)
    opn_dsts += n -> d
    val m_spc = H5Screate_simple(1, Array[Long](vlngth), null)
    H5Sselect_hyperslab(m_spc, H5S_SELECT_SET, Array[Long](off), null, Array[Long](spc_sz), null)
    (d, m_spc)
  }

  /**
   * write helper function
   */
  private def wrtr(n: String, s: h_spc, t: h_typ, w_foo: (h_dst) => Unit): h_dst = {
    val d = h_dst(n, this, s, t)
    w_foo(d)
    s.cls
    opn_dsts += n -> d
    d
  }

  /**
   * change an existing dataset at a given offset
   */
  def chng(n: String, off: Long, v: Array[Char]): h_dst = chng(n, Array(off), v)
  def chng(n: String, off: Long, v: Array[Byte]): h_dst = chng(n, Array(off), v)
  def chng(n: String, off: Long, v: Array[Short]): h_dst = chng(n, Array(off), v)
  def chng(n: String, off: Long, v: Array[Int]): h_dst = chng(n, Array(off), v)
  def chng(n: String, off: Long, v: Array[Long]): h_dst = chng(n, Array(off), v)
  def chng(n: String, off: Long, v: Array[Float]): h_dst = chng(n, Array(off), v)
  def chng(n: String, off: Long, v: Array[Double]): h_dst = chng(n, Array(off), v)
  def chng(n: String, off: Long, v: Array[String]): h_dst = chng(n, Array(off), v)
  /**
   * the general n-dimensional version
   * todo:>> , this isnt really general enough, as it doesnt allow for the space definitions...
   */
  def chng(n: String, off: Array[Long], v: Array[Char]): h_dst = subspc_wrt(n, off, Array[Long](v.length), (d: h_dst, s_id_f: Int, s_id_m: Int) => { h_char.f_wrt_dst(d.id, s_id_f, s_id_m, v) })
  def chng(n: String, off: Array[Long], v: Array[Byte]): h_dst = subspc_wrt(n, off, Array[Long](v.length), (d: h_dst, s_id_f: Int, s_id_m: Int) => { h_int8.f_wrt_dst(d.id, s_id_f, s_id_m, v) })
  def chng(n: String, off: Array[Long], v: Array[Short]): h_dst = subspc_wrt(n, off, Array[Long](v.length), (d: h_dst, s_id_f: Int, s_id_m: Int) => { h_int16.f_wrt_dst(d.id, s_id_f, s_id_m, v) })
  def chng(n: String, off: Array[Long], v: Array[Int]): h_dst = subspc_wrt(n, off, Array[Long](v.length), (d: h_dst, s_id_f: Int, s_id_m: Int) => { h_int32.f_wrt_dst(d.id, s_id_f, s_id_m, v) })
  def chng(n: String, off: Array[Long], v: Array[Long]): h_dst = subspc_wrt(n, off, Array[Long](v.length), (d: h_dst, s_id_f: Int, s_id_m: Int) => { h_int64.f_wrt_dst(d.id, s_id_f, s_id_m, v) })
  def chng(n: String, off: Array[Long], v: Array[Float]): h_dst = subspc_wrt(n, off, Array[Long](v.length), (d: h_dst, s_id_f: Int, s_id_m: Int) => { h_ieee32.f_wrt_dst(d.id, s_id_f, s_id_m, v) })
  def chng(n: String, off: Array[Long], v: Array[Double]): h_dst = subspc_wrt(n, off, Array[Long](v.length), (d: h_dst, s_id_f: Int, s_id_m: Int) => { h_ieee64.f_wrt_dst(d.id, s_id_f, s_id_m, v) })
  def chng(n: String, off: Array[Long], v: Array[String]): h_dst = subspc_wrt(n, off, Array[Long](v.length), (d: h_dst, s_id_f: Int, s_id_m: Int) => { h_str.f_wrt_dst(d.id, s_id_f, s_id_m, v) })

  /**
   * read a complete dataset
   */
  def rd_chr(n: String): Array[Char] = h_char.f_rd_dst(opn_dst(n))
  def rd_byt(n: String): Array[Byte] = h_int8.f_rd_dst(opn_dst(n))
  def rd_sht(n: String): Array[Short] = h_int16.f_rd_dst(opn_dst(n))
  def rd_int(n: String): Array[Int] = h_int32.f_rd_dst(opn_dst(n))
  def rd_lng(n: String): Array[Long] = h_int64.f_rd_dst(opn_dst(n))
  def rd_flt(n: String): Array[Float] = h_ieee32.f_rd_dst(opn_dst(n))
  def rd_dbl(n: String): Array[Double] = h_ieee64.f_rd_dst(opn_dst(n))
  def rd_str(n: String): Array[String] = h_str.f_rd_dst(opn_dst(n))
  /**
   * read a partial dataset
   */
  //def rd_chr(n: String, off: Long, idx: Int): Array[Char] = { val (d, s) = subspc(n, off, idx); h_char.f_rd_dst(d, d.s.id, s.id, idx) }
  def rd_chr(n: String, off: Long, idx: Int): Array[Char] = throw new h_err("subsetting character datasets not possible. use byte datasets instead")
  def rd_byt(n: String, off: Long, idx: Int): Array[Byte] = { val (d, s) = subspc(n, off, idx); val r = h_int8.f_rd_dst(d, d.s.id, s.id, idx); s.cls; r }
  def rd_sht(n: String, off: Long, idx: Int): Array[Short] = { val (d, s) = subspc(n, off, idx); val r = h_int16.f_rd_dst(d, d.s.id, s.id, idx); s.cls; r }
  def rd_int(n: String, off: Long, idx: Int): Array[Int] = { val (d, s) = subspc(n, off, idx); val r = h_int32.f_rd_dst(d, d.s.id, s.id, idx); s.cls; r }
  def rd_lng(n: String, off: Long, idx: Int): Array[Long] = { val (d, s) = subspc(n, off, idx); val r = h_int64.f_rd_dst(d, d.s.id, s.id, idx); s.cls; r }
  def rd_flt(n: String, off: Long, idx: Int): Array[Float] = { val (d, s) = subspc(n, off, idx); val r = h_ieee32.f_rd_dst(d, d.s.id, s.id, idx); s.cls; r }
  def rd_dbl(n: String, off: Long, idx: Int): Array[Double] = { val (d, s) = subspc(n, off, idx); val r = h_ieee64.f_rd_dst(d, d.s.id, s.id, idx); s.cls; r }
  def rd_str(n: String, off: Long, idx: Int): Array[String] = { val (d, s) = subspc(n, off, idx); val r = h_str.f_rd_dst(d, d.s.id, s.id, idx); s.cls; r }

  /**
   * subspace operations:
   * select a subspace on the file dataspace and create a corresponding-in memory dataspace
   */
  private def subspc(d: h_dst, off: Array[Long], idx: Array[Long]): h_spc = {
    H5Sselect_hyperslab(d.s.id, H5S_SELECT_SET, off, null, idx, null)
    val s_id_m = H5Screate_simple(idx.length, idx, null)
    H5Sselect_hyperslab(s_id_m, H5S_SELECT_SET, new Array[Long](idx.length), null, idx, null)
    new h_spc(s_id_m, idx)
  }
  private def subspc(n: String, off: Long, idx: Long): (h_dst, h_spc) = subspc(n, Array(off), Array(idx))
  private def subspc(n: String, off: Array[Long], idx: Array[Long]): (h_dst, h_spc) = {
    val d = opn_dsts.getOrElse(n, throw new h_err("you are trying to change a non-existent data set: " + n + " in group " + nm + ". make sure dataset exists and is open!"))
    (d, subspc(d, off, idx))
  }

  /**
   * write to a subspace (partial writes) with a delegate function
   */
  private def subspc_wrt(n: String, off: Array[Long], idx: Array[Long], c_foo: (h_dst, Int, Int) => Unit): h_dst = {
    //val d = opn_dsts.getOrElse(n, throw new h_err("you are trying to change a non-existent data set: " + n + " in group " + nm + ". make sure dataset exists and is open!"))
    val d = opn_dsts.getOrElseUpdate(n, h_dst(this, n))
    val ss = subspc(d, off, idx)
    c_foo(d, d.s.id, ss.id)
    ss.cls
    d
  }

  def splc(n: String, off: Long, v: Array[Char]) = {
    val chrs = rd_chr(n)
    for (i <- 0 until chrs.length) v(off.toInt + i) = chrs(i)
  }
  def splc(n: String, off: Long, v: Array[Byte]) = {
    val (d, memspace) = prpr_splc(n, off, v.length)
    h_int8.f_rd_dst(d, d.s.id, memspace, 0, v)
    H5Sclose(memspace)
  }
  def splc(n: String, off: Long, v: Array[Short]) = {
    val (d, memspace) = prpr_splc(n, off, v.length)
    h_int16.f_rd_dst(d, d.s.id, memspace, 0, v)
    H5Sclose(memspace)
  }
  def splc(n: String, off: Long, v: Array[Int]) = {
    val (d, memspace) = prpr_splc(n, off, v.length)
    h_int32.f_rd_dst(d, d.s.id, memspace, 0, v)
    H5Sclose(memspace)
  }
  def splc(n: String, off: Long, v: Array[Long]) = {
    val (d, memspace) = prpr_splc(n, off, v.length)
    h_int64.f_rd_dst(d, d.s.id, memspace, 0, v)
    H5Sclose(memspace)
  }
  def splc(n: String, off: Long, v: Array[Float]) = {
    val (d, memspace) = prpr_splc(n, off, v.length)
    h_ieee32.f_rd_dst(d, d.s.id, memspace, 0, v)
    H5Sclose(memspace)
  }
  def splc(n: String, off: Long, v: Array[Double]) = {
    val (d, memspace) = prpr_splc(n, off, v.length)
    h_ieee64.f_rd_dst(d, d.s.id, memspace, 0, v)
    H5Sclose(memspace)
  }
  @deprecated("use string named method which closes the space correctlly", "0.1")
  def splc(d: h_dst, off: Long, v: Array[Byte]) = h_int8.f_rd_dst(d, d.s.id, prpr_splc(d, off, v.length), 0, v)
  @deprecated("use string named method which closes the space correctlly", "0.1")
  def splc(d: h_dst, off: Long, v: Array[Short]) = h_int16.f_rd_dst(d, d.s.id, prpr_splc(d, off, v.length), 0, v)
  @deprecated("use string named method which closes the space correctlly", "0.1")
  def splc(d: h_dst, off: Long, v: Array[Int]) = h_int32.f_rd_dst(d, d.s.id, prpr_splc(d, off, v.length), 0, v)
  @deprecated("use string named method which closes the space correctlly", "0.1")
  def splc(d: h_dst, off: Long, v: Array[Long]) = h_int64.f_rd_dst(d, d.s.id, prpr_splc(d, off, v.length), 0, v)
  @deprecated("use string named method which closes the space correctlly", "0.1")
  def splc(d: h_dst, off: Long, v: Array[Float]) = h_ieee32.f_rd_dst(d, d.s.id, prpr_splc(d, off, v.length), 0, v)
  @deprecated("use string named method which closes the space correctlly", "0.1")
  def splc(d: h_dst, off: Long, v: Array[Double]) = h_ieee64.f_rd_dst(d, d.s.id, prpr_splc(d, off, v.length), 0, v)

  private def prpr_splc(d: h_dst, off: Long, vlngth: Int) = {
    val m_spc = H5Screate_simple(1, Array[Long](vlngth), null)
    H5Sselect_hyperslab(m_spc, H5S_SELECT_SET, Array[Long](off), null, Array[Long](d.s.sz), null)
    m_spc
  }
  private def prpr_splc(n: String, off: Long, vlngth: Int) = {
    //val d = opn_dsts.getOrElse(n, throw new h_err("dataset " + n + "does not exist"))
    val d = opn_dsts.getOrElseUpdate(n, h_dst(this, n))
    val m_spc = H5Screate_simple(1, Array[Long](vlngth), null)
    H5Sselect_hyperslab(m_spc, H5S_SELECT_SET, Array[Long](off), null, Array[Long](d.s.sz), null)
    (d, m_spc)
  }

}

/**
 * group companion object
 */
object h_grp {

  def mk(pid: Int, nm: String): h_grp = {
    //try { new h_grp(H5Gcreate(pid, nm, 0), nm) } catch {
    try { new h_grp(H5Gcreate(pid, nm, H5P_DEFAULT, H5P_DEFAULT, H5P_DEFAULT), nm) } catch {
      case ioe: HDF5LibraryException => throw new h_err("failed to create group " + nm + ". does it exist already?", ioe)
      case t: Throwable              => throw new h_err("unknown exeption while trying to create group " + nm + ". does it exist already?", t)
    }
  }

  def opn(pid: Int, nm: String): h_grp = {
    try {
      val g = new h_grp(H5Gopen(pid, nm, HDF5Constants.H5P_DEFAULT), nm)
      g.enum(nm)
      g
    } catch {
      case ioe: HDF5LibraryException => throw new h_err("failed to create group " + nm + ". are you sure it exists?", ioe)
      case t: Throwable              => throw new h_err("unknown exeption while trying to create group " + nm + ". does it exist already?", t)
    }
  }

  def apply(pid: Int, nm: String): h_grp = {
    try { opn(pid, nm) } catch {
      case h: h_err =>
        h.printStackTrace; mk(pid, nm)
      case t: Throwable => throw new h_err("unexpected exception caught while opening the group", t)
    }
  }
}

/**
 * data space definition
 */
class h_spc(override val id: Int, val dim: Array[Long], val mx_dim: Array[Long] = null) extends h_obj {
  opn = true
  if (dim.length <= 0) throw new h_err("data space must have at least one dimension")
  override def cls = {
    if (opn) H5Sclose(id)
    super.cls
  }
  lazy val sz = (1l /: dim)((x, y) => x * y) toInt

}

object h_spc {
  def apply(d: Long*) = new h_spc(H5Screate_simple(d.toArray.length, d.toArray, null), d.toArray)
  def apply(d: Array[Long]) = new h_spc(H5Screate_simple(d.length, d, d), d, d)
  def apply(d: Array[Long], md: Array[Long]) = new h_spc(H5Screate_simple(d.length, d, md), d, md)
  def by_id(sid: Int) = {
    if (!(H5Sis_simple(sid))) throw new h_err("only simple dataspaces supported")
    val rnk = H5Sget_simple_extent_ndims(sid)
    val (dim, mxd) = (new Array[Long](rnk), new Array[Long](rnk))
    H5Sget_simple_extent_dims(sid, dim, mxd)
    new h_spc(sid, dim, mxd)
  }
}

/**
 * hdf5 database abstraction. groups physical hdf5 objects into a logical database.
 *
 * it roles are to serve as the entry point into the database and its object tree
 * and to host the database-wide operations
 */
class h_db(val f: h_fle) extends h_obj {
  //val spcs = HashMap[String, h_spc]()
  val root = h_grp(f.id, "/")
  def / = root
  override val id = -1
  override def flsh = f.flsh
  override def cls = {
    //spcs foreach { x => x._2.cls }
    root.cls
    f.cls
  }

  /**
   * navigate into the hierarchical group structure
   */
  def apply(n: String): h_grp = if (n.length() == 1 && n == "/") root else apply((n split "/").toList filter (_.length > 0))
  def apply(n: List[String]): h_grp =
    if (n.length == 1) root(n.head)
    else root(n.head)(n.tail)

  def exist(n: String): Boolean = exist((n split "/").toList filter (_.length > 0))
  def exist(n: List[String]): Boolean =
    if (n.length == 1) root.opn_grps.contains(n.head)
    else if (root.opn_grps.contains(n.head)) root(n.head).exist(n.tail)
    else false

  //  def add_spc(n: String, d: Array[Long], md: Array[Long] = null) = {
  //    val spc = h_spc(d, md)
  //    spcs += n -> spc
  //    spc
  //  }

  def mk_grp(n: String) = {
    if (n.trim.length <= 0) throw new h_err("cant create a group with a null or root ('/') name. pass in a valid string name, like '/foo'")
    val pth = (n split "/").toList filter (_.length > 0)
    root.mk_grp(pth)
  }

  //  def add_dst(n: String, g: String, s: String, t: h_typ) = mk_grp(g).add_dst(n, spcs(s), t)
  //  def add_dst(n: String, g: String, s: h_spc, t: h_typ) = mk_grp(g).add_dst(n, s, t)
}

/**
 * database companion object
 */
object h_db {
  /**
   * apply. open an existing file or create a new one if it doesnt exist
   */
  def apply(f: File): h_db = apply(f.getCanonicalPath)
  def apply(n: String) = if (h_fle.is_hdf5(n)) opn(n) else mk(n)
  /**
   * open hdf5 file if it exists, throw an error otherwise
   */
  def opn(f: File): h_db = opn(f.getCanonicalPath)
  def opn(n: String) =
    if (!h_fle.is_hdf5(n)) throw new h_err("Cannot open file " + n + ". It does not exist or is not a hdf5 file.")
    else new h_db(h_fle.opn(n))

  def rd(n: String) = new h_db(h_fle.rd(n))

  def mk(f: File): h_db = mk(f.getCanonicalPath)
  def mk(n: String) = new h_db(h_fle.mk(n))
  /**
   * delete a database file. (corrupt, or incorrectly closed files can unusable and require hard deletion)
   */
  def rm(f: File): Boolean = rm(f.getCanonicalPath)
  def rm(fn: String) = if (new File(fn).exists && h_fle.is_hdf5(fn)) new File(fn).delete else false
  /**
   * version of the library
   */
  def ver = {
    val v = new Array[Int](3)
    H5get_libversion(v)
    "" + v(0) + "." + v(1) + "." + v(2)
  }
}

/**
 * implicits definitions for ease of use
 */
object hdf_hlpr {
  implicit def __int2spc__(d: Int) = h_spc(d)
  implicit def __lng2spc__(d: Long) = h_spc(d)
}