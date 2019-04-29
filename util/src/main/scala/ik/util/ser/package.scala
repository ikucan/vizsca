package ik.util

import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.io.FileInputStream
import java.io.ObjectInputStream

package object ser {

  /**
   * save an object into a file
   */
  def sv[T](o: T, f: String) = {
    val os = new ObjectOutputStream(new FileOutputStream(f))
    os.writeObject(o)
    os.close
  }

  /**
   * read obj from fle
   */
  def rd[T](f: String): T = {
    val is = new ObjectInputStream(new FileInputStream(f))
    val obj = is.readObject.asInstanceOf[T]
    is.close
    obj
  }

}