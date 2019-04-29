package ik.util.ui

//:>> TODO>>.. shift this as per below comment
//@deprecated("poor placement", "this class is viz specific and really should go there...")
class clr(val c: Int) {
  def apply() = c
}

object clr {
  def apply(c: Int) = new clr(c)
  def blk = new clr(0xff000000)
  def wht = new clr(0xffffffff)
  def gry = new clr(0xff999999)
  def red = new clr(0xffff0000)
  def grn = new clr(0xff00ff00)
  def blu = new clr(0xff0000ff)
  def yll = new clr(0xffffff00)

  def apply(o: Int, r: Int, g: Int, b: Int) = {
    val c = (o << 24 & 0xff << 24) | (r << 16 & 0xff << 16) | (g << 8 & 0xff << 8) | (b & 0xff)
    new clr(c)
  }

  implicit def int2clr(c: Int): clr = new clr(c)
}