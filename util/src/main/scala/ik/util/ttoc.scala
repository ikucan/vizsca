package ik.util

/**
 * tic-toc wrapper ...
 * DSL-ise time tracking 
 */
class ttoc {
  var t0 = System.currentTimeMillis

  def tic = t0 = System.currentTimeMillis
  def tic(s: String) = {
    println(s)
    t0 = System.currentTimeMillis
  }

  def exp = System.currentTimeMillis - t0
  def exps = exp.toDouble / 1000.0

  def toc: Unit = toc("")
  def toc(s: String) = println(s + " :>  " + (System.currentTimeMillis - t0))
  def tocs: Unit = tocs("")
  def tocs(s: String) = println(s + " :>  " + ((System.currentTimeMillis - t0).toDouble / 1000.0))
  def toc_tic: Unit = toc_tic("")
  def toc_tic(s: String) = { println(s + " :>  " + (System.currentTimeMillis - t0)); tic }
  def tocs_tic: Unit = tocs_tic("")
  def tocs_tic(s: String) = { println(s + " :>  " + ((System.currentTimeMillis - t0).toDouble / 1000.0)); tic }
}

object ttoc {
  var tt = new ttoc
  def exp = tt.exp
  def exps = tt.exps
  def tic = tt.tic
  def toc = tt.toc
  def tocs = tt.tocs
  def tic(s: String) = tt.tic(s)
  def toc(s: String) = tt.toc(s)
  def tocs(s: String) = tt.tocs(s)
}

