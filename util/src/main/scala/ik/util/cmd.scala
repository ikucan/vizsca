package ik.util

import java.util.concurrent.Executors

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
 * helper - execute some command 
 */
object cmd {

  implicit val ec = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool)

  def exec(cmd_str: String) = nny(Runtime.getRuntime().exec(cmd_str))
  def exec(cmd_str: String, args: Array[String]) = nny(Runtime.getRuntime().exec(cmd_str, args))
  def exec(cmd: Array[String]) = nny(Runtime.getRuntime().exec(cmd))

  /** 
   *  nanny a process. strap a command to a stdout and std err and wait for completion
   */
  def nny(cmd: Process) = {
    val (is, es) = (cmd.getInputStream, cmd.getErrorStream)
    val f1 = Future { strms.drain(es, System.err) }
    val f2 = Future { strms.drain(is, System.out) }
    cmd.waitFor
    Await.result(f1, 1.hour)
    Await.result(f2, 1.hour)
    cmd.exitValue
  }
}
