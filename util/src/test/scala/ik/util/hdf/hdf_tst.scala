//package ik.util.hdf
//
//import org.junit.runner.RunWith
//import org.scalatest.junit._
//import org.scalatest._
//import ik.util.hdf._
//import java.util.Date
//import java.io.File
//import ncsa.hdf.hdf5lib.H5
//import ncsa.hdf.hdf5lib.HDF5Constants
//import ik.util.ttoc._
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//
////import htd.hdf.h_int32
//
//@RunWith(classOf[JUnitRunner])
//class hdf_tst extends FlatSpec with MustMatchers {
//
//  import HDF5Constants._
//  import H5._
//
//  "hdf" must "be able to create an empty file" in {
//    val fn = "ut1.hdf5"
//    val f0 = h_fle(fn)
//    f0.cls
//    val f1 = h_fle(fn, true)
//    f1.cls
//    new File(fn).delete
//    println(" ut1 >>> " + h_lib.n_opn)
//  }
//  "hdf" must "be able to create an empty database" in {
//    val fn = "ut2.hdf5"
//    val db1 = h_db.mk(fn)
//    db1.cls
//    val db2 = h_db.opn(fn)
//    db2.cls
//    new File(fn).delete
//    println(" ut2 >>> " + h_lib.n_opn)
//  }
//
//  "hdf" must "be able to create groups" in {
//    val fn = "ut3.hdf5";
//    {
//      val db = h_db.mk(fn)
//      db.f.n_opn_grp must equal(1)
//      db.exist("/abc") must be(false)
//      val g0 = db mk_grp "/abc"
//      val g220 = db mk_grp "/abc"
//      db.exist("/abc") must be(true)
//      db.f.n_opn_grp must equal(2)
//
//      db.exist("/fed/cba") must be(false)
//      val g1 = db mk_grp "/fed/cba"
//      db.exist("/fed/cba") must be(true)
//      db.root.exist("/fed/cba") must be(true)
//      db("/fed").exist("/cba") must be(true)
//      db.f.n_opn_grp must equal(4)
//
//      db.exist("/fed/dcb") must be(false)
//      val g2 = db mk_grp "/fed/dcb"
//      db.exist("/fed/dcb") must be(true)
//      db.root.exist("/fed/dcb") must be(true)
//      db("/fed").exist("/dcb") must be(true)
//      db.f.n_opn_grp must equal(5)
//
//      val g3 = db mk_grp "/fed/dcb"
//      val g4 = db mk_grp "/fed/dcb"
//      db.f.n_opn_grp must equal(5)
//      val g5 = db mk_grp "/gfe/dcb/azy"
//      db.f.n_opn_grp must equal(8)
//      val g6 = db mk_grp "/gfe/dcb/zyx"
//      db.f.n_opn_grp must equal(9)
//      g6.cls
//      db.f.n_opn_grp must equal(8)
//      g5.cls
//      db.f.n_opn_grp must equal(7)
//      g4.cls
//      db.f.n_opn_grp must equal(6)
//      g3.cls
//      db.f.n_opn_grp must equal(6)
//      db.cls
//    };
//    {
//      val db = h_db(fn)
//      db.f.n_opn_grp must equal(1)
//      db("/abc").nm must equal("abc")
//      db("abc").nm must equal("abc")
//      db("abc").nm must equal("abc")
//      db("/fed").nm must equal("fed")
//      db("/fed/cba").nm must equal("cba")
//      db("/fed/dcb").nm must equal("dcb")
//      db("/gfe").nm must equal("gfe")
//      db("/gfe/dcb").nm must equal("dcb")
//      db("/gfe/dcb/azy").nm must equal("azy")
//      db("/gfe/dcb/zyx").nm must equal("zyx")
//      db("fed")("cba").nm must equal("cba")
//      db("fed")("dcb").nm must equal("dcb")
//      db("/gfe")("dcb").nm must equal("dcb")
//      db("/gfe")("dcb")("azy").nm must equal("azy")
//      db("/gfe")("dcb")("zyx").nm must equal("zyx")
//      db("/gfe")("dcb/azy").nm must equal("azy")
//      db("/gfe")("dcb/zyx").nm must equal("zyx")
//      //println(db2.root("fed")("cba").nm)
//      db.cls
//    }
//    new File(fn).delete
//    println(" ut3 >>> " + h_lib.n_opn)
//  }
//  "hdf" must "be able to accept attributes" in {
//    val fn = "ut4.hdf5";
//    val N = 8100
//    val tst_str = "~!\"£ABCDEFG¬`|\\~@:{}]abcdzxcwetyr[;'#/.,<>?=+-_0)9"
//
//    {
//      val db1 = h_db.mk(fn);
//      {
//        val g1 = db1 mk_grp "/g1"
//        g1.set("att0", (0 until N).toArray map (_.toByte))
//        g1.set("att1", (0 until N).toArray map (_.toShort))
//        g1.set("att2", (0 until N).toArray map (_.toInt))
//        g1.set("att3", (0 until N).toArray map (_.toLong))
//        g1.set("att4", (0 until N).toArray map (_.toFloat))
//        g1.set("att5", (0 until N).toArray map (_.toDouble))
//        g1.set("att6", tst_str.getBytes)
//        g1.set("att7", tst_str.toCharArray)
//        //g1.set("attX", (0 until N).toArray map (_.toString))
//      }
//      db1.cls
//      println(" ut4.1 >>> " + h_lib.n_opn)
//      println(" ut4.1 >>> " + h_lib.opn_ids)
//    }
//    {
//      val db1 = h_db(fn)
//      db1("/g1").nm must equal("g1")
//      val g1 = db1("/g1")
//
//      g1.get_byt("att0") must equal((0 until N).toArray map (_.toByte))
//      g1.get_sht("att1") must equal((0 until N).toArray map (_.toShort))
//      g1.get_int("att2") must equal((0 until N).toArray map (_.toInt))
//      g1.get_lng("att3") must equal((0 until N).toArray map (_.toLong))
//      g1.get_flt("att4") must equal((0 until N).toArray map (_.toFloat))
//      g1.get_dbl("att5") must equal((0 until N).toArray map (_.toDouble))
//      new String(g1.get_byt("att6")) must equal(tst_str)
//      new String(g1.get_chr("att7")) must equal(tst_str)
//
//      db1.cls
//    }
//
//    new File(fn).delete
//    println(" ut4.2 >>> " + h_lib.n_opn)
//    println(" ut4.2 >>> " + h_lib.opn_ids)
//
//  }
//
//  "hdf" must "be able to write and read linear data" in {
//    val fn = "ut5.hdf5";
//    //val N = 1024 * 1024
//    val N = 32
//
//    val arr = (0 until N).toArray
//    val chr = arr map (x => (x + 65).toChar)
//    val byt = arr map (_.toByte)
//    val sht = arr map (_.toShort)
//    val int = arr map (_.toInt)
//    val lng = arr map (_.toLong)
//    val dbl = arr map (_.toDouble)
//    val flt = arr map (_.toFloat)
//    val str = arr map ("str>>" + _.toString + "<<")
//    val m_arr = arr map (-1 *)
//    val m_chr = m_arr map (_.toChar)
//    val m_byt = m_arr map (_.toByte)
//    val m_sht = m_arr map (_.toShort)
//    val m_int = m_arr map (_.toInt)
//    val m_lng = m_arr map (_.toLong)
//    val m_dbl = m_arr map (_.toDouble)
//    val m_flt = m_arr map (_.toFloat)
//    val m_str = m_arr map ("str>>" + _.toString + "<<")
//
//    {
//      new File(fn).delete
//      val db1 = h_db.mk(fn);
//      {
//        val g1 = db1 mk_grp "/g1"
//        tic
//        val ds_1 = g1.wrt("d1", chr)
//        val ds_2 = g1.wrt("d2", byt)
//        val ds_3 = g1.wrt("d3", sht)
//        val ds_4 = g1.wrt("d4", int)
//        val ds_5 = g1.wrt("d5", lng)
//        val ds_6 = g1.wrt("d6", dbl)
//        val ds_7 = g1.wrt("d7", flt)
//        val ds_8 = g1.wrt("d8", str)
//      }
//      db1.cls
//      val db2 = h_db.opn(fn)
//      db2.cls
//
//    }
//    {
//      val db1 = h_db(fn)
//      db1("/g1").nm must equal("g1")
//      val g1 = db1("/g1")
//      g1.rd_chr("d1") must equal(chr)
//      g1.rd_byt("d2") must equal(byt)
//      g1.rd_sht("d3") must equal(sht)
//      g1.rd_int("d4") must equal(int)
//      g1.rd_lng("d5") must equal(lng)
//      g1.rd_flt("d6") must equal(flt)
//      g1.rd_dbl("d7") must equal(dbl)
//      g1.rd_str("d8") must equal(str)
//      db1.cls
//      val db2 = h_db.opn(fn)
//      db2.cls
//    }
//
//    println(" ut5.2 >>> " + h_lib.n_opn)
//    println(" ut5.2 >>> " + h_lib.opn_ids)
//
//    {
//      val db1 = h_db(fn)
//      db1("/g1").nm must equal("g1")
//      val g1 = db1("/g1")
//
//      g1.chng("d1", Array[Long](4), Array[Char]('a', 'a', 'a', 'a'))
//      g1.chng("d2", Array[Long](5), Array[Byte](-1, -1, -1, -1))
//      g1.rd_byt("d2", 3, 8) must equal(Array[Byte](3, 4, -1, -1, -1, -1, 9, 10))
//      g1.chng("d3", Array[Long](6), Array[Short](-1, -1, -1, -1))
//      g1.rd_sht("d3", 5, 13) must equal(Array[Short](5, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17))
//      g1.chng("d4", Array[Long](7), Array[Int](-1, -1, -1, -1))
//      g1.rd_int("d4", 5, 10) must equal(Array[Int](5, 6, -1, -1, -1, -1, 11, 12, 13, 14))
//      g1.chng("d5", Array[Long](5), Array[Long](-1, -1, -1, -1))
//      g1.rd_lng("d5", 2, 9) must equal(Array[Long](2, 3, 4, -1, -1, -1, -1, 9, 10))
//      g1.chng("d6", Array[Long](6), Array[Double](-1, -1, -1, -1))
//      g1.rd_flt("d6", 2, 11) must equal(Array[Double](2, 3, 4, 5, -1, -1, -1, -1, 10, 11, 12))
//      g1.chng("d7", Array[Long](5), Array[Float](-1, -1, -1))
//      g1.rd_flt("d7", 5, 3) must equal(Array[Float](-1, -1, -1))
//      g1.chng("d8", Array[Long](4), Array[String]("000", "000", "000", "000", "000"))
//      g1.rd_str("d8", 4, 5) must equal(Array[String]("000", "000", "000", "000", "000"))
//      db1.cls
//      val db2 = h_db.opn(fn)
//      db2.cls
//
//    }
//
//    new File(fn).delete
//    println(" ut5.xx >>> " + h_lib.n_opn)
//    println(" ut5.xx >>> " + h_lib.opn_ids)
//
//  }
//
//  "hdf" must "be able to accurately update data from buffers" in {
//    println("----------")
//    val fn = "ut5.5.hdf5";
//    new File(fn).delete
//    val N = 128
//
//    {
//      val db1 = h_db(fn)
//      val g1 = db1 mk_grp "/g1"
//      val v = (0 until N) toArray
//      //g1.add_dst("ds1", h_spc(4), h_int32())
//      val dst = h_dst("ae", g1, h_spc(14), h_int32)
//      h_int32.f_wrt_dst(dst.id, dst.s.id, dst.s.id, v)
//      dst.cls
//      db1.cls
//    }
//    new File(fn).delete
//
//    println(" ut5.5.xx >>> " + h_lib.n_opn)
//    println(" ut5.5.xx >>> " + h_lib.opn_ids)
//
//  }
//
//  //
//  "hdf" must "be able to write and 2D data" in {
//    val fn = "ut6.hdf5";
//    {
//      {
//
//        for (M <- 5 until 10; N <- 500 until 510) {
//          //for (M <- 2 until 3; N <- 500 until 501) {
//          val db1 = h_db.mk(fn);
//          val g1 = db1 mk_grp "/g1"
//          g1.wrt("t0", h_spc(M, N), (0 until M * N) map (_.toChar) toArray)
//          g1.rd_chr("t0") must equal((0 until M * N) map (_.toChar) toArray)
//          g1.wrt("t1", h_spc(M, N), (0 until M * N) map (_.toByte) toArray)
//          g1.rd_byt("t1") must equal((0 until M * N) map (_.toByte) toArray)
//          g1.wrt("t2", h_spc(M, N), (0 until M * N) map (_.toShort) toArray)
//          g1.rd_sht("t2") must equal((0 until M * N) map (_.toShort) toArray)
//          g1.wrt("t3", h_spc(M, N), (0 until M * N) map (_.toInt) toArray)
//          g1.rd_int("t3") must equal((0 until M * N) map (_.toInt) toArray)
//          g1.wrt("t4", h_spc(M, N), (0 until M * N) map (_.toLong) toArray)
//          g1.rd_lng("t4") must equal((0 until M * N) map (_.toLong) toArray)
//          g1.wrt("t5", h_spc(M, N), (0 until M * N) map (_.toFloat) toArray)
//          g1.rd_flt("t5") must equal((0 until M * N) map (_.toFloat) toArray)
//          g1.wrt("t6", h_spc(M, N), (0 until M * N) map (_.toDouble) toArray)
//          g1.rd_dbl("t6") must equal((0 until M * N) map (_.toDouble) toArray)
//          g1.wrt("t7", h_spc(M, N), (0 until M * N) map (_.toString) toArray)
//          g1.rd_str("t7") must equal((0 until M * N) map (_.toString) toArray)
//          db1.cls
//          val db2 = h_db.opn(fn)
//          db2.cls
//        }
//      }
//    }
//    new File(fn).delete
//
//    println(" ut6.xx >>> " + h_lib.n_opn)
//    println(" ut6.xx >>> " + h_lib.opn_ids)
//  }
//
//  "hdf" must "be able to write and 3D data" in {
//    val fn = "ut7.hdf5";
//    {
//      {
//        //for (M <- 20 until 25; N <- 10 until 15; O <- 40 until 45) {
//        for (M <- 20 until 22; N <- 10 until 12; O <- 40 until 42) {
//          val db1 = h_db.mk(fn);
//          val g1 = db1 mk_grp "/g1"
//
//          g1.wrt("t0", h_spc(M, N, O), (0 until M * N * O) map (_.toChar) toArray)
//          g1.rd_chr("t0") must equal((0 until M * N * O) map (_.toChar) toArray)
//          g1.wrt("t1", h_spc(M, N, O), (0 until M * N * O) map (_.toByte) toArray)
//          g1.rd_byt("t1") must equal((0 until M * N * O) map (_.toByte) toArray)
//          g1.wrt("t2", h_spc(M, N, O), (0 until M * N * O) map (_.toShort) toArray)
//          g1.rd_sht("t2") must equal((0 until M * N * O) map (_.toShort) toArray)
//          g1.wrt("t3", h_spc(M, N, O), (0 until M * N * O) map (_.toInt) toArray)
//          g1.rd_int("t3") must equal((0 until M * N * O) map (_.toInt) toArray)
//          g1.wrt("t4", h_spc(M, N, O), (0 until M * N * O) map (_.toLong) toArray)
//          g1.rd_lng("t4") must equal((0 until M * N * O) map (_.toLong) toArray)
//          g1.wrt("t5", h_spc(M, N, O), (0 until M * N * O) map (_.toFloat) toArray)
//          g1.rd_flt("t5") must equal((0 until M * N * O) map (_.toFloat) toArray)
//          g1.wrt("t6", h_spc(M, N, O), (0 until M * N * O) map (_.toDouble) toArray)
//          g1.rd_dbl("t6") must equal((0 until M * N * O) map (_.toDouble) toArray)
//          g1.wrt("t7", h_spc(M, N, O), (0 until M * N * O) map (_.toString) toArray)
//          g1.rd_str("t7") must equal((0 until M * N * O) map (_.toString) toArray)
//
//          db1.cls
//        }
//      }
//    }
//    new File(fn).delete
//
//    println(" ut7.xx >>> " + h_lib.n_opn)
//    println(" ut7.xx >>> " + h_lib.opn_ids)
//
//  }
//
//  "hdf" must "be able to shard 1D data" in {
//    val fn = "ut8.hdf5"
//    val N = 10 * 512 * 1024 / 128
//    val M = 512 * 1024 / 128
//    //    val N = 10
//    //    val M = 3
//    val arr = (1 to N).toArray
//    val ch = arr map (x => (x % 20 + 60).toChar)
//    val by = arr map (_.toByte)
//    val sh = arr map (_.toShort)
//    val in = arr map (_.toInt)
//    val ln = arr map (_.toLong)
//    val fl = arr map (_.toFloat)
//    val db = arr map (_.toDouble)
//    val st = arr map (_.toString)
//
//    {
//      println(" ut8.1 >>> " + h_lib.n_opn)
//      println(" ut8.1 >>> " + h_lib.opn_ids)
//
//      new File(fn).delete
//      val db1 = h_db(fn);
//      {
//        {
//          val g1 = db1 mk_grp "g1"
//          tic
//          var i = 0; while (i < N) { g1.shrd("d_" + i, M, i, ch); i += M }
//          toc("chr wrt" + N)
//        }
//        {
//          val g1 = db1 mk_grp "g2"
//          tic
//          var i = 0; while (i < N) { g1.shrd("d_" + i, M, i, by); i += M }
//          toc("byt wrt" + N)
//        }
//        {
//          val g1 = db1 mk_grp "g3"
//          tic
//          var i = 0; while (i < N) { g1.shrd("d_" + i, M, i, sh); i += M }
//          toc("sht wrt" + N)
//        }
//        {
//          val g1 = db1 mk_grp "g4"
//          tic
//          var i = 0; while (i < N) { g1.shrd("d_" + i, M, i, in); i += M }
//          toc("int wrt" + N)
//        }
//        {
//          val g1 = db1 mk_grp "g5"
//          tic
//          var i = 0; while (i < N) { g1.shrd("d_" + i, M, i, ln); i += M }
//          toc("lng wrt" + N)
//        }
//        {
//          val g1 = db1 mk_grp "g6"
//          tic
//          var i = 0; while (i < N) { g1.shrd("d_" + i, M, i, fl); i += M }
//          toc("flt wrt" + N)
//        }
//        {
//          val g1 = db1 mk_grp "g7"
//          tic
//          var i = 0; while (i < N) { g1.shrd("d_" + i, M, i, db); i += M }
//          toc("dbl wrt" + N)
//        }
//      }
//      db1.cls
//      println(" ut8.22 >>> " + h_lib.n_opn)
//      println(" ut8.22 >>> " + h_lib.opn_ids)
//
//    }
//    {
//      val db1 = h_db(fn);
//      {
//        val ch2 = new Array[Char](N)
//        val by2 = new Array[Byte](N)
//        val sh2 = new Array[Short](N)
//        val in2 = new Array[Int](N)
//        val ln2 = new Array[Long](N)
//        val fl2 = new Array[Float](N)
//        val db2 = new Array[Double](N)
//
//        {
//          val g1 = db1("g1")
//          tic
//          var i = 0; while (i < N) { g1.splc("d_" + i, i, ch2); i += M }
//          toc("chr rd " + N)
//          ch2 must equal(ch)
//          println(g1.dsts)
//        }
//        {
//          val g1 = db1("g2")
//          tic
//          var i = 0; while (i < N) { g1.splc("d_" + i, i, by2); i += M }
//          toc("byt rd " + N)
//          by2 must equal(by)
//          println(g1.dsts)
//        }
//        {
//          val g1 = db1("g3")
//          tic
//          var i = 0; while (i < N) { g1.splc("d_" + i, i, sh2); i += M }
//          toc("sht rd " + N)
//          sh2 must equal(sh)
//          println(g1.dsts)
//        }
//        {
//          val g1 = db1("g4")
//          tic
//          var i = 0; while (i < N) { g1.splc("d_" + i, i, in2); i += M }
//          toc("int rd " + N)
//          in2 must equal(in)
//          println(g1.dsts)
//        }
//        {
//          val g1 = db1("g5")
//          tic
//          var i = 0; while (i < N) { g1.splc("d_" + i, i, ln2); i += M }
//          toc("lng rd " + N)
//          ln2 must equal(ln)
//          println(g1.dsts)
//        }
//        {
//          val g1 = db1("g6")
//          tic
//          var i = 0; while (i < N) { g1.splc("d_" + i, i, fl2); i += M }
//          toc("flt rd " + N)
//          fl2 must equal(fl)
//          println(g1.dsts)
//        }
//        {
//          val g1 = db1("g7")
//          tic
//          var i = 0; while (i < N) { g1.splc("d_" + i, i, db2); i += M }
//          toc("dbl rd " + N)
//          db2 must equal(db)
//          println(g1.dsts)
//        }
//        println(db1("/").grps)
//      }
//      db1.cls
//
//      println(" ut8.xx >>> " + h_lib.n_opn)
//      println(" ut8.xx >>> " + h_lib.opn_ids)
//
//    }
//    //    {
//    //      val db1 = h_db(fn);
//    //      {
//    //        val by3 = new Array[Byte](N)
//    //        val sh3 = new Array[Short](N)
//    //        val in3 = new Array[Int](N)
//    //        val ln3 = new Array[Long](N)
//    //        val fl3 = new Array[Float](N)
//    //        val db3 = new Array[Double](N)
//    //
//    //        {
//    //          val g1 = db1("g2")
//    //          tic
//    //          var i = 0; while (i < N) { g1.splc(g1.dsts("d_" + i), i, by3); i += M }
//    //          toc("byt rd " + N)
//    //          by3 must equal(by)
//    //        }
//    //        {
//    //          val g1 = db1("g3")
//    //          tic
//    //          var i = 0; while (i < N) { g1.splc(g1.dsts("d_" + i), i, sh3); i += M }
//    //          toc("sht rd " + N)
//    //          sh3 must equal(sh)
//    //        }
//    //        {
//    //          val g1 = db1("g4")
//    //          tic
//    //          var i = 0; while (i < N) { g1.splc(g1.dsts("d_" + i), i, in3); i += M }
//    //          toc("int rd " + N)
//    //          in3 must equal(in)
//    //        }
//    //        {
//    //          val g1 = db1("g5")
//    //          tic
//    //          var i = 0; while (i < N) { g1.splc(g1.dsts("d_" + i), i, ln3); i += M }
//    //          toc("lng rd " + N)
//    //          ln3 must equal(ln)
//    //        }
//    //        {
//    //          val g1 = db1("g6")
//    //          tic
//    //          var i = 0; while (i < N) { g1.splc(g1.dsts("d_" + i), i, fl3); i += M }
//    //          toc("flt rd " + N)
//    //          fl3 must equal(fl)
//    //        }
//    //        {
//    //          val g1 = db1("g7")
//    //          tic
//    //          var i = 0; while (i < N) { g1.splc(g1.dsts("d_" + i), i, db3); i += M }
//    //          toc("dbl rd " + N)
//    //          db3 must equal(db)
//    //        }
//    //      }
//    //      db1.cls
//    //    }
//    //    h_db.dstry(fn)
//    //}
//
//    //   EXAMPEL{
//    //    val fn = "ut6.hdf5"
//    //
//    //    val N = 32
//    //    val arr = (0 until N).toArray
//    //    val chr = arr map (_.toChar)
//    //    val byt = arr map (_.toByte)
//    //    val sht = arr map (_.toShort)
//    //    val int = arr map (_.toInt)
//    //    val lng = arr map (_.toLong)
//    //    val dbl = arr map (_.toDouble)
//    //    val flt = arr map (_.toFloat)
//    //    val str = arr map ("str>>" + _.toString + "<<")
//    //
//    //    {
//    //      val db1 = h_db(fn, true);
//    //      {
//    //        val g1 = db1 mk_grp "/g1"
//    //        tic
//    //        //        val ds_1 = g1.wrt("d1", chr)
//    //        //        val ds_2 = g1.wrt("d2", byt)
//    //        //        val ds_3 = g1.wrt("d3", sht)
//    //        val ds_4 = g1.wrt("d4", int)
//    //        //        val ds_5 = g1.wrt("d5", lng)
//    //        //        val ds_6 = g1.wrt("d6", dbl)
//    //        //        val ds_7 = g1.wrt("d7", flt)
//    //        val ds_8 = g1.wrt("d8", str)
//    //        toc("wrote " + N + " values")
//    //      }
//    //      db1.cls
//    //    }
//    //    {
//    //      val db1 = h_db(fn);
//    //      {
//    //        val g1 = db1 mk_grp "/g1"
//    //        val ds4 = g1.opn_dst("d4")
//    //        val d_id = ds4.id
//    //        val s_id = ds4.s.id
//    //        val t_id = ds4.t.tid
//    //
//    //        H5Sselect_hyperslab(s_id, H5S_SELECT_SET, Array[Long](4), null, Array[Long](5), null)
//    //        val memspace = H5Screate_simple(1, Array[Long](5), null)
//    //        H5Sselect_hyperslab(memspace, H5S_SELECT_SET, Array[Long](0), null, Array[Long](5), null)
//    //
//    //        //        val bfr = new Array[Byte](20)
//    //        //        val ret = new Array[Int](5)
//    //        //        H5Dread(d_id, H5T_NATIVE_INT, memspace, s_id, H5P_DEFAULT, bfr)
//    //        //        ByteBuffer.wrap(bfr).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(ret)
//    //        val ret = new Array[Int](5)
//    //        H5Dread_int(d_id, H5T_NATIVE_INT, memspace, s_id, H5P_DEFAULT, ret)
//    //        println(ret.toList)
//    //      }
//    //      {
//    //        val g1 = db1 mk_grp "/g1"
//    //        val ds4 = g1.opn_dst("d4")
//    //        val d_id = ds4.id
//    //        val s_id = ds4.s.id
//    //        val t_id = ds4.t.tid
//    //
//    //        H5Sselect_hyperslab(s_id, H5S_SELECT_SET, Array[Long](4), null, Array[Long](5), null)
//    //        val memspace = H5Screate_simple(1, Array[Long](5), nu ll)
//    //        H5Sselect_hyperslab(memspace, H5S_SELECT_SET, Array[Long](0), null, Array[Long](5), null)
//    //
//    //        val ret = new Array[Int](5)
//    //
//    //        H5Dread_int(d_id, H5T_NATIVE_INT, memspace, s_id, H5P_DEFAULT, ret)
//    //        H5Dwrite_int(d_id, H5T_NATIVE_INT, memspace, s_id, H5P_DEFAULT, ret map (_ - 10))
//    //        H5Dread_int(d_id, H5T_NATIVE_INT, memspace, s_id, H5P_DEFAULT, ret)
//    //        println(ret.toList)
//    //      }
//    //
//    //      {
//    //        val g1 = db1 mk_grp "/g1"
//    //        val ds4 = g1.opn_dst("d8")
//    //        val d_id = ds4.id
//    //        val s_id = ds4.s.id
//    //        val t_id = ds4.t.tid
//    //
//    //        H5Sselect_hyperslab(s_id, H5S_SELECT_SET, Array[Long](4), null, Array[Long](5), null)
//    //        val memspace = H5Screate_simple(1, Array[Long](5), null)
//    //        H5Sselect_hyperslab(memspace, H5S_SELECT_SET, Array[Long](0), null, Array[Long](5), null)
//    //
//    //        //        val bfr = new Array[Byte](20)
//    //        //        val ret = new Array[Int](5)
//    //        //        H5Dread(d_id, H5T_NATIVE_INT, memspace, s_id, H5P_DEFAULT, bfr)
//    //        //        ByteBuffer.wrap(bfr).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(ret)
//    //        val ret = new Array[String](5)
//    //        H5Dread(d_id, h_str().tid, memspace, s_id, H5P_DEFAULT, ret)
//    //        println(ret.toList)
//    //        H5Dwrite(d_id, h_str().tid, memspace, s_id, H5P_DEFAULT, ret map (_ + "XXX[["))
//    //        H5Dread(d_id, h_str().tid, memspace, s_id, H5P_DEFAULT, ret)
//    //        println(ret.toList)
//    //      }
//    //
//    //      db1.cls
//    //    }
//    //    //h_db.dstry(fn)
//    //  }
//
//    //  "hdf" must "be able to write and read string attrributes" in {
//    //    val fn = "ut7.hdf5";
//    //    val N = 32
//    //
//    //    new File(fn).delete
//    //    val db1 = h_db(fn)
//    //    val g1 = db1.mk_grp("/g1")
//    //    
//    //    
//    //       /* Create string attribute.  */
//    //   val aid3  = H5Screate_simple (1, Array(5l), null)
//    //   val atype = H5Tcopy(H5T_C_S1);
//    //   H5Tset_size(atype, 4);
//    //   H5Tset_strpad(atype,H5T_STR_NULLTERM);
//    //   val attr3 = H5Acreate(g1.id, "baaah", atype, aid3, H5P_DEFAULT);
//    //   H5Awrite(attr3, atype, Array("asdf")); 
//    //    
//    //    //g1.set("tst_att", Array("1","3", "3"))
//    //    
//    //    
//    //    db1.cls
//    //
//    //  }
//  }
//}
