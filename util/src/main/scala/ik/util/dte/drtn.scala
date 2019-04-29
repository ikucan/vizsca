package ik.util.dte

/**
 * duration
 */
class drtn(n: Long) {
  def SEC = n * 1000l
  def MIN = n * 60000l
  def HR = n * 3600000l
  def DAY = n * 24 * 3600000l
}
