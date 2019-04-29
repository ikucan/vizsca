package ik.util

import java.io.FileOutputStream

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.language.dynamics

/**
 * JSON object model and parser
 * everything defined at package level, just include jsn._
 */
package object jsn {

  class jsn_err(m: String, t: Throwable) extends RuntimeException(m, t) { def this(m: String) = this(m, null) }

  /**
   * abstract root. does little
   */
  abstract class j_typ extends Dynamic {
    def sz = 0

    def to_str: String = throw new jsn_err("method not supported")
    def to_num: Double = throw new jsn_err("method not supported")
    def to_obj: j_obj = throw new jsn_err("method not supported")
    def to_arr: j_arr = throw new jsn_err("method not supported")
    def to_bol: Boolean = throw new jsn_err("method not supported")

    /**
     * this interface might be useful??
     * >>
     */
    def is_nll: Boolean = false
    //        def is_str: Boolean = false
    //        def is_num: Boolean = false
    def is_obj: Boolean = false
    //        def is_arr: Boolean = false
    //        def is_bol: Boolean = false
    //        def is_arr: Boolean = false

    def get(ky: String): j_typ = throw new jsn_err("method not supported")

    def to_jtyp(v: Any): j_typ = if (v.getClass.isArray) arr_to_jtyp(v) else any_to_jtyp(v)

    def any_to_jtyp(v: Any): j_typ =
      v match {
        case s: Short   => j_num(s)
        case i: Int     => j_num(i)
        case l: Long    => j_num(l)
        case d: Double  => j_num(d)
        case f: Float   => j_num(f)
        case b: Byte    => j_num(b)
        case s: String  => j_str(s)
        case b: Boolean => if (b) j_tru() else j_fls()
        case jt: j_typ  => jt
        case othr       => throw new jsn_err("value " + v + " has type " + v.getClass.getCanonicalName + " is not currentlly supported by this json implementation")
      }

    def arr_to_jtyp(v: Any): j_typ =
      v match {
        case i: Array[Int]     => j_int_arr(i)
        case l: Array[Long]    => j_lng_arr(l)
        case d: Array[Double]  => j_dbl_arr(d)
        case s: Array[Short]   => j_arr() += (s map (sv => j_num(sv)) toList)
        case f: Array[Float]   => j_arr() += (f map (fv => j_num(fv)) toList)
        case b: Array[Byte]    => j_arr() += (b map (bv => j_num(bv)) toList)
        case s: Array[String]  => j_arr() += (s map (sv => j_str(sv)) toList)
        case b: Array[Boolean] => j_arr() += (b map (bv => if (bv) j_tru() else j_fls()) toList)
        case aa: Array[Any]    => j_arr() += (aa map (av => any_to_jtyp(av)) toList)
        case jt: j_typ         => jt
        case othr              => throw new jsn_err("BUG(?). type " + v.getClass.getCanonicalName + " is not an array.")
      }

    /**
     * handle the xxx.<name>(<n1>=<p1> *) type calls
     * only supported on objects so return object type
     */
    def applyDynamicNamed(nm: String)(args: (String, Any)*) = {
      if (!nm.equals("apply")) throw new jsn_err("!dynamic! operation not supported: " + nm)
      args foreach (kvp => updateDynamic(kvp._1)(kvp._2))
      this obj
    }

    /**
     * handle the xxx.<name> type calls
     */
    def selectDynamic(k: String) =
      this match {
        //case o: j_obj => o.apply(k)
        case o: j_obj => o.get(k)
        case _        => throw new jsn_err("only objects can have child-data")
      }

    /**
     * handle the xxx.<name>=<value> type calls
     */
    def updateDynamic(k: String)(v: Any): j_typ =
      this match {
        case o: j_obj => o += (k, to_jtyp(v))
        case _        => throw new jsn_err("can only insert child-data into objects")
      }

    //    def apply(i: Int): j_typ = throw new jsn_err("method not implemented.")
    //    def apply(k: String): j_typ = throw new jsn_err("method not implemented.")
    def keys: List[String] = throw new jsn_err("method not implemented.")
    def has(k: String) = keys.contains(k)

    /**
     * serialisation
     */
    def to_jsn(sb: StringBuilder): StringBuilder
    def to_jsn(): String = to_jsn(new StringBuilder).toString
    /**
     * a VERY INEFFICIENT pretty print implementation
     */
    def prtty = {
      val flt = to_jsn()
      val sb = new StringBuilder(flt.size * 2)
      var ind = 0;

      def indnt = for (i <- 0 until ind) sb += ' '

      flt foreach { ch =>
        ch match {
          case '{' =>
            sb ++= "{\n"; ind += 2; indnt
          case ':' =>
            sb ++= " : "
          case ',' =>
            sb ++= ",\n"; indnt
          case '}' =>
            sb += '\n'; ind -= 2; indnt; sb += '}'
          case x =>
            sb += x
        }
      }
      sb.toString
    }

    /**
     * some syntactic candy for cleaner notation when using
     */
    def ~ = to_jsn
    def obj = to_obj
    def arr = to_arr
    def str = to_str
    def int = to_num.toInt
    def dbl = to_num.toDouble
    def flt = to_num.toFloat
    def bol = to_bol
  }

  /**
   * marker interface for...
   * json objects which can hold other objects.
   */
  trait j_cntnr extends j_typ {
    //def apply(i:Int):j_typ = throw new jsn_err("method not supported")
    def map[B](f: j_typ => B): Iterable[B] = throw new jsn_err("method not supported")
    def foreach(f: j_typ => Unit): Unit = throw new jsn_err("method not supported")
  }

  /**
   * json object. {...}. is mutable!
   * TODO:>> not thread safe!
   */
  case class j_obj(d: LinkedHashMap[String, j_typ] = new LinkedHashMap[String, j_typ]) extends j_cntnr {

    /**
     * list all the keys in the object
     */
    override def keys: List[String] = d.keys.toList

    override def sz = d.size

    /**
     * retrieve a child element by key
     */
    //override def apply(k: String): j_typ =
    override def get(k: String): j_typ =
      if (d.contains(k)) d(k)
      else throw new jsn_err("key " + k + " does not exist on object.")

    /**
     * add a member to the object
     */
    def +=(k: String, v: String): j_typ = +=(k, new j_str(v))
    def +=(k: String, v: j_typ) = if (!d.contains(k)) { d(k) = v; v } else throw new jsn_err("json object already contains value at key '" + k + "'")
    /**
     * force add (overwrite if it exists already) a member to the object.
     */
    def +=!(k: String, v: String): j_typ = +=!(k, new j_str(v))
    def +=!(k: String, v: j_typ) = { d(k) = v; v }

    /**
     * cast as an object
     */
    override def to_obj: j_obj = this

    override def is_obj: Boolean = true

    override def map[B](f: j_typ => B): Iterable[B] = d.values map f

    override def foreach(f: j_typ => Unit): Unit = d.values foreach f

    /**
     * serialise the object and its contents to a json stream
     */
    override def to_jsn(sb: StringBuilder): StringBuilder = {
      def jzn(kvp: (String, j_typ)) = { sb += '\"'; sb ++= kvp._1; sb ++= "\":"; kvp._2.to_jsn(sb) }
      sb += '{'
      if (d.size > 0) {
        jzn(d.head)
        d.tail foreach { kvp => sb ++= ","; jzn(kvp) }
      }
      sb += '}'
      sb
    }

    /**
     * save the json into the file
     */
    def sv(f: String) = {
      val fos = new FileOutputStream(f)
      fos.write(to_jsn().getBytes)
      fos.close
    }
  }

  /**
   * encapsulation of a json array value
   */
  //case class j_arr(ll: List[j_typ]) extends j_cntnr {
  case class j_arr() extends j_cntnr {
    private val l = new ListBuffer[j_typ]

    /**
     * add a member to the object
     */
    def +=(v: j_typ) = { l += v; v }
    def +=(v: Seq[j_typ]) = { l ++= v; this }

    def apply(i: Int) = if (i < l.size) l(i) else throw new jsn_err("index out of bounds :" + i + ". array only has " + l.size + " elements.")

    override def sz = l.size

    override def to_jsn(sb: StringBuilder): StringBuilder = {
      sb += '['
      if (l.size > 0) {
        l.head.to_jsn(sb)
        l.tail foreach { v => sb ++= ", "; v.to_jsn(sb) }
      }
      sb += ']'
    }

    override def toString: String = {
      val sb = new StringBuilder
      sb ++= "j_arr("
      if (l.size > 0) {
        sb ++= l.head.toString
        l.tail foreach { v => sb ++= ", " + v }
      }
      sb += ')'
      sb.toString
    }

    override def to_arr: j_arr = this
    override def map[B](f: j_typ => B): Iterable[B] = l map f
    override def foreach(f: j_typ => Unit): Unit = l foreach f

    def toArray = l.toArray
    def toList = l.toList
  }

  /**
   * specialisation for integer arrays
   */
  case class j_int_arr(l: Array[Int]) extends j_typ {
    override def to_jsn(sb: StringBuilder): StringBuilder = jsnz(sb, true)
    private def jsnz(sb: StringBuilder, optmz: Boolean): StringBuilder = {
      sb += '['
      if (optmz) sb += 'i'
      if (l.size > 0) {
        sb ++= (l.head.toString)
        l.tail foreach { v => sb ++= ", "; sb ++= (v.toString) }
      }
      sb += ']'
    }
  }
  /**
   * specialisation for long arrays
   */
  case class j_lng_arr(l: Array[Long]) extends j_typ {
    override def to_jsn(sb: StringBuilder): StringBuilder = jsnz(sb, true)
    private def jsnz(sb: StringBuilder, optmz: Boolean): StringBuilder = {
      sb += '['
      if (optmz) sb += 'l'
      if (l.size > 0) {
        sb ++= (l.head.toString)
        l.tail foreach { v => sb ++= ", "; sb ++= (v.toString) }
      }
      sb += ']'
    }
  }
  /**
   * specialisation for double arrays
   */
  case class j_dbl_arr(l: Array[Double]) extends j_typ {
    override def to_jsn(sb: StringBuilder): StringBuilder = jsnz(sb, true)
    private def jsnz(sb: StringBuilder, optmz: Boolean): StringBuilder = {
      sb += '['
      if (optmz) sb += 'd'
      if (l.size > 0) {
        sb ++= (l.head.toString)
        l.tail foreach { v => sb ++= ", "; sb ++= (v.toString) }
      }
      sb += ']'
    }
  }

  /**
   * array builder !!optimisation!!. scan a list of already defined j_typ s to see
   * if they are numeric and if so of uniform numeric type. if so, optimise storage
   */
  object j_arr_bldr {
    def apply(arr: List[j_typ]) = {
      var (num, whl, int, mx, mn, dbls) = (true, true, true, Double.MinValue, Double.MaxValue, new Array[Double](arr.size))
      var i = 0;
      while (num && i < arr.size) {
        arr(i) match {
          case nm: j_num =>
            dbls(i) = nm.to_num
            int &&= dbls(i).isValidInt
            whl &&= dbls(i).isWhole

          case othr => num = false
        }
        i += 1
      }
      if (num && int) j_int_arr(dbls map (_.toInt))
      else if (num && whl) j_lng_arr(dbls map (_.toLong))
      else if (num) j_dbl_arr(dbls)
      else j_arr() += (arr)
    }
  }

  /**
   * json string object
   */
  case class j_str(s: String) extends j_typ {
    override def to_str = s
    override def to_num = s.toDouble
    override def to_jsn(sb: StringBuilder): StringBuilder = { sb += '\"'; sb ++= s; sb += '\"' }
  }
  /**
   * json number object
   */
  case class j_num(n: Double) extends j_typ {
    override def to_str = n.toString
    override def to_num = n
    override def to_jsn(sb: StringBuilder): StringBuilder = sb ++= n.toString
  }
  /**
   * json true object
   */
  case class j_tru() extends j_typ {
    override def to_str = "true"
    override def to_bol: Boolean = true
    override def to_jsn(sb: StringBuilder): StringBuilder = sb ++= "true"
  }
  /**
   * json false object
   */
  case class j_fls() extends j_typ {
    override def to_str = "false"
    override def to_bol: Boolean = false
    override def to_jsn(sb: StringBuilder): StringBuilder = sb ++= "false"
  }
  /**
   * json null object
   */
  case class j_nll() extends j_typ {
    override def to_str = null
    override def to_num = Double.NaN
    override def to_jsn(sb: StringBuilder): StringBuilder = sb ++= "null"
    override def is_nll = true
  }

}
