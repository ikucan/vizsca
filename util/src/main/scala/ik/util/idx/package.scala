package ik.util
package object idx {

  /**
   * create a homogenous grid over a range
   * @param h grid size
   * @v0 first value of the grid
   * @v1 last value of the grid
   * @return a homogenous grid of numbers starting at v0 and ending at v1 if a perfect match or just beyond v1 if not
   */
  def grd(h: Long, v0: Long, v1: Long): Array[Long] = {
    val rng = v1 - v0
    val dv = math.abs(h)
    val N = if (rng % dv == 0) (rng / dv + 1) toInt else (rng / dv + 2) toInt
    val bins = new Array[Long](N)
    for (i <- 0 until N) bins(i) = v0 + i * dv
    bins
  }

}