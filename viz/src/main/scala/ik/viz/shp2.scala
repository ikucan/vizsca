package ik.viz

import processing.core.PConstants

/**
 * trait describing shape of a point
 */
trait shp2 {
  def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float): Unit
}

/**
 * a dot shape
 */
class dot2 extends shp2 {
  override def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float) = {
    p.strokeWeight(ln_sz)
    p.ellipseMode(PConstants.CENTER)
    p.ellipse(x, y, sz, sz)
  }
}

class crcl2 extends shp2 {
  override def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float) = {
    p.noFill
    p.strokeWeight(ln_sz)
    p.ellipseMode(PConstants.CENTER)
    p.ellipse(x, y, sz, sz)
  }
}

/**
 * an x-type cross shape
 */
class x2 extends shp2 {
  override def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float) = {
    val D = sz / 2f
    p.strokeWeight(ln_sz)
    p.line(x, y, x + D, y + D)
    p.line(x, y, x + D, y - D)
    p.line(x, y, x - D, y + D)
    p.line(x, y, x - D, y - D)
  }
}

/**
 * a +-type cros shape
 */
class pls2 extends shp2 {
  override def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float) = {
    val D = sz / 2f
    p.strokeWeight(ln_sz)
    p.line(x, y, x + D, y)
    p.line(x, y, x - D, y)
    p.line(x, y, x, y + D)
    p.line(x, y, x, y - D)
  }
}

/**
 * an upside down empty triangle
 * bottom apex at x,y
 */
//class <(sz: Int) extends shape {
class lt2 extends shp2 {
  override def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float) = {
    val D = sz / 2f
    p.strokeWeight(ln_sz)
    p.line(x, y, x - D, y - sz)
    p.line(x - D, y - sz, x + D, y - sz)
    p.line(x + D, y - sz, x, y)
  }
}
/**
 * downward filled triangle
 * bottom apex at x,y
 */
class lte2 extends shp2 {
  override def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float) = {
    val D = sz / 2f
    p.strokeWeight(ln_sz)
    p.triangle(x, y, x - D, y - sz, x + D, y - sz)
  }
}

/**
 * an upright triangle - vertical apex at top
 */
class gt2 extends shp2 {
  override def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float) = {
    val D = sz / 2f
    p.strokeWeight(ln_sz)
    p.line(x, y, x - D, y + sz)
    p.line(x - D, y + sz, x + D, y + sz)
    p.line(x + D, y + sz, x, y)
  }
}
/**
 * an upright triangle - vertical apex at top
 */
class gte2 extends shp2 {
  override def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float) = {
    val D = sz / 2f
    p.strokeWeight(ln_sz)
    p.triangle(x, y, x - D, y + sz, x + D, y + sz)
  }
}

/**
 * square box shape
 */
class bx2 extends shp2 {
  override def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float) = {
    val D = sz / 2f
    val (x1, y1, x2, y2) = (x - D, y - D, x + D, y + D)
    p.rect(x1, y1, x2 - x1, y2 - y1, 1)
  }
}

/**
 * daimond box shape
 */
class ltgt2 extends shp2 {
  override def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float) = {
    val D = sz / 2f
    p.strokeWeight(ln_sz)
    p.quad(x + D, y, x, y + D, x - D, y, x, y - D)
  }
}

/**
 * draw an aplhanumeric
 */
class lvl2 extends shp2 {
  override def drw(p: p_sktch, x: Float, y: Float, sz: Float, ln_sz: Float) = {
    //val D = sz / 2f
    //p.strokeWeight(ln_sz)
    //p.quad(x + D, y, x, y + D, x - D, y, x, y - D)
  }
}

/**
 * shape factory
 */
object shp2 {
  def dot = new dot2
  def bx = new bx2
  def x = new x2
  def pls = new pls2
  def o = new crcl2
  def < = new lt2
  def <= = new lte2
  def > = new gt2
  def >= = new gte2
  def <> = new ltgt2
}
