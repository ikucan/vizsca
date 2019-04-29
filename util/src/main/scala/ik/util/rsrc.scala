package ik.util

import java.io._

/**
 * find a resource file in the following order:
 * 1. search the classpath
 * 2. treat as a relative path in current directory
 * 3. treat as an absolute path on the filesystem
 */
object rsrc {

  def apply(nm: String) = {
    if (nm.size == 0) throw new util_err("can't deal with empty resource name")
    val conf = getClass.getResourceAsStream(nm)
    if (conf != null) strms.txt(conf)
    else if (new File(System.getProperty("user.dir") + "/" + nm).exists)
      strms.txt(new File(System.getProperty("user.dir") + "/" + nm))
    else if (new File(nm).exists)
      strms.txt(new File(nm))
    else throw new util_err("resource could not be found in the classpath, working directory or on the file-system: " + nm)
  }
}
