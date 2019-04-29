package ik.util

import java.io.File

package object env {

  class env_err(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause)

  def env_bool(nme:String) = {
    val v = System.getenv(nme)
    if (v == null) false
    else if (v.equalsIgnoreCase("true"))true
    else if(v.equalsIgnoreCase("false"))false
    else if (v.equalsIgnoreCase("yes"))true
    else if(v.equalsIgnoreCase("no"))false
    else throw new env_err("expecting an environment variable that is converitble to boolean. i.e. true/false or yes/no.")
  }

  def env_str(nme:String) = {
    val v = System.getenv(nme)
    if (v == null) ""
    else v
  }

  def env_fle(nme:String) = {
    val v = System.getenv(nme)
    if (v == null) None
    else {
      val f = new File(v)
      if(f.exists()) Some(f)
      else throw new env_err(v + " does not exist or is not accessible.")
    }
  }

}
