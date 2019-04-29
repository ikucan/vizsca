package ik.util.strm

import java.nio.ByteOrder
import java.nio.ByteBuffer

abstract class byt_strm(val o: ByteOrder) {
  def <<(c: Character): byt_strm
  def <<(b: Byte): byt_strm
  def <<(s: Short): byt_strm
  def <<(i: Int): byt_strm
  def <<(f: Float): byt_strm
  def <<(d: Double): byt_strm
  def <<(s: String): byt_strm
  def <<(ab: Array[Byte]): byt_strm
  def <<(as: Array[Short]): byt_strm
  def <<(ai: Array[Int]): byt_strm
  def <<(al: Array[Long]): byt_strm
  def <<(af: Array[Float]): byt_strm
  def <<(ad: Array[Double]): byt_strm
  def <<(s: byt_strm): byt_strm = this << s.byts
  def <<~(c: Character): byt_strm = this << c.toString
  def <<~(b: Byte): byt_strm = this << b.toString
  def <<~(s: Short): byt_strm = this << s.toString
  def <<~(i: Int): byt_strm = this << i.toString
  def <<~(l: Long): byt_strm = this << l.toString
  def <<~(f: Float): byt_strm = this << f.toString
  def <<~(d: Double): byt_strm = this << d.toString
  def byts: Array[Byte]
  def sz: Int
  def avail: Int
}

object byt_strm {
  def apply(): byt_strm = apply(16 * 1024, ByteOrder.LITTLE_ENDIAN)
  def apply(init_sz: Int): byt_strm = apply(init_sz, ByteOrder.LITTLE_ENDIAN)
  def apply(init_sz: Int, o: ByteOrder): byt_strm = new smpl_bb_byt_strm(o, init_sz)
}

class smpl_bb_byt_strm(override val o: ByteOrder, init_sz: Int) extends byt_strm(o) {
  var bb = ByteBuffer.allocate(init_sz)

  override def <<(c: Character): byt_strm = {
    chk(32)
    bb.putChar(c)
    this
  }
  override def <<(b: Byte): byt_strm = {
    chk(32)
    bb.put(b)
    this
  }
  override def <<(s: Short): byt_strm = {
    chk(32)
    bb.putShort(s)
    this
  }
  override def <<(i: Int): byt_strm = {
    chk(32)
    bb.putInt(i)
    this
  }
  override def <<(f: Float): byt_strm = {
    chk(32)
    bb.putFloat(f)
    this
  }
  override def <<(d: Double): byt_strm = {
    chk(32)
    bb.putDouble(d)
    this
  }
  override def <<(s: String): byt_strm = {
    chk(32 + s.size)
    bb.put(s.getBytes)
    this
  }
  override def <<(ab: Array[Byte]): byt_strm = {
    chk(ab.size)
    bb.put(ab)
    this
  }
  override def <<(as: Array[Short]): byt_strm = {
    chk(as.size * 2 + 32)
    bb.asShortBuffer.put(as)
    this
  }
  override def <<(ai: Array[Int]): byt_strm = {
    chk(ai.size * 4 + 32)
    bb.asIntBuffer.put(ai)
    this
  }
  override def <<(al: Array[Long]): byt_strm = {
    chk(al.size * 8 + 32)
    bb.asLongBuffer.put(al)
    this
  }
  override def <<(af: Array[Float]): byt_strm = {
    chk(af.size * 4 + 32)
    bb.asFloatBuffer.put(af)
    this
  }
  override def <<(ad: Array[Double]): byt_strm = {
    chk(ad.size + 8 + 32)
    bb.asDoubleBuffer.put(ad)
    this
  }

  override def byts: Array[Byte] = {
    val bff = new Array[Byte](avail)
    bb.flip
    bb.get(bff)
    bb.clear
    bff
  }

  override def sz: Int = bb.capacity
  override def avail: Int = bb.position

  private def chk(rqrd: Int) = if (rqrd >= bb.remaining) bb = ByteBuffer.allocate(5 * sz / 4 + rqrd).put(bb.array, 0, bb.position)

  //  private def chk(rqrd: Int) = {
  //    println("cpcty, lmt, pos, mrk, rmn : (" + bb.capacity + ", " + bb.limit + ", " + bb.position + ", " + bb.remaining + ")")
  //    println("need: " + rqrd + ", have: " + bb.remaining )
  //    if (rqrd >= bb.remaining) {
  //      bb = ByteBuffer.allocate(5 * sz / 4 + rqrd).put(bb.array, 0, bb.position)
  //      println("NEW:: cpcty, lmt, pos, mrk, rmn : (" + bb.capacity + ", " + bb.limit + ", " + bb.position + ", " + bb.remaining + ")")
  //    }
  //  }
}