package ik.util.df

import java.util.concurrent.Executors
import scala.Array.canBuildFrom
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Sorting
import scala.language.dynamics
import ik.util.vctr.mk_vctr_wrppr
import scala.collection.SortedMap
import scala.collection.immutable.ListMap

/**
  * @author iztok.kucan
  * @date
  *  a data frame class. logically similar to R/python data frames.
  *  for ease of use it implements a dynamic interface to allow for named
  * access. instead of frm.get("abc") we can simply say frm.abc.
  *  supports all main primitive types and strings.
  *
  *  TODO:>>
  *  1. parallelise the crap out of this (eg. the ++ function)
  *  2. be nice to be able to NOT have to do operations separately for each type!
  *  3. provide a companion builder class
  */
class frm extends frm_ops with frm_wrtr with frm_rdr with Dynamic {

  /**
    * required for Futures
    */
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

  /**
    * handle the xxx.<name> type calls
    */
  def selectDynamic[T](n: String)(implicit m: Manifest[T]): Array[T] = get_arg[T](n)

  /**
    * handle the xxx.<name>(<p1> *) type calls
    */
  //    def applyDynamic(name: String)(args: Any*) {
  //      println("applyDynamic: " + name + " :: " + args)
  //      throw new df_err("applyDynamic: " + name + " :: " + args)
  //    }

  /**
    * handle the xxx.<name>(<n1>=<p1> *) type calls
    */
  def applyDynamicNamed(nm: String)(args: (String, Any)*): frm = {
    if (nm != "apply") throw new df_err("Method " + nm + " not implemented. Class df is dynamic so this error doesnt apear during compilation.")
    args foreach set_arg
    this
  }

  /**
    * handle the xxx.<name>=<value> type calls
    */
  def updateDynamic(name: String)(value: Any) = {
    set_arg(name, value)
    this
  }

  /**
    * list of column names
    */
  def cols = col_nms map (_.name) toList

  /**
    * select values from the frame using a boolean index
    */
  def slct(idx: Array[Boolean]) = {
    import ik.util.vctr._
    val sub_frm = new frm
    bol_cols foreach { c => sub_frm.set_arg((c._1, c._2(idx))) }
    chr_cols foreach { c => sub_frm.set_arg((c._1, c._2(idx))) }
    byt_cols foreach { c => sub_frm.set_arg((c._1, c._2(idx))) }
    sht_cols foreach { c => sub_frm.set_arg((c._1, c._2(idx))) }
    int_cols foreach { c => sub_frm.set_arg((c._1, c._2(idx))) }
    lng_cols foreach { c => sub_frm.set_arg((c._1, c._2(idx))) }
    flt_cols foreach { c => sub_frm.set_arg((c._1, c._2(idx))) }
    dbl_cols foreach { c => sub_frm.set_arg((c._1, c._2(idx))) }
    sym_cols foreach { c => sub_frm.set_arg((c._1, c._2(idx))) }
    str_cols foreach { c => sub_frm.set_arg((c._1, c._2(idx))) }
    sub_frm
  }

  /**
    * select values based on a numeric index
    */
  def slct(idx: Array[Int]) = {
    import ik.util.vctr._
    val sub_frm = new frm

    val bol_ftrs = bol_cols map { c =>
      c._1 -> Future {
        c._2(idx)
      }
    }
    val chr_ftrs = chr_cols map { c =>
      c._1 -> Future {
        c._2(idx)
      }
    }
    val byt_ftrs = byt_cols map { c =>
      c._1 -> Future {
        c._2(idx)
      }
    }
    val sht_ftrs = sht_cols map { c =>
      c._1 -> Future {
        c._2(idx)
      }
    }
    val int_ftrs = int_cols map { c =>
      c._1 -> Future {
        c._2(idx)
      }
    }
    val lng_ftrs = lng_cols map { c =>
      c._1 -> Future {
        c._2(idx)
      }
    }
    val dbl_ftrs = dbl_cols map { c =>
      c._1 -> Future {
        c._2(idx)
      }
    }
    val flt_ftrs = flt_cols map { c =>
      c._1 -> Future {
        c._2(idx)
      }
    }
    val sym_ftrs = sym_cols map { c =>
      c._1 -> Future {
        c._2(idx)
      }
    }
    val str_ftrs = str_cols map { c =>
      c._1 -> Future {
        c._2(idx)
      }
    }

    bol_ftrs foreach { c => sub_frm.set_arg(c._1, Await.result(c._2, 1.hour)) }
    chr_ftrs foreach { c => sub_frm.set_arg(c._1, Await.result(c._2, 1.hour)) }
    byt_ftrs foreach { c => sub_frm.set_arg(c._1, Await.result(c._2, 1.hour)) }
    sht_ftrs foreach { c => sub_frm.set_arg(c._1, Await.result(c._2, 1.hour)) }
    int_ftrs foreach { c => sub_frm.set_arg(c._1, Await.result(c._2, 1.hour)) }
    lng_ftrs foreach { c => sub_frm.set_arg(c._1, Await.result(c._2, 1.hour)) }
    flt_ftrs foreach { c => sub_frm.set_arg(c._1, Await.result(c._2, 1.hour)) }
    dbl_ftrs foreach { c => sub_frm.set_arg(c._1, Await.result(c._2, 1.hour)) }
    sym_ftrs foreach { c => sub_frm.set_arg(c._1, Await.result(c._2, 1.hour)) }
    str_ftrs foreach { c => sub_frm.set_arg(c._1, Await.result(c._2, 1.hour)) }

    sub_frm
  }

  /**
    * select a frame on a numeric index
    */
  def slc(i0: Int, i1: Int) = {
    import ik.util.vctr._
    val sub_frm = new frm

    bol_cols foreach { c => sub_frm.set_arg((c._1, c._2.slice(i0, i1))) }
    chr_cols foreach { c => sub_frm.set_arg((c._1, c._2.slice(i0, i1))) }
    byt_cols foreach { c => sub_frm.set_arg((c._1, c._2.slice(i0, i1))) }
    sht_cols foreach { c => sub_frm.set_arg((c._1, c._2.slice(i0, i1))) }
    int_cols foreach { c => sub_frm.set_arg((c._1, c._2.slice(i0, i1))) }
    lng_cols foreach { c => sub_frm.set_arg((c._1, c._2.slice(i0, i1))) }
    flt_cols foreach { c => sub_frm.set_arg((c._1, c._2.slice(i0, i1))) }
    dbl_cols foreach { c => sub_frm.set_arg((c._1, c._2.slice(i0, i1))) }
    sym_cols foreach { c => sub_frm.set_arg((c._1, c._2.slice(i0, i1))) }
    str_cols foreach { c => sub_frm.set_arg((c._1, c._2.slice(i0, i1))) }

    sub_frm
  }

  /**
    * sort by one column
    */
  def sort[T](nm: String)(implicit m1: Manifest[T], o1: Ordering[T]) = {
    val a1 = get_arg[T](nm)
    val srt = (0 until a1.size) map (i => (a1(i), i)) toArray;
    Sorting.stableSort(srt, (v1: (T, Int), v2: (T, Int)) => o1.lt(v1._1, v2._1))
    val idx = srt map (_._2)
    slct(idx)
  }

  /**
    * sort by two columns
    */
  def sort[S, T](n1: String, n2: String)(implicit m1: Manifest[S], m2: Manifest[T], o1: Ordering[S], o2: Ordering[T]) = {
    println("starting sort :>> ")
    val t0 = System.currentTimeMillis
    val (a1, a2) = (get_arg[S](n1), get_arg[T](n2))
    val srt = (0 until a1.size) map (i => (a1(i), a2(i), i)) toArray;
    Sorting.stableSort(srt, (v1: (S, T, Int), v2: (S, T, Int)) => {
      val cmp1 = o1.compare(v1._1, v2._1)
      if (cmp1 < 0) true
      else if (cmp1 == 0) o2.lt(v1._2, v2._2)
      else false
    })
    val idx = srt map (_._3)
    println("sorting done :>> " + ((System.currentTimeMillis - t0)))
    val res = slct(idx)
    println("selecting done :>> " + ((System.currentTimeMillis - t0)))

    res
  }

  /**
    * sort by three columns
    */
  def sort[S, T, U](n1: String, n2: String, n3: String)(implicit m1: Manifest[S], m2: Manifest[T], m3: Manifest[U], o1: Ordering[S], o2: Ordering[T], o3: Ordering[U]) = {
    val (a1, a2, a3) = (get_arg[S](n1), get_arg[T](n2), get_arg[U](n3))
    val srt = (0 until a1.size) map (i => (a1(i), a2(i), a3(i), i)) toArray;
    Sorting.stableSort(srt, (v1: (S, T, U, Int), v2: (S, T, U, Int)) => {
      val cmp1 = o1.compare(v1._1, v2._1)
      if (cmp1 < 0) true
      else if (cmp1 == 0) {
        val cmp2 = o2.compare(v1._2, v2._2)
        if (cmp2 < 0) true
        else if (cmp2 == 0) o3.lt(v1._3, v2._3)
        else false
      } else false
    })
    val idx = srt map (_._4)
    slct(idx)
  }

  var len = -1

  def sz = (len, cols.size)

  /**
    * ---------------
    * implementation
    * ---------------
    */

  /**
    * internal storage for all vectors (columns)
    */
  protected val bol_cols = new HashMap[String, Array[Boolean]]
  protected val chr_cols = new HashMap[String, Array[Char]]
  protected val byt_cols = new HashMap[String, Array[Byte]]
  protected val sht_cols = new HashMap[String, Array[Short]]
  protected val int_cols = new HashMap[String, Array[Int]]
  protected val lng_cols = new HashMap[String, Array[Long]]
  protected val flt_cols = new HashMap[String, Array[Float]]
  protected val dbl_cols = new HashMap[String, Array[Double]]
  protected val sym_cols = new HashMap[String, Array[Symbol]]
  protected val str_cols = new HashMap[String, Array[String]]

  /**
    * index of all columns
    */
  val col_nms = new ListBuffer[Symbol]

  def set_arg(kvp: (String, Any)) = {
    val (n, v) = (kvp._1, kvp._2)
    v match {
      case ab: Array[Boolean] =>
        chk_len(n, ab.size)
        bol_cols(n) = ab
      case ac: Array[Char] =>
        chk_len(n, ac.size)
        chr_cols(n) = ac
      case ab: Array[Byte] =>
        chk_len(n, ab.size)
        byt_cols(n) = ab
      case as: Array[Short] =>
        chk_len(n, as.size)
        sht_cols(n) = as
      case ai: Array[Int] =>
        chk_len(n, ai.size)
        int_cols(n) = ai
      case al: Array[Long] =>
        chk_len(n, al.size)
        lng_cols(n) = al
      case af: Array[Float] =>
        chk_len(n, af.size)
        flt_cols(n) = af
      case ad: Array[Double] =>
        chk_len(n, ad.size)
        dbl_cols(n) = ad
      case as: Array[Symbol] =>
        chk_len(n, as.size)
        sym_cols(n) = as
      case as: Array[String] =>
        chk_len(n, as.size)
        str_cols(n) = as
      case x => throw new df_err("Type (" + x + ")of item named " + n + " is not supported. You must pass an array.")
    }
    if (col_nms contains Symbol(n)) throw new df_err("Duplicate column " + n + ". It already exists.")
    else col_nms += Symbol(n)
  }

  /**
    * provide a type map for each column
    */
  def typ_map = {
    val mp = (bol_cols map (_._1 -> Boolean)) ++
      (chr_cols map (_._1 -> Char)) ++
      (byt_cols map (_._1 -> Byte)) ++
      (sht_cols map (_._1 -> Short)) ++
      (int_cols map (_._1 -> Int)) ++
      (lng_cols map (_._1 -> Long)) ++
      (flt_cols map (_._1 -> Float)) ++
      (dbl_cols map (_._1 -> Double)) ++
      (sym_cols map (_._1 -> Symbol)) ++
      (str_cols map (_._1 -> new String))
    ListMap(col_nms.toList map (c => c.name -> mp(c.name)): _*)
  }

  def typs = typ_map

  /**
    * the underlying implementation of a column getter
    * switches based on type and returns appropriate column if it exists
    */
  def get_arg[T](n: String)(implicit m: Manifest[T]): Array[T] = {
    m.newArray(0) match {
      case ab: Array[Boolean] => bol_cols.getOrElse(n, throw new df_err("boolean column named " + n + " does not exist."))
      case ac: Array[Char] => chr_cols.getOrElse(n, throw new df_err("char column named " + n + " does not exist."))
      case ab: Array[Byte] => byt_cols.getOrElse(n, throw new df_err("byte column named " + n + " does not exist."))
      case as: Array[Short] => sht_cols.getOrElse(n, throw new df_err("short column named " + n + " does not exist."))
      case ai: Array[Int] => int_cols.getOrElse(n, throw new df_err("int column named " + n + " does not exist."))
      case al: Array[Long] => lng_cols.getOrElse(n, throw new df_err("long column named " + n + " does not exist."))
      case af: Array[Float] => flt_cols.getOrElse(n, throw new df_err("float column named " + n + " does not exist."))
      case ad: Array[Double] => dbl_cols.getOrElse(n, throw new df_err("double column named " + n + " does not exist."))
      case as: Array[Symbol] => sym_cols.getOrElse(n, throw new df_err("symbol column named " + n + " does not exist."))
      case as: Array[String] => str_cols.getOrElse(n, throw new df_err("string column named " + n + " does not exist."))
      case _ => throw new df_err("Type of item named " + n + " is not supported.")
    }
  }

  private def chk_len(n: String, l: Int) = {
    if (len == -1) len = l
    else if (l != len) throw new df_err("Incorrect length of column " + n + " is not supported.")
  }
}

object frm {
  /**
    * append fwo frames, provided they are of equal "shape"
    * creates a new frame to contain appended values
    */
  def ++(dfs: frm*) = {
    // filter out empty frames
    val ddfs = dfs filter (_.sz._1 > 0)
    // if no non-empty, return first one...
    if (ddfs.size == 0) dfs.head
    // if only one non-empty - return it
    else if (ddfs.size == 1) ddfs.head
    // else concatenate all frames
    else {
      //val n_rows = (0 /: dfs)(_ + _.sz._1)
      val szs = ddfs map (_.sz._1) toArray
      val idx = 0 +: szs cumsum
      val nu_sz = idx.last

      println(szs toList)
      println(nu_sz)


      val frst = ddfs.head
      val nu_frm = new frm {
        len = nu_sz
        col_nms ++= frst.col_nms
      }

      /**
        * yep, a bit tedious.... first append all the boolean columns
        * todo:>> this shold be parallelised per-type
        */
      frst.bol_cols foreach { c =>
        val bffr = new Array[Boolean](nu_sz)
        var j = 0
        ddfs foreach { df =>
          val src = df.bol_cols(c._1)
          (0 until src.size) foreach { i => bffr(j) = src(i); j += 1 }
        }
        nu_frm.bol_cols(c._1) = bffr
      }

      /**
        * ... then all the character columns
        */
      frst.chr_cols foreach { c =>
        val bffr = new Array[Char](nu_sz)
        var j = 0
        ddfs foreach { df =>
          val src = df.chr_cols(c._1)
          (0 until src.size) foreach { i => bffr(j) = src(i); j += 1 }
        }
        nu_frm.chr_cols(c._1) = bffr
      }

      /**
        * ... byte columns
        */
      frst.byt_cols foreach { c =>
        val bffr = new Array[Byte](nu_sz)
        var j = 0
        ddfs foreach { df =>
          val src = df.byt_cols(c._1)
          (0 until src.size) foreach { i => bffr(j) = src(i); j += 1 }
        }
        nu_frm.byt_cols(c._1) = bffr
      }

      /**
        * ... short
        */
      frst.sht_cols foreach { c =>
        val bffr = new Array[Short](nu_sz)
        var j = 0
        ddfs foreach { df =>
          val src = df.sht_cols(c._1)
          (0 until src.size) foreach { i => bffr(j) = src(i); j += 1 }
        }
        nu_frm.sht_cols(c._1) = bffr
      }

      /**
        * int
        */
      frst.int_cols foreach { c =>
        val bffr = new Array[Int](nu_sz)
        var j = 0
        ddfs foreach { df =>
          val src = df.int_cols(c._1)
          (0 until src.size) foreach { i => bffr(j) = src(i); j += 1 }
        }
        nu_frm.int_cols(c._1) = bffr
      }

      /**
        * long
        */
      frst.lng_cols foreach { c =>
        val bffr = new Array[Long](nu_sz)
        var j = 0
        ddfs foreach { df =>
          val src = df.lng_cols(c._1)
          (0 until src.size) foreach { i => bffr(j) = src(i); j += 1 }
        }
        nu_frm.lng_cols(c._1) = bffr
      }

      /**
        * float
        */
      frst.flt_cols foreach { c =>
        val bffr = new Array[Float](nu_sz)
        var j = 0
        ddfs foreach { df =>
          val src = df.flt_cols(c._1)
          (0 until src.size) foreach { i => bffr(j) = src(i); j += 1 }
        }
        nu_frm.flt_cols(c._1) = bffr
      }

      /**
        * double
        */
      frst.dbl_cols foreach { c =>
        val bffr = new Array[Double](nu_sz)
        var j = 0
        ddfs foreach { df =>
          val src = df.dbl_cols(c._1)
          (0 until src.size) foreach { i => bffr(j) = src(i); j += 1 }
        }
        nu_frm.dbl_cols(c._1) = bffr
      }

      /**
        * symbols
        */
      frst.sym_cols foreach { c =>
        val bffr = new Array[Symbol](nu_sz)
        var j = 0
        ddfs foreach { df =>
          val src = df.sym_cols(c._1)
          (0 until src.size) foreach { i => bffr(j) = src(i); j += 1 }
        }
        nu_frm.sym_cols(c._1) = bffr
      }

      /**
        * strings
        */
      frst.str_cols foreach { c =>
        val bffr = new Array[String](nu_sz)
        var j = 0
        ddfs foreach { df =>
          val src = df.str_cols(c._1)
          (0 until src.size) foreach { i => bffr(j) = src(i); j += 1 }
        }
        nu_frm.str_cols(c._1) = bffr
      }
      nu_frm
    }
  }
}