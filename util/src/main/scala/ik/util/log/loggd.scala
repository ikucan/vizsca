package ik.util.log

import org.apache.logging.log4j.LogManager

/**
 * logger trait to be used as a wrapper for commons logging. it can be used in two ways by the implementing class:
 *   1. the logger member can be accessed directly and used as would in a typical java program
 *   2. implementation can make use wrapper functions which check logging levels first and avoid unnecessary construction of logged objects
 *
 * @author i.kucan
 *
 */
trait loggd {
  val log_ctg = this.getClass.getName
  /**
   * the log member
   */
  lazy val log = LogManager.getLogger(log_ctg)

  /**
   * call the member log's debug function if debug is enabled. the message is generated only if
   * debug level is enabled
   * @param msg user provided message generated function. generates string to be logged when invoked
   */
  def log_dbg(msg: => String) = if (log.isDebugEnabled) log debug msg
  def log_err(msg: => String) = if (log.isErrorEnabled) log error msg
  def log_ftl(msg: => String) = if (log.isFatalEnabled) log fatal msg
  def log_inf(msg: => String) = if (log.isInfoEnabled) log info msg
  def log_trc(msg: => String) = if (log.isTraceEnabled) log trace msg
  def log_wrn(msg: => String) = if (log.isWarnEnabled) log warn msg
  /**
   * call the member log's debug function if debug is enabled. the message is generated only if
   * debug level is enabled
   * @param msg user provided message generated function. generates string to be logged when invoked
   * @param t exception related to the message. will be logged
   */
  def log_dbg(msg: => String, t: Throwable) = if (log.isDebugEnabled) log debug (msg, t)
  def log_err(msg: => String, t: Throwable) = if (log.isErrorEnabled) log error (msg, t)
  def log_ftl(msg: => String, t: Throwable) = if (log.isFatalEnabled) log fatal (msg, t)
  def log_inf(msg: => String, t: Throwable) = if (log.isInfoEnabled) log info (msg, t)
  def log_trc(msg: => String, t: Throwable) = if (log.isTraceEnabled) log trace (msg, t)
  def log_wrn(msg: => String, t: Throwable) = if (log.isWarnEnabled) log warn (msg, t)

  /**
   * syntactic candy for log level checks
   */
  def log_dbg = log.isDebugEnabled
  def log_err = log.isErrorEnabled
  def log_ftl = log.isFatalEnabled
  def log_inf = log.isInfoEnabled
  def log_trc = log.isTraceEnabled
  def log_wrn = log.isWarnEnabled

}
