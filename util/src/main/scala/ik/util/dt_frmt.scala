package ik.util

import java.text.SimpleDateFormat

/**
 * date formatter
 */
object dt_frmt {
  lazy val df = new SimpleDateFormat("yyyy.MM.dd")
  lazy val ddf = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss.SSS")
}