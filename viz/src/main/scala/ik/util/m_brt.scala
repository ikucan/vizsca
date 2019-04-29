package ik.util

import scala.math._
import java.util.concurrent.Executors
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.concurrent.Future


object m_brt {

  implicit val ec = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool)

  def zsqr(z: (Double, Double)) = (z._1 * z._1 - z._2 * z._2, 2 * z._1 * z._2)
  def zadd(a: (Double, Double), b: (Double, Double)) = (a._1 + b._1, a._2 + b._2)
  def zabs(z: (Double, Double)) = sqrt(z._1 * z._1 + z._2 * z._2)
  def zabssq(z: (Double, Double)) = z._1 * z._1 + z._2 * z._2

  def gen(xdim: Int, ydim: Int, x0: Double = -2.8, x1: Double = 1.5, y0: Double = -.3, y1: Double = 2.3, niter: Int = 30, bse_clr: Int = 0x552211) = {
    val xrng = abs(x1 - x0)
    val yrng = abs(y1 - y0)
    val dx = xrng / xdim
    val dy = yrng / ydim
    val xoff = min(x0, x1)
    val yoff = max(y0, y1)

    //var rv = (0 until ydim).toArray map { y => future { (0 until xdim).toArray map { x => if (y % 10 == 0) 0x003388 else 0xaaaa33 } } } map (_())

    var rv = (0 until ydim).toArray map { y =>
      Future {
        (0 until xdim).toArray map { x =>
          val c = (xoff + x * dx, yoff - y * dy)
          var z = (.0, .0)
          var i = 0
          while ((i < niter) && (zabssq(z) < 4.0)) {
            z = zadd(zsqr(z), c)
            i += 1
          }
          if (i == niter) bse_clr
          else bse_clr + i + (i << 8)
        }
      }
    } map (Await.result(_, 1 hour))

    rv
  }
}