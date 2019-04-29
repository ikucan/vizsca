package ik.util.jsn

import scala.collection.mutable.Stack
import scala.language.dynamics
import java.io.FileInputStream
import scala.collection.mutable.ArrayBuffer

/**
  * the parser class
  */
class prsr {
  def err_msg(i: Int) = {
    val (n_pre, n_pst) = (100, 50)
    val n_strt = Math.max(0, i - n_pre)
    val msg = new StringBuilder() ++= ("json err. char >>" + bffr(i).toChar + "<< (" + bffr(i) + ") @ index " + i + "\n")
    msg ++= ("\n\t" + new String(bffr.slice(n_strt, i + n_pst)))
    msg ++= "\n\t"
    for (ii <- n_strt until i) msg += '-'
    msg ++= "*\n"
    msg.toString
  }

  val WHT_SPC = (0x00 to 0x20) toArray;

  /**
    * definition of all possible states
    */
  object st extends Enumeration {
    val INIT, ERR, OBJ_OPN, OBJ_CLS, CMMA = Value
    val KSCN, K_DN, PRE_VAL, SSCN, ESC_SSCN, S_DN = Value
    val T1, T2, T3, T4, F1, F2, F3, F4, F5, N1, N2, N3, N4 = Value
    val N_NG, N_ZR, N_PS, N_SP, N_DC, N_E_SP, N_EE = Value
    val ARR_OPN, ARR_CLS = Value
    val U_CD2, U_CD3 , U_CD3_2 = Value // utf-8 parsing sttes
  }

  /**
    * transition table.
    * each transition maps a list of transition functions. upon a transition, there are invoked in order
    */
  object tx_tbl {
    /**
      * grammar: http://json.org/
      */
    val tx = Array.ofDim[(st.Value, List[(Int) => Unit])](st.values.size, 256)
    /**
      * by default, all transitions are erroneous. except for the ones explicitly
      * being set up as correct below
      */
    for (s <- st.values; c <- 0 until 256) tx(s.id)(c) = (st.ERR, List(f_err))
    /**
      * start in INITIAL state. only one valid transition
      */
    tx(st.INIT.id)('{') = (st.OBJ_OPN, List())
    tx(st.INIT.id)('[') = (st.ARR_OPN, List(f_set_arr, f_skp_ky))

    /**
      * valid transitions from OBJECT OPEN state
      */
    tx(st.OBJ_OPN.id)('}') = (st.OBJ_CLS, List(f_pop))
    tx(st.OBJ_OPN.id)('\"') = (st.KSCN, List(f_kst))
    WHT_SPC foreach { c => tx(st.OBJ_OPN.id)(c) = (st.OBJ_OPN, List()) }

    /**
      * transitions from OBJECT CLOSE
      */
    tx(st.OBJ_CLS.id)('}') = (st.OBJ_CLS, List(f_pop))
    tx(st.OBJ_CLS.id)(',') = (st.CMMA, List(f_skp_ky))
    tx(st.OBJ_CLS.id)(']') = (st.ARR_CLS, List(f_chk_arr, f_pop))
    WHT_SPC foreach { c => tx(st.OBJ_CLS.id)(c) = (st.OBJ_CLS, List()) }

    /**
      * transitions from the KEY SCAN state
      */
    for (c <- 0x20 to 0x7e) tx(st.KSCN.id)(c) = (st.KSCN, List())
    tx(st.KSCN.id)('\"') = (st.K_DN, List(f_kdn))

    /**
      * transitions from the KEY DONE state
      */
    tx(st.K_DN.id)(':') = (st.PRE_VAL, List())
    WHT_SPC foreach { c => tx(st.K_DN.id)(c) = (st.K_DN, List()) }

    /**
      * transitions out of KVP SEPARATOR (':') FOUND
      */
    tx(st.PRE_VAL.id)('\"') = (st.SSCN, List(f_sst))
    tx(st.PRE_VAL.id)('{') = (st.OBJ_OPN, List(f_psh_obj))
    tx(st.PRE_VAL.id)('t') = (st.T1, List())
    tx(st.PRE_VAL.id)('f') = (st.F1, List())
    tx(st.PRE_VAL.id)('n') = (st.N1, List())
    tx(st.PRE_VAL.id)('-') = (st.N_NG, List(f_nm_st))
    tx(st.PRE_VAL.id)('0') = (st.N_ZR, List(f_nm_st))
    tx(st.PRE_VAL.id)('[') = (st.ARR_OPN, List(f_psh_arr, f_skp_ky))
    tx(st.PRE_VAL.id)(']') = (st.ARR_CLS, List(f_chk_arr, f_pop))
    for (c <- 0x31 to 0x39) tx(st.PRE_VAL.id)(c) = (st.N_PS, List(f_nm_st))
    WHT_SPC foreach { c => tx(st.PRE_VAL.id)(c) = (st.PRE_VAL, List()) }

    /**
      * transitions from ARRAY CLOSED state
      */
    tx(st.ARR_CLS.id)('}') = (st.OBJ_CLS, List(f_pop))
    tx(st.ARR_CLS.id)(']') = (st.OBJ_CLS, List(f_chk_arr, f_pop))
    tx(st.ARR_CLS.id)(',') = (st.CMMA, List(f_skp_ky))
    WHT_SPC foreach { c => tx(st.ARR_CLS.id)(c) = (st.ARR_CLS, List()) }

    /**
      * transitions from a NEGATIVE NUMBER OPERATOR ('-')
      */
    for (c <- 0x31 to 0x39) tx(st.N_NG.id)(c) = (st.N_PS, List())
    tx(st.N_NG.id)('0') = (st.N_ZR, List())
    /**
      * transitions from a NUMBER PREFIX
      */
    for (c <- 0x30 to 0x39) tx(st.N_PS.id)(c) = (st.N_PS, List())
    tx(st.N_PS.id)('e') = (st.N_E_SP, List())
    tx(st.N_PS.id)('E') = (st.N_E_SP, List())
    tx(st.N_PS.id)('.') = (st.N_SP, List())
    tx(st.N_PS.id)(',') = (st.CMMA, List(f_nm_dn, f_skp_ky))
    tx(st.N_PS.id)('}') = (st.OBJ_CLS, List(f_nm_dn, f_pop))
    tx(st.N_PS.id)(']') = (st.ARR_CLS, List(f_chk_arr, f_nm_dn, f_pop))
    WHT_SPC foreach { c => tx(st.N_PS.id)(c) = (st.N_PS, List()) }

    /**
      * tranistions from a ZERO prefix
      */
    tx(st.N_ZR.id)('.') = (st.N_SP, List())
    tx(st.N_ZR.id)(',') = (st.CMMA, List(f_nm_dn, f_skp_ky))
    tx(st.N_ZR.id)('}') = (st.OBJ_CLS, List(f_nm_dn, f_pop))
    tx(st.N_ZR.id)(']') = (st.ARR_CLS, List(f_chk_arr, f_nm_dn, f_pop))
    WHT_SPC foreach { c => tx(st.N_ZR.id)(c) = (st.N_ZR, List()) }

    for (c <- 0x30 to 0x39) tx(st.N_SP.id)(c) = (st.N_DC, List())
    for (c <- 0x30 to 0x39) tx(st.N_DC.id)(c) = (st.N_DC, List())

    tx(st.N_DC.id)('e') = (st.N_E_SP, List())
    tx(st.N_DC.id)('E') = (st.N_E_SP, List())
    tx(st.N_DC.id)(',') = (st.CMMA, List(f_nm_dn, f_skp_ky))
    tx(st.N_DC.id)('}') = (st.OBJ_CLS, List(f_nm_dn, f_pop))
    tx(st.N_DC.id)(']') = (st.ARR_CLS, List(f_chk_arr, f_nm_dn, f_pop))
    WHT_SPC foreach { c => tx(st.N_DC.id)(c) = (st.N_DC, List()) }

    /**
      * transitions from the E SEPARATOR STATE
      * (what after the 'e'/'E' has been parsed)
      */
    for (c <- 0x30 to 0x39) tx(st.N_E_SP.id)(c) = (st.N_EE, List())
    tx(st.N_E_SP.id)('-') = (st.N_EE, List())

    /**
      * transitions from the E EXPONENT STATE
      */
    for (c <- 0x30 to 0x39) tx(st.N_EE.id)(c) = (st.N_EE, List())
    tx(st.N_EE.id)(',') = (st.CMMA, List(f_nm_dn, f_skp_ky))
    tx(st.N_EE.id)('}') = (st.OBJ_CLS, List(f_nm_dn, f_pop))
    tx(st.N_EE.id)(']') = (st.ARR_CLS, List(f_chk_arr, f_nm_dn, f_pop))

    /**
      * transitions from STRING SCAN STATE
      */
    for (c <- 0x20 to 0x7e) tx(st.SSCN.id)(c) = (st.SSCN, List())
    tx(st.SSCN.id)('\"') = (st.S_DN, List(f_sdn))
    tx(st.SSCN.id)('\\') = (st.ESC_SSCN, List())
    tx(st.SSCN.id)(0xc2) = (st.U_CD2, List())
    tx(st.SSCN.id)(0xe2) = (st.U_CD3, List())
    /**
      * transitions from STRING ESCAPE SCAN STATE
      */
    for (c <- 0x20 to 0x7e) tx(st.ESC_SSCN.id)(c) = (st.SSCN, List())

    /**
      * some feeble UTF-8 scanning. for complete list look at this:
      * http://www.fileformat.info/info/charset/UTF-8/list.htm
      * note that UTF-8 information is lost here!
      *   0xc2 utf-8 encodings are 2 chars including prefix...
      *   0xe2 utf-8 encodings are 3 chars including prefix...
      */
    for (i <- 0x80 to 0xbf) tx(st.U_CD2.id)(i) = (st.SSCN, List())
    for (i <- 0x80 to 0xbf) tx(st.U_CD3.id)(i) = (st.U_CD3_2, List())
    for (i <- 0x80 to 0xbf) tx(st.U_CD3_2.id)(i) = (st.SSCN, List())

    /**
      * transitions from (well..) TRUE SCAN states
      */
    tx(st.T1.id)('r') = (st.T2, List())
    tx(st.T2.id)('u') = (st.T3, List())
    tx(st.T3.id)('e') = (st.T4, List(f_tdn))
    tx(st.T4.id)(',') = (st.CMMA, List(f_skp_ky))
    tx(st.T4.id)('}') = (st.OBJ_CLS, List(f_pop))
    tx(st.T4.id)(']') = (st.ARR_CLS, List(f_chk_arr, f_pop))
    WHT_SPC foreach { c => tx(st.T4.id)(c) = (st.T4, List()) }

    /**
      * transitions from FALSE SCAN states
      */
    tx(st.F1.id)('a') = (st.F2, List())
    tx(st.F2.id)('l') = (st.F3, List())
    tx(st.F3.id)('s') = (st.F4, List())
    tx(st.F4.id)('e') = (st.F5, List(f_fdn))
    tx(st.F5.id)(',') = (st.CMMA, List(f_skp_ky))
    tx(st.F5.id)('}') = (st.OBJ_CLS, List(f_pop))
    tx(st.F5.id)(']') = (st.ARR_CLS, List(f_chk_arr, f_pop))
    WHT_SPC foreach { c => tx(st.F5.id)(c) = (st.F5, List()) }

    /**
      * transitions from NULL SCAN states
      */
    tx(st.N1.id)('u') = (st.N2, List())
    tx(st.N2.id)('l') = (st.N3, List())
    tx(st.N3.id)('l') = (st.N4, List(f_ndn))
    tx(st.N4.id)(',') = (st.CMMA, List(f_skp_ky))
    tx(st.N4.id)('}') = (st.OBJ_CLS, List(f_pop))
    tx(st.N4.id)(']') = (st.ARR_CLS, List(f_chk_arr, f_pop))
    WHT_SPC foreach { c => tx(st.N4.id)(c) = (st.N4, List()) }

    /**
      * from the STRING DONE STATE
      */
    tx(st.S_DN.id)(',') = (st.CMMA, List(f_skp_ky))
    tx(st.S_DN.id)('}') = (st.OBJ_CLS, List(f_pop))
    tx(st.S_DN.id)(']') = (st.ARR_CLS, List(f_chk_arr, f_pop))
    WHT_SPC foreach { c => tx(st.S_DN.id)(c) = (st.S_DN, List()) }
    /**
      * from the OBJECT SEPARATOR STATE
      */
    tx(st.CMMA.id)('\"') = (st.KSCN, List(f_kst))
    WHT_SPC foreach { c => tx(st.CMMA.id)(c) = (st.CMMA, List()) }
  }

  /**
    * transition functions
    */
  val f_err = (i: Int) => {
    throw new jsn_err(err_msg(i))
  }

  val f_psh_obj = (i: Int) => psh(i, j_obj())
  val f_psh_arr = (i: Int) => psh(i, j_arr())
  val f_set_arr = (i: Int) => _crrnt = j_arr()

  /**
    * add new container to the current container, keyed depending on current container
    * push current container and make new container current
    */
  private def psh(i: Int, nu: j_cntnr) = {
    add(i, nu)
    _stck push _crrnt
    _crrnt = nu
  }

  val f_oo: (Int) => Unit = (i: Int) => {
    add(i, j_nll())
  }
  val f_pop: (Int) => Unit = (i: Int) => if (_stck.size > 0) _crrnt = _stck pop // else throw new jsn_err("BUG:>> trying to pop an empty stack. " + err_msg(i))
  val f_kst: (Int) => Unit = (i: Int) => {
    _ki0 = i + 1;
    _ki1 = _ki0
  }
  val f_kdn: (Int) => Unit = (i: Int) => _ki1 = i
  val f_sst: (Int) => Unit = (i: Int) => {
    _vi0 = i + 1;
    _vi1 = _vi0
  }
  val f_sdn: (Int) => Unit = (i: Int) => {
    _vi1 = i;
    add(i, j_str(new String(bffr, _vi0, _vi1 - _vi0)))
  }
  val f_tdn: (Int) => Unit = (i: Int) => add(i, j_tru())
  val f_fdn: (Int) => Unit = (i: Int) => add(i, j_fls())
  val f_ndn: (Int) => Unit = (i: Int) => add(i, j_nll())
  val f_nm_st: (Int) => Unit = (i: Int) => {
    _vi0 = i;
    _vi1 = _vi0
  }
  val f_nm_dn: (Int) => Unit = (i: Int) => {
    _vi1 = i;
    add(i, j_num(new String(bffr, _vi0, _vi1 - _vi0).toDouble))
  }
  val f_skp_ky: (Int) => Unit = (i: Int) => if (arry_mde(i)) s = st.PRE_VAL
  val f_chk_arr: (Int) => Unit = (i: Int) => if (!arry_mde(i)) throw new jsn_err(err_msg(i))

  /**
    * add a value to the current container, depending on what the container is...
    */
  private def add(i: Int, v: j_typ) =
    _crrnt match {
      case o: j_obj => o += (new String(bffr, _ki0, _ki1 - _ki0), v)
      case a: j_arr => a += v
      case xx => throw new jsn_err("BUG: unknown container type. " + err_msg(i))
    }

  private def arry_mde(i: Int) =
    _crrnt match {
      case o: j_obj => false
      case a: j_arr => true
      case xx => throw new jsn_err("BUG: unknown container type. " + err_msg(i))
    }

  /**
    * parser state
    */
  private var _stck = Stack[j_cntnr]()
  private var _crrnt: j_cntnr = new j_obj
  private var bffr: Array[Byte] = null
  private var ((_ki0, _ki1), (_vi0, _vi1)) = ((0, 0), (0, 0))
  private var s = st.INIT

  /**
    * xxx
    */
  def consume(chrs: Array[Byte], i: Int, n: Int): Int = {
    var ii = i
    while ((ii < n)) {
      val chr = chrs(ii) & 0xff
      //print("tknsr (" + ii + "):>> " + s + "(" + _stck.size + ", " + arry_mde(ii) + "):" + chr.toChar)
      val (new_state, foo_list) = tx_tbl.tx(s.id)(chr)
      s = new_state
      foo_list foreach (_ (ii))
      //println(" =*=> " + s + "(" + _stck.size + ", " + arry_mde(i) + ")")
      //        }
      ii += 1
    }
    ii - i
  }

  /**
    * consume token stream
    */
  def apply(chrs: String): j_typ = apply(chrs.getBytes)

  def apply(chrs: Array[Byte]): j_typ = apply(chrs, chrs.length)

  def apply(chrs: Array[Byte], n: Int): j_typ = {
    bffr = chrs
    var i = 0
    while (i < n) {
      val di = consume(chrs, i, n)
      i += di
    }
    _crrnt
  }
}

/**
  * companion object for simpler creation
  */
object prsr {
  def apply(jsn: Array[Byte]) = new prsr()(jsn)

  def apply(jsn: String) = new prsr()(jsn)

  def rd(fnm: String) = {
    val fis = new FileInputStream(fnm)
    val (bffr, tmp) = (new ArrayBuffer[Byte], new Array[Byte](16 * 1024))
    var n = fis.read(tmp)
    while (n > 0) {
      bffr ++= tmp.slice(0, n)
      n = fis.read(tmp)
    }
    fis.close
    apply(bffr.toArray)
  }
}
