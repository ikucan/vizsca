package ik.util

import java.util.concurrent.Executors
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.concurrent.future
import java.util.TimeZone

/**
 * a timezone utility
 */
object tz {

  /**
   * set timezone as default, throws an exception if the timezone is not known
   */
  def apply(tz_nm: String) =
    if (tzs.contains(tz_nm)) TimeZone.setDefault(TimeZone.getTimeZone(tz_nm))
    else throw new utl_err("attempting to set an unsupported timezone : " + tz_nm + ". valid timezones: " + tzs)

  /**
   * return currentlly set timezone
   */
  def apply() = TimeZone.getDefault

  /**
   * list all known timezones
   */
  private def tzs = Set(TimeZone.getAvailableIDs: _*)

}
