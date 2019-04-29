package ik.util.df

/**
 * package exception definition
 */
class df_err(msg: String, cause: Throwable) extends RuntimeException(msg, cause) {
  def this(msg: String) = this(msg, null)
}