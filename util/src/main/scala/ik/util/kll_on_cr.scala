package ik.util

import ik.util.log.loggd

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object kill_on_cr {

  def apply() = {
    Future {
      scala.io.StdIn.readLine()
    }.onComplete { x =>
      println(" +-----------------------------+")
      println(" | <cr> pressed - terminating  |")
      println(" +-----------------------------+")
      System.exit(0)
    }
  }
}

/**
  * expand. write a class that matches a string and executes a monad when matched
  * { map: string -> monad
  * def : add (pattern, monad)
  * def: ? - list
  */
object cnsl_cmnd extends loggd {

  type ftyp = () => Unit
  private val cmnds = new scala.collection.mutable.HashMap[String, ftyp]
  private var ext_str = "exit"

  def +=(pttrn: String, f: ftyp) = cmnds += (pttrn.trim -> f)

  def -=(pttrn: String) = cmnds.remove(pttrn.trim)

  def apply(exit_on_cr: Boolean = true) = {
    Future {
      while (true) {
        val ln = scala.io.StdIn.readLine().trim
        if (ln == "" && exit_on_cr) System.exit(0)
        else {
          cmnds.get(ln) match {
            case Some(foo) => foo()
            case None => log_err("unknown command: " + ln)
          }
        }
      }
    }
  }

//    /**
//      * unit test
//      */
//    def main(argv: Array[String]): Unit = {
//      apply()
//      +=("aa", () => {
//        println("aaaaaaaa")
//      })
//      +=("bb", () => {
//        println("bbbbbbbb")
//      })
//
//      Thread.sleep(10000000l)
//    }
}

