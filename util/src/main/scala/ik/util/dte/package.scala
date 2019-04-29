package ik.util

/**
 * date and time manipulation routines
 */
import java.util.GregorianCalendar
import java.util.Date
import java.util.Calendar
import java.text.SimpleDateFormat

package object dte {

  implicit def lng2drtn(t: Long) = new drtn(t)
  implicit def lng2drtn(t: Int) = new drtn(t.toLong)
  implicit def dt2wrppr(dt: Date) = new dt_wrppr(dt)

  def now = new Date
  def tme = System.currentTimeMillis

  /**
   * a wrapper for calendaring functionality around the date
   */
  class dt_wrppr(dt: Date) {
    val cc = new GregorianCalendar(tz())
    cc.setTime(dt)

    def dow = cc.get(Calendar.DAY_OF_WEEK)
    def is_sun = dow == Calendar.SUNDAY
    def is_sat = dow == Calendar.SATURDAY
    def is_mon = dow == Calendar.MONDAY
    def is_tue = dow == Calendar.TUESDAY
    def is_wed = dow == Calendar.WEDNESDAY
    def is_thu = dow == Calendar.THURSDAY
    def is_fri = dow == Calendar.FRIDAY
    def is_wknd = is_sun || is_sat
    def is_wkdy = !is_wknd

    def dow_str = new SimpleDateFormat("EEEE").format(dt)

    def trm = {
      val cc = new GregorianCalendar(tz())
      cc.setTime(dt)
      cc.set(Calendar.HOUR_OF_DAY, 0)
      cc.set(Calendar.MINUTE, 0)
      cc.set(Calendar.SECOND, 0)
      cc.set(Calendar.MILLISECOND, 0)
      cc.getTime
    }

    def -(ms: Long): Date = new Date(dt.getTime - ms)
    def -(dys: Double): Date = this.-((dys * 24.0 * 3600000.0).toLong)

  }

}