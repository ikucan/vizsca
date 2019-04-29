package ik.util.num

object geom {

  def dst(x1: Double, y1: Double, x2: Double, y2: Double): Double = {
    val (dx, dy) = ((x2 - x1), (y2 - y1))
    math.sqrt((dx * dx + dy * dy).toDouble)
  }
}