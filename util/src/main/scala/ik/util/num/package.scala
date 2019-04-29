package ik.util

/**
 * namespace for various non-specialised numeric helpers, functions etc...
 */
package object num {
  import scala.math._
  def rng_lmt(v: Double, mn: Double, mx: Double): Double = min(max(v, mn), mx)
  def rng_lmt(v: Float, mn: Float, mx: Float): Float = min(max(v, mn), mx)
  def rng_lmt(v: Int, mn: Int, mx: Int): Int = min(max(v, mn), mx)
  def rng_lmt(v: Long, mn: Long, mx: Long): Long = min(max(v, mn), mx)
}